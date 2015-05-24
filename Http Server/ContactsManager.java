
import java.io.*;
import java.util.ArrayList;

/**
 * Created by Werner on 3/7/2015.
 */
public class ContactsManager {
    private static String FILE_NAME = "data.txt";
    public synchronized static String readFile()
    {
        String content = null;
        File file = new File(FILE_NAME); //for ex foo.txt
        try {
            FileReader reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public synchronized static void writeFile(String data){
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(FILE_NAME), "utf-8"));
            writer.write(data);
        } catch (IOException ex) {
            // report
        } finally {
            try {writer.close();} catch (Exception ex) {}
        }
    }

    //CRUD operations
    public static boolean deleteEntry(int id){
        String [] data = readEntries();
        try{
            String entry = getEntry(data,id);
            if(entry == null) return false;
            String newData = "";
            for(String s : data){
                if(entry.equals(s)) continue;
                newData+="\n"+s;
            }
            if(newData.equals("")) writeFile("");
            else
                writeFile(newData.substring(1,newData.length()));
            return true;
        }catch(Exception e){
            return false;
        }

    }

    public static boolean createEntry(String name,String surname,String number){
        try{
            String data = readFile();
            if(data.equals("")) writeFile(name+"/"+surname+"/"+number);
            else
                writeFile(data+"\n"+name+"/"+surname+"/"+number);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public static boolean updateEntry(int id, String name,String surname,String number){
        String [] data = readEntries();
        try{
            String entry = getEntry(data,id);
            if(entry == null) return false;
            String newData = "";
            for(String s : data){
                if(!s.equals(data[0])) newData+="\n";
                if(entry.equals(s)) newData+= name+"/"+surname+"/"+number;
                else newData+=s;

            }
            writeFile(newData);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public static String [] search(String expression){
        if(expression.equals("*")) return readEntries();

        ArrayList<String> entries = new ArrayList<String>();
        String [] data = readEntries();
        for(String s : data){
            if(s.contains(expression)) entries.add(s);
        }
        String [] result = new String[entries.size()];
        result = entries.toArray(result);
        return result;
    }

    public static String[] readEntries(){
        return readFile().split("\n");
    }

    public static String getEntry(int id){
        try {
            String [] data = readEntries();
            return data[id];
        }
        catch(Exception e){
            return null;
        }
    }

    public static String getEntry(String [] data, int id){
        try {
            return data[id];
        }
        catch(Exception e){
            return null;
        }
    }
}
