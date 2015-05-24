import java.net.Socket;

/**
 * Created by Trevor on 2015-05-24.
 */
public class GameClient {
    private String name;
    private Socket socket;

    public GameClient(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
}
