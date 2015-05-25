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
    public static ArrayList<GameHandler> clients = new ArrayList<GameHandler>();
    public MailChecker mailChecker;
    private class MailChecker extends Thread{
        private POP3Client pop;
        private ArrayList<MailHeader> headers;

        public ArrayList<String> shownMessages;

        synchronized public void notifyClients(){
            for(GameHandler g : clients){
                int i = 0;
                for(String message : shownMessages)
                    g.sendToClient("MSG "+(i++)+" "+message);
            }
        }

        public MailChecker(){
            try {
                clients = new ArrayList<GameHandler>();
                shownMessages = new ArrayList<String>();
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }

        public void run(){
            while(true) {
                try {
                    pop = new POP3Client(Main.pop3ServerName,Main.pop3Port);
                    pop.login(Main.pop3Username,Main.pop3Password);
                    pop.populateMailHeaders();
                    headers = pop.getMailList();
                    shownMessages = new ArrayList<String>();

                    int gameMessages = 0;
                    int messageNo = headers.size()-1;
                    int maxMessages = 5;
                    while(gameMessages < maxMessages && messageNo < headers.size()){
                        if(headers.get(messageNo).subject.equals("3DHANGMAN")){
                            String message = pop.getContent(headers.get(messageNo).indexOnServer);
                            if(!shownMessages.contains(message)){
                                shownMessages.add(headers.get(messageNo).sender.substring(0,headers.get(messageNo).sender.indexOf("<")-1)+" : "+message);
                                if(gameMessages >= maxMessages){
                                    shownMessages.remove(0);
                                }
                            }
                            gameMessages++;
                        }
                        messageNo--;
                    }
                    notifyClients();
                    sleep(1000 * 30);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        sleep(1000 * 60);
                    }catch (Exception ex){

                    }
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
