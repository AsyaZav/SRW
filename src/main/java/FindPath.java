import java.util.ArrayList;

public class FindPath {
    private  String start = "";
    private  String end = "";
    private ArrayList<Integer> indexes = new ArrayList();
    private ArrayList<String> terms = new ArrayList<String>();
    private ArrayList<String> relations = new ArrayList<String>();

    FindPath(String a, String b, String r, int i_a, int i_b){
        this.start = a;
        this.end = b;
        this.terms.add(a);
        this.terms.add(b);
        this.relations.add(r);
        this.indexes.add(i_a);
        this.indexes.add(i_b);
    }

    FindPath(String s, String tes){

        int i = s.indexOf("(");
        //System.out.println(s);
        while (i != s.lastIndexOf(")")){
            String str = s.substring(s.indexOf("(",i)+1,s.indexOf(")",i + 1));
            if (indexes.contains(Integer.parseInt(str)) == false)
            {
                this.indexes.add(Integer.parseInt(str));
                this.terms.add(Connector.getNode(str, tes));
            }
            i = s.indexOf(")",i + 1);
        }

        this.start = terms.get(0);
        end = terms.get(indexes.size() - 1);
        i = s.indexOf(":");
        while (i <= s.lastIndexOf(":")){
            i = s.indexOf(":",i + 1 );
            this.relations.add(s.substring(i+1,s.indexOf("]",i)));
            i = s.indexOf("]",i + 2);
        }
    }
    public ArrayList<String> get_Terms (){
        return this.terms;
    }
    public ArrayList<String> get_Relations (){
        return this.relations;
    }

    public void showPath (){
        System.out.println("start: " + this.start + " end: " + this.end + " indexes: " + this.indexes + " relations: " + this.relations + " terms: " + this.terms);
    }
    @Override
    public boolean equals(Object a) {
        if (((FindPath) a).indexes.equals(this.indexes)) return true;
        else return false;
    }

    public String get_start(){
        return this.start;
    }
    public String get_end(){
        return this.end;
    }

    @Override
    public String toString(){
        return this.start + "," + this.relations.toString().substring(1,this.relations.toString().length() - 1) + "," + this.end;
    }
}