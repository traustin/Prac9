import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Trevor on 2015-05-25.
 */
public class SMTPClient {
    String server;
    int port;

    public SMTPClient(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void send(String to, String from){
        Socket socket = null;
        try {
            socket = new Socket(server, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println(in.readLine());
            out.println("HELO " + server);
            System.out.println(in.readLine());
            out.println("MAIL FROM: <" + from + ">");
            System.out.println(in.readLine());
            out.println("RCPT TO: <" + to + ">");
            System.out.println(in.readLine());
            out.println("DATA");
            System.out.println(in.readLine());
            out.println("Subject: Your 3D Hangman results");
            out.println("Congratulations you won your game of 3DHangman!");
            out.println(".");
            out.println("QUIT");
            System.out.println(in.readLine());
            // clean up
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }

    }
}
