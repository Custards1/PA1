package edu.ucdenver.server;
import edu.ucdenver.domain.store.ItemStore;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private int port;


    private int backlog;
    private String configFile;
    private ItemStore store;
    //constructs server at specified port and backlog,does not start server
    public Server (int port,int backlog) {
            this.backlog = backlog;
            this.port = port;
            this.configFile = "default.ser";
            this.store = new ItemStore();
    }
    //constructs server at specified port and backlog and config file,does not start server but will
    //throw error if data cannot be loaded from the config file
    public Server (int port,int backlog,String configFile) throws IllegalArgumentException,java.io.IOException,ClassNotFoundException {
            this.backlog = backlog;
            this.port = port;
            this.configFile = configFile;
            this.store = new ItemStore();
            this.loadFromFile();
    }

    //runs the server
    @Override
    public void run(){
        ServerSocket server = null;
        ExecutorService service = Executors.newCachedThreadPool();
        try {
            server = new ServerSocket(this.port,this.backlog);
            while (true){

                Socket s = server.accept();

                service.execute(new ServerTask(s,server,store));
            }
        }
        catch (Exception e){
            try{
                if(server!=null){
                    server.close();
                }
            }
            catch (Exception ee){

            }
        }

        try{
            if(server!=null){
                server.close();
            }
        }
        catch (Exception ee){

        }
        service.shutdown();

    }
    //loads from a config file
    public void loadFromFile() throws IOException,ClassNotFoundException{
        if(configFile.isEmpty() || configFile == null){
            configFile = "default.ser";
        }
        FileInputStream fileIn = new FileInputStream(configFile);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        store = (ItemStore) in.readObject();
        in.close();
        fileIn.close();
    }
    //saves to a config file
    public void saveToFile() throws IOException{
        if(configFile.isEmpty() || configFile == null){
            configFile = "default.ser";
        }
        FileOutputStream fileOut = new FileOutputStream(configFile);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(store);
        out.close();
        fileOut.close();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public ItemStore getStore() {
        return store;
    }

    public void setStore(ItemStore store) {
        this.store = store;
    }
}
