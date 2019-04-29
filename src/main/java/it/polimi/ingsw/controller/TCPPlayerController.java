package it.polimi.ingsw.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//TODO: finish implementing
//this is the class that both the gameengine and the client talk to. it is used as a "messenger" between the two
public class TCPPlayerController extends PlayerController implements Runnable{

    private Socket socket;
    private List<String> incoming;
    private List<String> outgoing;
    private String name;

    public TCPPlayerController(Socket socket){
        this.socket = socket;
        this.incoming = new ArrayList<>();
        this.outgoing = new ArrayList<>();
    }

    public void run (){
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println("Select a name");
            out.flush();
            while(!in.hasNextLine()) {}
            name = in.nextLine();
            while(!ServerMain.getInstance().login(name, this)){
                if(ServerMain.getInstance().canResume(name)){
                    out.println("Do you want to resume?");
                    out.flush();
                    while(!in.hasNextLine()) {}
                    String ans = in.nextLine();
                    if(ans == "yes") {
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
                while(!in.hasNextLine()) {}
                name = in.nextLine();
            }
            while(!suspended){
                if (in.hasNextLine()) {
                    incoming.add(in.nextLine());
                }
                if (!outgoing.isEmpty()) {
                    out.println(outgoing.get(0));
                    out.flush();
                }
            }
        }catch (IOException e){ e.printStackTrace(); }
    }

    @Override
    public void send(String in) {
        outgoing.add(in);
    }

    @Override
    public String receive() {
        while(incoming.isEmpty()){}
        return incoming.get(0);
    }

    @Override
    public String getQuestion() {       //remote method
        while(outgoing.isEmpty()){
        }
        return outgoing.get(0);
    }

    @Override
    public void setAnswer(String answer) {      //remote method
        incoming.add(answer);
    }
}