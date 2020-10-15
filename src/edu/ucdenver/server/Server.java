package edu.ucdenver.server;
import com.sun.javaws.exceptions.InvalidArgumentException;
import java.net.ServerSocket;
public class Server {
    private ServerSocket socket;
    private String configFile;
    public Server (int port) throws java.io.IOException {
            this.socket = new ServerSocket();
            this.configFile = null;
    }
    public Server (String configFile) throws InvalidArgumentException,java.io.IOException {
            this.socket = new ServerSocket(8080);
            this.configFile = configFile;
            this.loadFromFile();
    }
    private void loadFromFile() throws InvalidArgumentException{
        return;
    }
    public synchronized void accept_new_connection() throws java.io.IOException{
        this.socket.accept();
    }
}
