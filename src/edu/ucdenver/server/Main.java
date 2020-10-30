package edu.ucdenver.server;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(8080,20);
        try {
            server.loadFromFile();
        }
        catch (Exception e) {
            System.out.printf("Failed to load from file %s\n",server.getConfigFile());
        }
        server.run();
        try{
            server.saveToFile();
        }
        catch (Exception e){
            System.out.printf("Failed to save to file %s because %s\n",server.getConfigFile(),e.getMessage());
        }
    }
}
