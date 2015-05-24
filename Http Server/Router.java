
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Werner on 3/7/2015.
 */
public class Router {
    private PrintWriter out;
    private ContactsManager contacts;

    ArrayList<String> allowedResources;
    public Router(PrintWriter out){
        this.out = out;
        allowedResources = new ArrayList<String>();
        contacts = new ContactsManager();
    }

    public Router(PrintWriter out, ArrayList<String> resource_list){
        allowedResources = resource_list;
        this.out = out;
        contacts = new ContactsManager();
    }


    private String arrayToHTML(String [] data,int id){
        String html = "";
            html += "<tr><td>"+id+"</td>";

            for(int j = 0; j<3;j++){
                html += "<td>"+data[j]+"</td>";
            }
             html += "</tr>";

        return html;
    }

    private String insertData(String response,String [] data){
        int i = response.indexOf("<!--~data~-->");

        int k = i + new String("<!--~data~-->").length();
        String end = response.substring(k,response.length());
        response = response.substring(0,i);

        int id = -1;
        for(String s : data){
            id++;
            response += arrayToHTML(s.split("/"),id);
        }
        return response+end;

    }



    public void accept(String request){
        String response = WebServer.readFile("index.html");
        System.out.println("======= NEW REQUEST ========");
        System.out.println(request);
        String resource = getResource(request);
        Hashtable<String,String> queryVariables = parseQueryString(request);
       System.out.println(resource);
       System.out.println(queryVariables.toString());
        if(resource.equals("")){
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html");
            out.println("");
            String [] allContacts = contacts.readEntries();

            out.println(insertData(response,allContacts));



        }
        else{
            if(allowedResources.contains(resource)){
                out.println("HTTP/1.0 200 OK");
                out.println("Content-Type: text/html");
                out.println("");

                if(resource.equals("contacts/create")){
                   if( contacts.createEntry(queryVariables.get("name"),queryVariables.get("surname"),queryVariables.get("number")))
                   {
                       String [] allContacts = contacts.readEntries();
                       out.println(insertData(response,allContacts));
                   }else{
                        out.println(WebServer.readFile("error.html"));
                   }
                }else if(resource.equals("contacts/delete")){
                    if( contacts.deleteEntry(new Integer(queryVariables.get("id"))))
                    {
                        String [] allContacts = contacts.readEntries();
                        out.println(insertData(response,allContacts));
                    }else{
                        out.println(WebServer.readFile("error.html"));
                    }
                }else if(resource.equals("contacts/update")) {
                    if (contacts.updateEntry(new Integer(queryVariables.get("id")), queryVariables.get("name"), queryVariables.get("surname"), queryVariables.get("number"))) {
                        String [] allContacts = contacts.readEntries();
                        out.println(insertData(response,allContacts));
                    } else {
                        out.println(WebServer.readFile("error.html"));
                    }
                }else if(resource.equals("contacts/search")){
                        String [] searchContacts = contacts.search(queryVariables.get("expression"));
                        out.println(insertData(response,searchContacts));
                }



            }
            else{
                out.println("HTTP/1.0 404 Not Found");
                out.println("Content-Type: text/html");
                out.println("");
                out.println(WebServer.readFile("not_found.html"));

            }
        }

        out.flush();
        System.out.println("======= END REQUEST =====");
    }

    private String getResource(String request){
        if(request.indexOf('?') == -1)
        return request.substring(5,request.length()-9);
        else
            return request.substring(5,request.indexOf('?'));
    }

    private Hashtable<String,String> parseQueryString(String request){
       Hashtable<String,String> result = new Hashtable<String, String>();

        if(request.indexOf('?') == -1) return result;
        String queryString = request.substring(request.indexOf('?')+1,request.length()-9);
        if(queryString.charAt(queryString.length()-1) == '=') queryString = queryString +"*";
        String [] values = queryString.split("&");
        for(String s: values){
            String [] temp =s.split("=");
            result.put(temp[0],temp[1]);
        }
        return result;
    }
}
