import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Trevor on 2015-05-24.
 */
public class GameServer{
    public MailChecker mailChecker;
    private class MailChecker extends Thread{
        private POP3Client pop;
        private ArrayList<MailHeader> headers;
        public ArrayList<GameHandler> clients;

        public MailChecker(){
            try {
                pop = new POP3Client(Main.pop3ServerName,Main.pop3Port);
                pop.login(Main.pop3Username,Main.pop3Password);
                clients = new ArrayList<GameHandler>();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }

        public void run(){
            while(true) {
                try {
                    pop.populateMailHeaders();
                    headers = pop.getMailList();
                    int count = 0;


                    sleep(1000 * 30);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }
    }

    private int port;


    public GameServer(int port) {
        this.port = port;
        mailChecker = new MailChecker();
        mailChecker.start();
    }

    public void run(){
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Game server is now running on port: " + port);


            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                Socket cSock = server.accept();
                GameClient client = new GameClient(cSock);
                new GameHandler(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
