import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by Trevor on 2015-05-24.
 */
public class GameHandler extends Thread {
    private BufferedReader in;
    private PrintWriter out;
    private GameClient toClient;
    private String email;
    private WordGenerator wordGenerator;
    private SMTPClient mailer;

    public GameHandler(GameClient toClient) {
        this.toClient = toClient;
        wordGenerator = new WordGenerator("words.txt");
        mailer = new SMTPClient(Main.smtpServer,Main.smtpPort);
    }

    public void sendToClient(String message){
        out.println(message);
    }

    public void run(){
        System.out.println("Client connected");
        try {
            in = new BufferedReader(new InputStreamReader(toClient.getSocket().getInputStream()));
            out = new PrintWriter(toClient.getSocket().getOutputStream(), true);
            email = in.readLine();
            email = email.substring(email.indexOf(" ") + 1);
            System.out.println("Email: " + email);
            String word = wordGenerator.getWord();
            System.out.println(word);
            String shownWord = "";
            for(int i = 0; i <  word.length(); i++){
                shownWord += "_";
            }
            out.println(shownWord);
            boolean done = false;

            while(!done){
                System.out.println(email);
                if(!email.equals("")) GameServer.clients.add(this);
                String s = in.readLine().trim();
                System.out.println(s);
                if(s.length()==3&&s.equals("BYE")){
                    System.out.println("Client disconnected");
                    GameServer.clients.remove(this);
                    break;
                }else if(s.length()==5&&s.equals("RESET")){
                    out.println("The word was : "+word);
                    word = wordGenerator.getWord();
                    shownWord = "";
                    for(int i = 0; i <  word.length(); i++){
                        shownWord += "_";
                    }
                    out.println(shownWord);
                }else if(s.length()>4 && s.substring(0,4).equals("TRY ")){
                    char letter = s.charAt(4);
                    boolean changed = false;
                    for(int i = 0; i < word.length(); i++){
                        if(letter ==  word.charAt(i)){
                            shownWord = shownWord.substring(0,i) + letter + shownWord.substring(i+1);
                            changed = true;
                        }
                    }
                    out.println(letter + " : " + shownWord);
                    if(word.equals(shownWord)){
                        out.println("RESULT " + changed);
                        mailer.send(email, "DONOTREPLY@3DHangman.com");
                    }else if(changed == false){
                        out.println("RESULT " + changed);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
