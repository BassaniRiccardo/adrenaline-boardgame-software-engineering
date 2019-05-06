package it.polimi.ingsw.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

//TODO: finish implementing
//this is the class that both the gameengine and the client talk to. it is used as a "messenger" between the two
public class TCPPlayerController extends PlayerController{

    private Socket socket;
    private List<String> incoming;
    private List<String> outgoing;
    private BufferedReader in;
    private PrintWriter out;

    public TCPPlayerController(Socket socket){
        this.socket = socket;
        this.incoming = new ArrayList<>();
        this.outgoing = new ArrayList<>();
    }

    public void run (){
        try {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            System.out.println("TCPPlayerController running, starting login procedures");
            out.println("Select a name");
            out.flush();
            System.out.println("Name request sent");
            name = in.readLine();
            System.out.println("Name received: " + name);

            while(!ServerMain.getInstance().login(name, this)){
                if(ServerMain.getInstance().canResume(name)){
                    out.println("Do you want to resume?");
                    out.flush();
                    String ans = in.readLine();
                    if(ans.equals("yes")) {
                        if(ServerMain.getInstance().resume(name, this)){
                            break;
                        } else {
                            out.println("Somebody already resumed.");
                            out.flush();
                        }
                    }
                }
                out.println("Name already taken. Try another one");
                out.flush();
                name = in.readLine();
            }
            out.println("Name accepted.");
            out.flush();
            socket.setSoTimeout(100);

        }catch (IOException e){ e.printStackTrace(); suspend();}
    }

    @Override
    public void refresh() {
        if(!suspended) {
            try {
                String message = in.readLine();
                if (message == null) {
                    suspend();
                } else {
                    incoming.add(message);
                    System.out.println("TCPPlayerController just received a message");
                }
            } catch (SocketTimeoutException ex) {
            } catch (IOException ex) {
                ex.printStackTrace();
                suspend();
            }
            if (!outgoing.isEmpty()) {
                out.println(outgoing.get(0));
                out.flush();
            }
        }
    }

    @Override
    public void send(String in, List<String> options) {
        outgoing.add(in);
    }

    @Override
    public String receive() {
        while(incoming.isEmpty()){}
        return incoming.get(0);
    }
}