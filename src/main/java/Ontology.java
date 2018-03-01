import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.neo4j.driver.v1.*;
import java.util.ArrayList;

public class Ontology {
    private String name;
    private ArrayList<FindPath> ontologyMatrix = new ArrayList<FindPath>();
    private ArrayList<String> terms = new ArrayList<String>();

    Ontology(){}

    Ontology(String name){
        this.name = name;

        StatementResult result = Connector.query("MATCH (a:"+this.name+"),(b:"+this.name+"),p=((a)-[*..1]->(b))\n " +
                "RETURN p ;");
        while ( result.hasNext() ) {
            Record record = result.next();
            FindPath newPath = new FindPath(record.toString(),this.name);
            if (!ontologyMatrix.contains(newPath)) ontologyMatrix.add(newPath);
        }

        for (FindPath p : ontologyMatrix)
            for (String s : p.get_Terms()){
                if (!terms.contains(s)) terms.add(s);
            }
        this.best_Paths();
    }

    public ArrayList<FindPath> get_ontologyMatrix(){
        return this.ontologyMatrix;
    }

    public ArrayList<String> get_terms(){
        return this.terms;
    }

    private void best_Paths (){
        ArrayList<FindPath> remove_List = new ArrayList<FindPath>();
        for (FindPath a : ontologyMatrix)
            for (FindPath b : ontologyMatrix)
                if (!a.equals(b)){
                    if (a.get_start().equals(b.get_start()) && a.get_end().equals(b.get_end())) { // два пути из a в b
                        if (!a.get_Relations().contains("RT") && b.get_Relations().contains("RT")) remove_List.add(b);
                        if (a.get_Relations().contains("RT") && !b.get_Relations().contains("RT")) remove_List.add(a);
                    }
                    if (a.get_start().equals(b.get_start()) ^ a.get_end().equals(b.get_end())) { // два пути из a или два пути в b
                        if (a.get_Relations().size() < b.get_Relations().size()) remove_List.add(b);
                        if (a.get_Relations().size() > b.get_Relations().size()) remove_List.add(a);
                        if (a.get_Relations().size() == b.get_Relations().size())
                        {
                            if (!a.get_Relations().contains("RT") && b.get_Relations().contains("RT")) remove_List.add(b);
                            if (a.get_Relations().contains("RT") && !b.get_Relations().contains("RT")) remove_List.add(a);
                        }
                    }
                }
        ontologyMatrix.removeAll(remove_List);
        remove_List.clear();
    }

    public String getOntoName(){
        return this.name;
    }

    public void setOntoName(String s){this.name = s;}

    public void showGraph(){
        for(FindPath p :ontologyMatrix)
            p.showPath();
    }
}
