
public class Main {

    public static void main(String[] args) {
	// write your code here
        int port = 80;
        try{
            port = Integer.parseInt(args[0]);
        }catch(Exception e){}
        WebServer ws = new WebServer(port);
        ws.start();
    }
}
