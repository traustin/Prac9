

//Werner Mostert
//13019695
//University of Pretoria COS 216

import java.io.*;
import java.net.*;


public class Main{

    private static LinkedList clients = new LinkedList();
    private static String FILE_NAME = "data.txt";

    public static void main(String[] args) throws IOException {
        int port = 23;

        if (args.length > 0)
            port = Integer.parseInt(args[0]);

        ServerSocket listener = new ServerSocket(port);
        System.out.println("Server running on port "+port+" at "+listener.getInetAddress());
        new ServerInputReader().start();

        while (true) {
            Socket client = listener.accept();
            Client newClient = new Client(client.getInetAddress().toString(),client);
            new ClientHandler(newClient).start();
            System.out.println("New client from "+ newClient.name+
                    " on client's port "+client.getPort());

            clients.add(newClient);

        }
    }

    //client operations
    static synchronized void broadcast(String message)
            throws IOException {
        // Sends the message to every client including the sender.
        Socket socket;
        PrintWriter printer;

        Client [] clientList = clients.getAll();
        for (Client c : clientList) {
            socket = c.socket;
            printer = new PrintWriter(socket.getOutputStream(),true);
            printer.println("SERVER: "+message);
        }

    }

    static synchronized void remove(Client s) {
        try{
            s.socket.close();
            System.out.println(s.name+ " disconnected.");
            clients.remove(s);

        }
        catch(Exception e){
            System.out.println("Error closing socket: " + e.getMessage() + " with client " + s.name);
        }
    }

//    static synchronized void send(String message, String name, String to) throws IOException {
//        Socket socket;
//        PrintWriter printer;
//        Client cl = clients.getByName(to);
//        if(cl != null){ //found
//            socket = cl.socket;
//            printer  = new PrintWriter(socket.getOutputStream(),true);
//            printer.println(name+" to You:"+message);
//        }
//        else{ //user not found
//            System.out.println("Error: User "+name+" not found.");
//            if(!name.equals("Server")){
//                cl = clients.getByName(name);
//                socket = cl.socket;
//                printer  = new PrintWriter(socket.getOutputStream(),true);
//                printer.println("Server to You: The user "+to+" is not connected.");
//            }
//        }
//    }

    static public void kill(){
        while(!clients.isEmpty()){
            remove(clients.getHead());
        }
        System.exit(1);
    }

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
            if(data.equals("")) writeFile(name+" "+surname+" "+number);
            else
            writeFile(data+"\n"+name+" "+surname+" "+number);
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
                if(entry.equals(s)) newData+= name+" "+surname+" "+number;
                else newData+=s;

            }
            writeFile(newData);
            return true;
        }catch(Exception e){
            return false;
        }
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

//    static public String getUserNames(){
//        String res = "";
//        Client [] clientList = clients.getAll();
//        for(Client c : clientList){
//            res += ","+c.name;
//        }
//        return res;
//    }
}

class ServerInputReader extends Thread{
    BufferedReader in;

    @Override
    public void run(){
        try{
            in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                String s = in.readLine().trim();
                if (s.length() == 4 && s.substring(0,4).equals("QUIT")) {
                    Main.broadcast("Server shutting down...");
                    Main.kill();
                }
                else
                if(s.length() > 10 && s.substring(0, 10).equals("BROADCAST ")){
                    Main.broadcast(s.substring(10,s.length()));
                }
                else
                        System.out.println(s +  " : is not a valid command!");
            }
        }
        catch(Exception e){
            System.out.println("Error with Server input : " + e.getMessage());
        }
    }

}


class ClientHandler extends Thread {

    private BufferedReader in;
    private PrintWriter out;
    private Client toClient;
    private String name;

    ClientHandler(Client s) {
        toClient = s;
        name = s.socket.getInetAddress().toString();
    }

    @Override
    public void run() {
        try{
            in = new BufferedReader(new InputStreamReader(toClient.socket.getInputStream()));
            out = new PrintWriter(toClient.socket.getOutputStream(), true);
            out.println("*** Connected to Aeo Tech contacts servlet ***");

            out.flush();

            toClient.name = toClient.socket.getInetAddress().toString();

            //out.println(name);
            //Main.broadcast(name+" has joined the discussion.", "Server");
            //Main.broadcast(Main.getUserNames(),"$USER_LIST$");

            while (true){
                // if(in == null) System.err.println("here comes an exception");
                if(!toClient.socket.isConnected()) break;
                String s = in.readLine().trim(); //get all white spaces out
                System.out.println("RECEIVED: "+s+" FROM "+name);
                out.write("\u001B[2J");
                if(s.length() >= 2 && s.substring(0,2).equals("$H")){
                    out.println("$QUIT - Exit the service");
                    out.println("$CREATE name surname number - Create an entry");
                    out.println("$UPDATE id name surname number - Update an entry");
                    out.println("$DELETE id - Delete an entry");
                    out.println("$SHOW - Display all entries");
                    out.println("$GET id - Display a single entry");
                }
                else if(s.length() >= 5 && s.substring(0,5).equals("$QUIT")){
                    break;
                }
                else if(s.length() >= 8 && s.substring(0,8).equals("$CREATE ")){
                    try{
                    String [] input = s.substring(8,s.length()).split(" ");
                        if (Main.createEntry(input[0], input[1],input[2])){
                        out.println("CREATE: Success.");
                    }
                    else{
                        out.println("CREATE: Error.");
                        out.println("Use the following format: $CREATE name surname number");
                    }
                    }catch(Exception e){
                        out.println("ERROR: CREATE Server Error");
                    }
                }
                else if(s.length() >= 8 && s.substring(0,8).equals("$UPDATE ")){
                    try {
                        String[] input = s.substring(8, s.length()).split(" ");
                        if (Main.updateEntry(new Integer(input[0]), input[1], input[2], input[3])) {
                            out.println("UPDATE: Success.");
                        } else {
                            out.println("UPDATE: Error.");
                            out.println("Use the following format: $UPDATE id name surname number");
                        }

                }catch(Exception e){
                    out.println("ERROR: UPDATE Server Error");
                }
                }

                else if(s.length() >= 8 && s.substring(0,8).equals("$DELETE ")){
                    try {
                        String input = s.substring(8, s.length());
                        if (Main.deleteEntry(new Integer(input))) {
                            out.println("DELETE: Success.");
                        } else {
                            out.println("DELETE: Error.");
                            out.println("Use the following format: $DELETE id");
                        }
                    }catch(Exception e){
                        out.println("ERROR: DELETE Server Error");
                    }
                }
                else if(s.length() >= 5 && s.substring(0,5).equals("$SHOW")){
                try{
                    Integer id = new Integer(0);
                    String [] entries = Main.readEntries();
                    if(entries[0].equals("")){
                        out.println("NO DATA TO SHOW");
                    }
                    else {
                        for (String e : entries) {
                            out.println(id.toString() + ": " + e);
                            id++;
                        }
                    }
                }catch(Exception e){
                    out.println("ERROR: SHOW Server Error");
                }
                }
                else if(s.length() >= 5 && s.substring(0,5).equals("$GET ")){
                    try{
                    String  input = s.substring(5,s.length());
                    String output = Main.getEntry(new Integer(input));
                    if(output != null){
                        out.println("ENTRY "+input+": "+output);
                    }
                    else{
                        out.println("GET: Error.");
                        out.println("Use the following format: $GET id");
                    }
                    }catch(Exception e){
                        out.println("ERROR: GET Server Error");
                    }
                }
                else{
                    out.println("ERROR: Command "+s+" not recognized.");
                    out.println("Type $H for help");
                }

//                if (s.length() > 2 && s.charAt(0) == 'B' &&
//                        s.charAt(1) == 'Y' && s.charAt(2) == 'E') {
//                    Main.broadcast(name+" has left the discussion.",
//                            "Server");
//
//                    Main.broadcast(Main.getUserNames().replace(","+name,""),"$USER_LIST$");
//                    break;
//                }
//                else
//                if(s.substring(0,5).equals("SEND ")){
//                    String [] parts = s.split(" ");
//                    String to = parts[1];
//                    String message = "";
//                    for(int i =2;i<parts.length;i++) message +=" "+parts[i];
//                    Main.send(message,name, to);
//                }
//                else
//                if(s.equals("$USER_LIST_REQUEST$")){
//                    Main.send(Main.getUserNames(),"$USER_LIST$", name);
//                }
//                else
//                    Main.broadcast(s, name);

            }

            Main.remove(toClient);
        } catch (Exception e) {
            System.out.println("Server error: "+e);
        }
    }

}




