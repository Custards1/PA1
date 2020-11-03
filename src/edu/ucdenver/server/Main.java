package edu.ucdenver.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(8080,20);
        boolean invalid = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(invalid){
            System.out.println("1) Read from file (default.ser)");
            System.out.println("2) Start without a file");
            System.out.print(">> ");
            String in=null;
            try {
                in = br.readLine();
            }
            catch(Exception e){
                System.out.println("You must be in a terminal..\nExiting...");
                return;
            }
            if(in.startsWith("1")) {
                try {
                    server.loadFromFile();
                    System.out.println("Starting server from loaded file default.ser...");
                }
                catch (Exception e) {
                    System.out.printf("Failed to load from file %s,does not exist\n",server.getConfigFile());
                    System.out.println("Starting server without a loaded file...");
                }
                invalid=false;
            }
            else if(in.startsWith("2")){
                System.out.println("Starting server without a loaded file...");
                invalid = false;
            }
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
