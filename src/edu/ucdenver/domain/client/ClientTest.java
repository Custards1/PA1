package edu.ucdenver.domain.client;

import edu.ucdenver.domain.RequestType;
import edu.ucdenver.domain.User;

public class ClientTest {
    public static void main(String[] args) {
        User user = new User("admin@admin.org","admin","admin3234");
        Client client = null;
        try {
            client = new Client("127.0.1.1",8080,user,false);
            client.sendBlankRequest(RequestType.TERMINATE);
            System.out.println("Success");
        }
        catch (Exception e){
            System.out.printf("Execptions %s",e.getMessage());
        }
        finally {
            if(client !=null){
                client.shutdown();
            }
        }

    }
}
