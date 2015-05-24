
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Werner on 3/2/2015.
 */
public class WebServer {

    Router route;
    private int port;
    public WebServer(int p){
        route = null;
        port = p;
    }

    public static String readFile(String f_name)
    {
        String content = null;
        File file = new File(f_name); //for ex foo.txt
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

    protected void start() {
        ServerSocket s;

        System.out.println("Web Server starting up on port "+port);
        try {
            s = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }
        System.out.println("Server ready");


        while(true) {
            try {

                Socket remote = s.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        remote.getInputStream()));
                PrintWriter out = new PrintWriter(remote.getOutputStream());

                ArrayList<String> res = new ArrayList<String>();
                res.add("contacts/create");
                res.add("contacts/update");
                res.add("contacts/delete");
                res.add("contacts/search");
                route = new Router(out,res);
                String str = "";

                str = in.readLine();

                if(str!= null)
                route.accept(str);

//                out.println("HTTP/1.0 200 OK");
//                out.println("Content-Type: text/html");
//                out.println("Server: Bot");
//
//                out.println("");
//
//                out.println(readFile("index.html"));
//                out.flush();

                remote.close();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

}
