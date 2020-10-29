package edu.ucdenver.server;

import edu.ucdenver.domain.Request;
import edu.ucdenver.domain.RequestType;
import edu.ucdenver.domain.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(8080,20);
        server.run();
        System.out.println("RANNNN");
    }
}
