
import java.net.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author wernermostert
 */
public class Client {
    public String name;
    public Socket socket;
    public Client(String n, Socket s){
        name = n;
        socket = s;
    }
}
