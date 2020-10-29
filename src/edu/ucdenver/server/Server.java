package edu.ucdenver.server;
import edu.ucdenver.domain.store.ItemStore;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private int port;
    private int backlog;
    private String configFile;
    private ItemStore store;
    public Server (int port,int backlog) {
            this.backlog = backlog;
            this.port = port;
            this.configFile = null;
            this.store = new ItemStore();
    }
    public Server (int port,int backlog,String configFile) throws IllegalArgumentException,java.io.IOException {
            this.backlog = backlog;
            this.port = port;
            this.configFile = configFile;
            this.store = new ItemStore();
            this.loadFromFile();
    }
    @Override
    public void run(){
        ServerSocket server = null;
        ExecutorService service = Executors.newCachedThreadPool();
        try {
            server = new ServerSocket(this.port,this.backlog);
            while (true){
                System.out.println("Accepting...");
                Socket s = server.accept();
                System.out.println("Accepted!");
                service.execute(new ServerTask(s,server,store));
            }
        }
        catch (Exception e){
            try{
                server.close();
            }
            catch (Exception ee){

            }
        }

        try{
            server.close();
        }
        catch (Exception ee){

        }
        service.shutdown();

    }
    private void loadFromFile() throws IllegalArgumentException{
        return;
    }
}
