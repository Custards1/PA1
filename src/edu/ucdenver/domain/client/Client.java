
package edu.ucdenver.domain.client;

import edu.ucdenver.domain.Request;
import edu.ucdenver.domain.RequestType;
import edu.ucdenver.domain.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader input = null;
    private PrintWriter output = null;
    private boolean isAdmin;
    private User user;
    public void shutdown(){
        if(this.socket!=null){
            try {

                this.socket.close();
            }
            catch (Exception e){

            }
            this.socket = null;
        }
        if(this.input!=null){
            try {

                this.input.close();
            }
            catch (Exception e){

            }
            this.input = null;
        }
        if(this.output!=null){
            try {

                this.output.close();
            }
            catch (Exception e){

            }
            this.output = null;
        }

    }

    public Client(String host,int port,User self,boolean signup)throws ClientError{
        this.host = host;
        this.port = port;
        this.isAdmin = false;
        Request recived = null;
        ArrayList<HashMap<String,String>> re = new ArrayList();
        re.add(self.asRequestable());
        Request to_send = new Request(signup?RequestType.CREATE_USER:RequestType.AUTHENTICATE_USER,null,re);
        try {
            try{
                this.socket = new Socket(host,port);
                input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream());
                to_send.send(output);
            }
            catch (IOException e){
                throw new ClientError(ClientErrorType.INVALID_SOCKET);
            }


            try {
                recived = new Request(input);
                if(recived.getType() == RequestType.ERROR){
                    ClientErrorType error = ClientErrorType.UNKNOWN;
                    try {
                        error = ClientErrorType.valueOf(recived.getField("error-type"));
                    }
                    catch (IllegalArgumentException e){

                    }
                    throw new ClientError(error);
                }
                else if(recived.getType() != RequestType.OK) {
                    throw new ClientError(ClientErrorType.UNKNOWN);
                }
                try {
                    String is_admin_raw = recived.getField("admin");
                    this.isAdmin = is_admin_raw.equals("true");
                }
                catch (IllegalArgumentException e){

                }


            }
            catch (ClientError e) {
                    throw e;
            }

        }
        catch (ClientError ioe) {
            this.shutdown();
            throw ioe;
        }
    }
    public void sendBlankRequest(RequestType type) throws ClientError {
        Request to_send = new Request(type,null,null);
        try {
            to_send.send(output);
        }
        catch (IOException e){
            shutdown();
            throw new ClientError(ClientErrorType.INVALID_SOCKET);
        }
    }

    /*
    public boolean requestUserByEmail(String email, String password) {

    }
    public boolean requestUserByName(String name,String password) {

    }
    public boolean requestCatagory(String catagory_name) {

    }
    public boolean requestProduct(String product_name) {

}

     */


}
