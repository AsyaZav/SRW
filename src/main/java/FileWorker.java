import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class FileWorker {

    public static String parse_Definition (String s){
        String delStr;
        while (s.contains("|")){
            if (s.contains(" "))
                if (s.indexOf(" ") > s.indexOf("|"))
                    delStr = s.substring(s.indexOf("|") + 1, s.indexOf(" ", s.indexOf("|")));
                else
                if (s.contains(")"))
                    delStr = s.substring(s.indexOf("|") + 1, s.indexOf(")"));
                else delStr = s.substring(s.indexOf("|") + 1);
            else
                if (s.contains(")"))
                    delStr = s.substring(s.indexOf("|") + 1, s.indexOf(")"));
                else delStr = s.substring(s.indexOf("|") + 1);
            s = s.replaceAll("\\|" + delStr, "");
        }
        return s;
    }
    public static ArrayList<String> parse_Relation (String s){
        ArrayList<String> s_list = new ArrayList<String>();
        if (s.contains("(")) {
            s_list.add(s.substring(0, s.indexOf("(")));
            if (s.contains(")"))
                s = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
            else s = s.substring(s.indexOf("(") + 1);
            s_list.add(s);
        }
        else s_list.add(s);

        return s_list;
    }

    public static void write_TXT (Ontology o, File f){
        try{

            OutputStreamWriter writer = null;
            new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
            //PrintWriter writer = new PrintWriter(f, "UTF-8");
            //writer.println("Term_1,Relation,Term_2");
            writer.write("Term_1,Relation,Term_2" + "\r\n");
            for (FindPath p: o.get_ontologyMatrix())
                writer.write(p.toString() + "\r\n");
                //writer.println(p.toString());
            writer.flush();
            writer.close();
        }
        catch(IOException ex){System.out.println(ex.getMessage());}
    }

    public static void write_RDF (Ontology o, File f){
        String SOURCE = "http://www.eswc2006.org/technologies/ontology";
        String NS = SOURCE + "#";
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        OntModel m = ModelFactory.createOntologyModel();

        for (String s: o.get_terms())
            m.createClass( NS + s );

        ObjectProperty relation;
        for (FindPath p: o.get_ontologyMatrix())
        {
            relation = m.createObjectProperty( NS + p.get_Relations().get(0) );
            relation.addDomain(m.getResource(NS + p.get_start()));
            relation.addRange(m.getResource(NS + p.get_end()));
            relation.addLabel(NS + p.get_Relations().get(0), "ru");
        }

        m.write(System.out);

        Writer out;
        try {
            out = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");;
            m.write( out, "RDF/XML-ABBREV" );
        }catch (IOException ignore) {}
    }
    public static void read_RDF (File f){
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        OntModel m = ModelFactory.createOntologyModel();

        FileReader in;
        try {
            in = new FileReader( f );
            m.read( in, "RDF/XML-ABBREV" );
        }catch (IOException ignore) {}
        m.write(System.out);
    }

    public static File preparing_File (File before, String delimiter){
        //String delimiter = ";";
        FileReader in;
        BufferedWriter out;
        File result = null;

        try {
            in = new FileReader( before );
            result = new File(before.getAbsolutePath().replaceAll(".txt", "New.txt"));
            if (new File(before.getAbsolutePath().replaceAll(".txt", "New.txt")).exists())
                result.delete();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result), StandardCharsets.UTF_8));
            Scanner scan = new Scanner(in);
            String a, b , r, s;
            scan.nextLine();
            while (scan.hasNextLine()){
                s = scan.nextLine();
                a = parse_Definition(s.substring(0,s.indexOf(delimiter)));
                b = parse_Definition(s.substring(s.lastIndexOf(delimiter) + 1));
                r = parse_Definition(s.substring(s.indexOf(delimiter) + 1, s.lastIndexOf(delimiter)));
                out.write(a + delimiter +  r + delimiter + b + "\r\n");
                System.out.println(a + delimiter +  r + delimiter + b);
            }
            in.close();
            out.flush();
            out.close();

        }catch (IOException ignore) {}

        return result;
    }
}
