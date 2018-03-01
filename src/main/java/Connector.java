import org.neo4j.driver.v1.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Connector {

    public static StatementResult query (String s){

        Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "asya_zav@mail.ru", "ujkbwsyf" ) );
        Session session = driver.session();
        StatementResult result = session.run(s);
        session.close();
        driver.close();

        return result;
    }

    public static String getNode (String s, String tes){                //получить понятия по списку их индексов

        StatementResult result = query("MATCH (n:"+tes+")\n" +
                "WHERE  id(n) IN ["+s+"]\n" +
                "RETURN n.title AS title" );

        String str = "";
        while ( result.hasNext() )
        {
            Record record = result.next();
            str = str + "," + record.get("title").asString();
        }
        return str.substring(1,str.length());
    }

    public static ArrayList<String> getDefinitions (String label){        //получить все понятия

        StatementResult result = query("MATCH (n:"+label+")\n" +
                "RETURN n.title AS title" );

        ArrayList<String> str = new ArrayList<String>();
        while ( result.hasNext() )
        {
            Record record = result.next();
            str.add(record.get("title").asString());
        }
        return str;
    }

    public static ArrayList<String> getRelations (String label){            //названия существующих связей в графе

        StatementResult result = query("MATCH (n:"+label+")-[r]->(x)\n" +
                "RETURN type(r) AS title" );

        ArrayList<String> str = new ArrayList<String>();
        while ( result.hasNext() )
        {
            Record record = result.next();
            if (!str.contains(record.get("title").asString()))
                str.add(record.get("title").asString());
        }
        return str;
    }

    public static Ontology getOntology (String label){
        Ontology onto = new Ontology();
        onto.setOntoName(label);
        onto.get_terms().addAll(getDefinitions(label));
        StatementResult result = query("MATCH (n:"+label+")-[r]->(x)\n" +
                "RETURN type(r) AS relation, n.title AS a, x.title AS b, id(n) AS i_a, id(x) AS i_b" );
        while ( result.hasNext() )
        {
            Record record = result.next();
            onto.get_ontologyMatrix().add(new FindPath(record.get("a").asString(),record.get("b").asString(),
                    record.get("relation").asString(),record.get("i_a").asInt(),record.get("i_b").asInt()));
        }

        //onto.showGraph();
        return onto;
    }

    public static ArrayList<FindPath> allShortestPath (String a, String b, String tes){
        ArrayList<FindPath> findPaths = new ArrayList<FindPath>();
        StatementResult result = query("MATCH (a:"+tes+" { title: \""+a+"\" }),(b:"+tes+" { title: \""+b+"\" }), p=allShortestPaths((a)-[:NT|BT|RT*]->(b))\n" +
                "WITH p, filter(x IN relationships(p) WHERE type(x) = \"RT\") AS rtRels\n" +
                "WHERE size(rtRels) < 2\n" +
                "RETURN p;");

        while ( result.hasNext() ) {
            Record record = result.next();
            FindPath newPath = new FindPath(record.toString(),tes);
            if (!findPaths.contains(newPath)) findPaths.add(newPath);
        }
        return findPaths;
    }

    public static void loadTXT(File file, String delimiter){
        String nameBD = file.getName().substring(0,file.getName().indexOf(".")), a, b, r;
        //String delimiter = ";";
        file = FileWorker.preparing_File(file, delimiter);

        try(FileReader reader = new FileReader(file))
        {
            Scanner scan = new Scanner(reader);
            if (scan.hasNextLine()) scan.nextLine();
            while (scan.hasNextLine()) {
                String s = scan.nextLine();
                a = s.substring(0,s.indexOf(delimiter));
                a = a.toUpperCase();
                r = s.substring(s.indexOf(delimiter) + 1, s.lastIndexOf(delimiter));
                r = r.toUpperCase();
                b = s.substring(s.lastIndexOf(delimiter) + 1);
                b = b.toUpperCase();

                r = FileWorker.parse_Definition(r);
                ArrayList<String> r_list = FileWorker.parse_Relation(r);
                a = FileWorker.parse_Definition(a);
                b = FileWorker.parse_Definition(b);

                //System.out.println(a + " " + r + " " + b);

                for (String str_r: r_list) {
                    str_r = str_r.replace(' ','_');
                            Connector.query("MERGE (a:" + nameBD + " {title: \"" + a + "\"})\n" +
                                    "MERGE (c:" + nameBD + " {title: \"" + b + "\"})\n" +
                                    "MERGE (a)-[:" + str_r + "]->(c);");
                }
            }
            reader.close();
        }catch(IOException ex){System.out.println(ex.getMessage());}
    }

    public static void deleteGraph (String name){
        //Connector.query("DROP CONSTRAINT ON (a:" + name + ") ASSERT a.ID IS UNIQUE");
        Connector.query("MATCH (a:" + name + ")-[r]->(b:" + name + ")\n" +
                "DELETE r;");
        Connector.query("MATCH (a:" + name + ")\n" +
                "DELETE a;");
        //Connector.query("DROP CONSTRAINT ON (a:" + name + ") ASSERT a.title IS UNIQUE");

    }

    public static void create_Onto (FindPath p, String nameBD){
        for (int i = 0; i < p.get_Relations().size(); i++){
            //Connector.query("CREATE CONSTRAINT ON (a:" + nameBD + ") ASSERT a.title IS UNIQUE");
            Connector.query("MERGE (a:" + nameBD + " {title: \"" + p.get_Terms().get(i) + "\"})\n" +
                    "MERGE (c:" + nameBD + " {title: \"" + p.get_Terms().get(i+1) + "\"})\n" +
                    "MERGE (a)-[:" + p.get_Relations().get(i) + "]->(c);"  );
        }

    }

    public static ArrayList<String> getLabels (){
        ArrayList<String> labels = new ArrayList<>();
        StatementResult result = Connector.query("MATCH (n)\n" +
                "WITH DISTINCT labels(n) AS labels\n" +
                "UNWIND labels AS label\n" +
                "RETURN DISTINCT label\n" +
                "ORDER BY label;");
        while ( result.hasNext() )
        {
            Record record = result.next();
            labels.add(record.get("label").asString());
        }

        return labels;
    }

}

