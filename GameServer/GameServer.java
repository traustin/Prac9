import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Trevor on 2015-05-24.
 */
public class GameServer{
    private int port;
    private BufferedReader br;

    public GameServer(int port) {
        this.port = port;
    }

    public void run(){
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Game server is now running on port: " + port);


            br = new BufferedReader(new InputStreamReader(System.in));
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
