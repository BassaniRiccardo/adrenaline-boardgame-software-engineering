package it.polimi.ingsw.network.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.ClientModel;
import it.polimi.ingsw.view.UI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of Socket connection to server
 *
 * @author marcobaga
 */
public class TCPConnection implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ClientMain clientMain;
    static final Logger LOGGER = Logger.getLogger("clientLogger");


    /**
     * Constructor establishing a TCP connection
     *
     * @param clientMain        reference to the main class
     * @param address           IP to connect to
     * @param port              port to connect to
     */
    public TCPConnection(ClientMain clientMain, String address, int port){
        this.clientMain = clientMain;
        LOGGER.log(Level.INFO, "Starting TCP connection");
        try {
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            socket.setSoTimeout(100);
            LOGGER.log(Level.INFO, "Connected to TCP server");
        }catch (ConnectException ex){
            LOGGER.log(Level.INFO, "Cannot connect to server. Closing");
            System.exit(0);
        }catch (IOException ex) {
            LOGGER.log(Level.INFO, "Cannot read or write to connection. Closing");
            clientMain.shutdown();
            //try again?
        }
    }


    public void run(){
        JsonParser jsonParser = new JsonParser();
        while(Thread.currentThread().isAlive()){
            try {
                String message = receive();
                JsonObject jMessage = (JsonObject) jsonParser.parse(message);
                LOGGER.log(Level.INFO, "Received message: " + jMessage.get("head").getAsString());
                handleRequest(jMessage);
            }catch (SocketTimeoutException ex) {
                LOGGER.log(Level.FINEST, "No incoming message from TCPVirtualView", ex);
            }catch(Exception ex){
                LOGGER.log(Level.SEVERE, "Received string cannot be parsed to Json", ex);
                JsonObject jMessage = new JsonObject();
                jMessage.addProperty("head", "MALFORMED");
                handleRequest(jMessage);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, "Skipped waiting time");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void handleRequest(JsonObject jMessage){
        String head = jMessage.get("head").getAsString();
        switch(head){
            case "MSG" :    clientMain.display(jMessage.get("text").getAsString());
                            break;
            case "REQ" :    String msg = clientMain.getInput(jMessage.get("text").getAsString(),Integer.valueOf(jMessage.get("length").getAsString()));
                            send(msg);
                            break;
            case "OPT" :    List<String> list = new ArrayList<>();
                            for(JsonElement j : jMessage.get("options").getAsJsonArray()){
                                list.add(j.getAsString());
                            }
                            int choice = clientMain.choose(jMessage.get("text").getAsString(), list);
                            send(String.valueOf(choice));
                            break;
            case "UPD" :    JsonObject mod = jMessage.getAsJsonObject("mod");
                            Gson gson = new Gson();
                            ClientModel clientModel = gson.fromJson(mod.toString(), ClientModel.class);
                            clientMain.setClientModel(clientModel);
                            break;
            default:        //do something
                            break;
        }
    }

    /**
     * Sends a message through the socket (NOT blocking)
     *
     * @param message       message to send
     */
    public void send(String message){
        out.println(message);
        out.flush();
        LOGGER.log(Level.FINE, "Message sent to TCP server: {0}", message);
    }

    /**
     * Checks the socket input stream for new messages (NOT blocking)
     *
     * @return          the string received (empty if no message arrived)
     */
    String receive() throws SocketTimeoutException{
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("head", "PING");
        String message = jsonObject.toString();
        try {
            message = in.readLine();
            if (message == null) {
                LOGGER.log(Level.INFO, "TCPConnection: server disconnected, shutting down");
                clientMain.shutdown();
            }
        }catch(SocketTimeoutException ex) {
            throw new SocketTimeoutException();
        }catch(IOException ex) {
            LOGGER.log(Level.INFO, "TCPConnection: server disconnected, shutting down");
            clientMain.shutdown();
        }
        return message;
    }
}
