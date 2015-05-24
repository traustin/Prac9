//Werner Mostert
//13019695
//University of Pretoria COS 216

import java.io.*;
import java.net.*;
import DataStructures.*;

public class MyChatServer{

    private static LinkedList clients = new LinkedList();
    private static int id = 0;

    public static void main(String[] args) throws IOException {
	int port = 8190;
        
	if (args.length > 0)
	    port = Integer.parseInt(args[0]);
        
	ServerSocket listener = new ServerSocket(port);
	System.out.println("Chat Server running on port "+port);
        new ServerInputReader().start();
        
	while (true) {
	    Socket client = listener.accept();
            Client newClient = new Client("user#"+ (new Integer(id)).toString(),client);
	    new ChatHandler(newClient).start();
	    System.out.println("New client no."+id+" from "+ listener.getInetAddress()+
                               " on client's port "+client.getPort());
	    
	    clients.add(newClient);
	    id++;
	}
    }

    static synchronized void broadcast(String message, String name)
	    throws IOException {
	// Sends the message to every client including the sender.
	Socket socket;
	PrintWriter printer;
         
       Client [] clientList = clients.getAll();
        for (Client c : clientList) {
            socket = c.socket;
            printer = new PrintWriter(socket.getOutputStream(),true);
            printer.println(name+": "+message);
        }
	
    }

    static synchronized void remove(Client s) {
        try{
        s.socket.close();
	System.out.println(s.name+ " disconnected.");
        clients.remove(s);        
	id--;
        }
        catch(Exception e){
            System.out.println("Error closing socket: " + e.getMessage() + " with client " + s.name);
        }
    }
    
    static synchronized void send(String message, String name, String to) throws IOException {
        Socket socket;
	PrintWriter printer;
        Client cl = clients.getByName(to);
        if(cl != null){ //found
        socket = cl.socket;
        printer  = new PrintWriter(socket.getOutputStream(),true);
        printer.println(name+" to You:"+message);
        }
        else{ //user not found
           System.out.println("Error: User "+name+" not found.");
            if(!name.equals("Server")){
                cl = clients.getByName(name);
                socket = cl.socket;
                printer  = new PrintWriter(socket.getOutputStream(),true);
                printer.println("Server to You: The user "+to+" is not connected.");
            }
        }
    }
    
    static public void kill(){
       while(!clients.isEmpty()){
           remove(clients.getHead());
       }
       System.exit(1);
    }
    
    static public String getUserNames(){
        String res = "";
        Client [] clientList = clients.getAll();
        for(Client c : clientList){
            res += ","+c.name;
        }
        return res;
    }
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
		    MyChatServer.broadcast("Server shutting down...","Server");
		    MyChatServer.kill();
		}
               else
                if(s.length() > 10 && s.substring(0, 10).equals("BROADCAST ")){
                    MyChatServer.broadcast(s.substring(10,s.length()), "Server");
                }
                else
               //SEND functionality
                if(s.length() > 6 && s.substring(0,5).equals("SEND ")){
                    String [] parts = s.split(" ");
                    String to = parts[1];
                    String message = "";
                    for(int i =2;i<parts.length;i++) message +=" "+parts[i];
                    MyChatServer.send(message,"Server", to);
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


class ChatHandler extends Thread {

    private BufferedReader in;
    private PrintWriter out;
    private Client toClient;
    private String name;

    ChatHandler(Client s) {
	toClient = s;
    }

    @Override
    public void run() {
	try{
	    in = new BufferedReader(new InputStreamReader(toClient.socket.getInputStream()));
	    out = new PrintWriter(toClient.socket.getOutputStream(), true);
	    out.println("*** Chat Service Active ***");
	   // out.println("Type QUIT to end");
            //out.println("Enter name:");
	    out.flush();
	    name = in.readLine();
            toClient.name = name;
            out.println(name+" has joined the discussion.");
            //out.println(name);
	    MyChatServer.broadcast(name+" has joined the discussion.", "Server");
            MyChatServer.broadcast(MyChatServer.getUserNames(),"$USER_LIST$");
            
	    while (true){
               // if(in == null) System.err.println("here comes an exception");
		String s = in.readLine().trim();
                
                if (s.length() > 2 && s.charAt(0) == 'B' &&
			s.charAt(1) == 'Y' && s.charAt(2) == 'E') {
		    MyChatServer.broadcast(name+" has left the discussion.",
			    "Server");
                   
                    MyChatServer.broadcast(MyChatServer.getUserNames().replace(","+name,""),"$USER_LIST$");
		    break;
		}
                else
                if(s.length() > 6 && s.substring(0,5).equals("SEND ")){
                    String [] parts = s.split(" ");
                    String to = parts[1];
                    String message = "";
                    for(int i =2;i<parts.length;i++) message +=" "+parts[i];
                    MyChatServer.send(message,name, to);
                }
                else
                if(s.equals("$USER_LIST_REQUEST$")){
                    MyChatServer.send(MyChatServer.getUserNames(),"$USER_LIST$", name);
                }
                else
                    MyChatServer.broadcast(s, name);
		
	    }
         
	    MyChatServer.remove(toClient);
	} catch (Exception e) {
	    System.out.println("Server error: "+e);
	}
    }

}



