import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Operations {

    static Ontology res_Onto;

    public static Map intersection_Onto_Tes(Ontology onto, String tes) {

        String terms = "";
        Map<Integer, String> tes_terms = new HashMap<Integer, String>();

        for (String s : onto.get_terms()) terms = terms + "\"" + s + "\",";
        terms = terms.substring(0, terms.length() - 1);
        StatementResult result = Connector.query("MATCH (a:" + tes + ")\n" +
                "WHERE a.title IN [" + terms + "]\n" +
                "RETURN id(a) AS id, a.title AS title");
        while (result.hasNext()) {
            Record record = result.next();
            tes_terms.put(record.get("id").asInt(), record.get("title").asString());
        }
        return tes_terms;
    }

    public static void intersection_Ontologies(Ontology a, Ontology b, String tes, String newOnto, boolean t) {
        Map onto1 = intersection_Onto_Tes(a, tes);
        Map onto2 = intersection_Onto_Tes(b, tes);

        ArrayList<String> sameTerms = new ArrayList(); //содержит понятия, одинаковые для онтологий a и b
        sameTerms.addAll(a.get_terms());
        sameTerms.retainAll(b.get_terms());


        if (onto1 == null || onto2 == null) System.out.println("нет теоретико множественного пересечения с тезауросом");
        else {
            ArrayList<FindPath> findPaths = new ArrayList<FindPath>();

            for (Object i : onto1.values()) {
                for (Object j : onto2.values()) {
                    if (!sameTerms.contains((String) i) && !sameTerms.contains((String) j))
                        findPaths.addAll(Connector.allShortestPath((String) i, (String) j, tes));
                }
            }
            res_Onto = new Ontology();
            res_Onto.get_terms().addAll(sameTerms);

            //Connector.query("CREATE CONSTRAINT ON (n:" + newOnto + ") ASSERT n.title IS UNIQUE;");

            for (FindPath p : a.get_ontologyMatrix()) //восстанавливаем связи между понятиями онтологии а
                if (sameTerms.contains(p.get_start()) && sameTerms.contains(p.get_end())) {
                    add_Element(p);
                }

            for (FindPath p : b.get_ontologyMatrix()) //восстанавливаем связи между понятиями онтологии b
                if (sameTerms.contains(p.get_start()) && sameTerms.contains(p.get_end())) {
                    add_Element(p);
                }

            for (FindPath f : findPaths) {
                for (FindPath p : b.get_ontologyMatrix())
                    if (sameTerms.contains(p.get_start()) && (p.get_end().equals(f.get_start()) || p.get_end().equals(f.get_end()) || sameTerms.contains(p.get_end()))) {
                        add_Element(p);
                    }
                for (FindPath p : a.get_ontologyMatrix())
                    if (sameTerms.contains(p.get_start()) && (p.get_end().equals(f.get_start()) || p.get_end().equals(f.get_end()) || sameTerms.contains(p.get_end()))) {
                        add_Element(p);
                    }
            }
            for (FindPath p : findPaths) {
                add_Element(p);
            }
            if (t) {
                for (FindPath p : res_Onto.get_ontologyMatrix())
                    Connector.create_Onto(p, newOnto);
                /*File f = new File("C:\\Users\\Настик\\Documents\\test\\" + newOnto + ".txt");
                FileWorker.write_TXT(res_Onto, f);
                Connector.loadTXT(f);*/
            }

            for (String st : sameTerms) { //добавляем одинаковые термины (могут быть отдельными КС)
                Connector.query("MERGE (a:" + newOnto + " {title: \"" + st + "\"})\n");
            }
        }
    }

    public static void union_Ontologies(Ontology a, Ontology b, String tsrs, String nwnt) {
        intersection_Ontologies(a, b, tsrs, nwnt, false);
        for (FindPath p : a.get_ontologyMatrix()) {
            add_Element(p);
        }

        for (FindPath p : b.get_ontologyMatrix()) {
            add_Element(p);
        }
        for (FindPath p : res_Onto.get_ontologyMatrix())
            Connector.create_Onto(p, nwnt);
        /*File f = new File("C:\\Users\\Настик\\Documents\\test\\" + nwnt + ".txt");
        FileWorker.write_TXT(res_Onto, f);
        Connector.loadTXT(f);*/
    }

    public static void add_Element(FindPath p) {

        for (String s : p.get_Terms())
            if (!res_Onto.get_terms().contains(s)) res_Onto.get_terms().add(s);
        if (!res_Onto.get_ontologyMatrix().contains(p)) res_Onto.get_ontologyMatrix().add(p);
    }


    public static void main(String[] args) {
        new Frame();

    }
}
