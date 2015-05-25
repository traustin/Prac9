import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by wernermostert on 2015/04/25.
 */
public class POP3Client {

    private BufferedReader in;
    private PrintWriter out;
    private Socket connection;
    private final String ADDRESS;
    private final int PORT;
    private String username, password;
    private final int TIMEOUT = 10000;//in milliseconds

    private ArrayList<MailHeader> mailList;

    public POP3Client(String address, int port) throws IOException, NotPOP3ServerException {
        mailList = new ArrayList<MailHeader>();        
        ADDRESS = address;
        PORT = port;
        initConnection();         
    }

    public void reconnect() throws IOException{
        try {
            closeConnection();
            initConnection();
            login();
        } catch (NotPOP3ServerException e) {
            //catch silently since server type has already been verified
            //  during initial login
        }        
    }  

    public void login(String _username, String _password) throws IOException, 
            IncorrectUsernameException, IncorrectPasswordException {
        username = _username;
        password = _password;
        out.println("USER " + _username);
        String response = in.readLine();
        if (!response.matches("[+]OK.*")) {
            throw new IncorrectUsernameException();
        }

        out.println("PASS " + _password);
        response = in.readLine();
        if (!response.matches("[+]OK.*")) {
           // throw new IncorrectPasswordException();
        }
    }
    
    public ArrayList<MailHeader> getMailList() {
        return mailList;
    }

    /**
     * Instruct server to mark message as deleted
     * 
     * @param i index
     * @return false if no such unmarked message exists, true otherwise
     * @throws IOException 
     */
    public boolean markToDelete(int i) throws IOException {
        if (mailList.get(i) != null) {
            out.println("DELE " + (mailList.get(i).indexOnServer));
            mailList.remove(i);
            if (in.readLine().matches("[+]OK .*")) {
                return true;
            }
        }
        return false;
    }

    public String getContent(int messageNo) throws IOException{
        String result = "";
        out.println("RETR "+messageNo);
        String line = in.readLine();
        boolean content = false;
        while(!line.equals(".")){

            if(content)
                result += line+"\n";
           // System.out.print(line);
            if(line.equals("Content-Type: text/plain; charset=UTF-8"))
                content = true;

            line = in.readLine();
        }
        result = result.substring(result.indexOf("\n"));
        result = result.substring(0,result.indexOf("--"));
        result = result.trim();
        return result;
    }

    public void unmarkToDelete() throws IOException{
        out.println("RSET");//the only possible response is +OK
        in.readLine();
    }

    public void closeConnection() throws IOException {
        out.println("QUIT");
        if (connection != null) {
            connection.close();
        }
    }

    public void populateMailHeaders() throws IOException {
        mailList = new ArrayList<MailHeader>();
        out.println("LIST");
        String resp = in.readLine();
        String[] parts = resp.split(" ");

        int numberOfMessages = new Integer(parts[1]);
        for (int i = 0; i < numberOfMessages; i++) {
            mailList.add(new MailHeader(new Integer(in.readLine().split(" ")[1])));
        }

        in.readLine();  //read the .
        for (int i = 1; i <= numberOfMessages; i++) {
            out.println("TOP " + i + " 0");
            String line = in.readLine();
            mailList.get(i-1).indexOnServer = i;
            
            while (!line.equals(".")) {
                line = in.readLine();
                if (line.matches("Subject[:].*")) {
                    mailList.get(i - 1).subject = (line.substring(line.indexOf(":") + 1).trim());                    
                }
                if (line.matches(("From[:].*"))) {
                    mailList.get(i - 1).sender = (line.substring(line.indexOf(":") + 1).trim());
                }
            }

        }

    }
    
    /**
     * Used to sign in with credentials already authenticated during
     * initial login 
     * 
     */
    private void login(){
        try {
            out.println("USER " + username);
            String response = in.readLine();
            out.println("PASS " + password);        
            response = in.readLine();
        } catch (IOException e) {}
    }
    
    private void initConnection() throws IOException, NotPOP3ServerException{
        connection = new Socket(ADDRESS, PORT);
        connection.setSoTimeout(TIMEOUT);//set timeout for blocking operations
        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        out = new PrintWriter(connection.getOutputStream(), true);

        if (!in.readLine().matches("[+]OK .*")) {
            throw new NotPOP3ServerException();
        }
    }

}
