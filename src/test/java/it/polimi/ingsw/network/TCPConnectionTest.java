package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.ServerMain;
import it.polimi.ingsw.controller.TCPServer;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

//TODO: consider more in-depth testing
public class TCPConnectionTest {

    @Test
    public void send() {
        ServerMain s = ServerMain.getInstance();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                ServerMain.getInstance().main(null);
            }
        });
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        }catch(InterruptedException ex){
            System.out.println("Skipped waiting time.");
            Thread.currentThread().interrupt();
        }
        TCPConnection t = new TCPConnection(null);
        t.send("name");
        t.send("test");
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        }catch(InterruptedException ex){
            System.out.println("Skipped waiting time.");
            Thread.currentThread().interrupt();
        }
        //assertTrue(s.getPlayers().get(0).receive().equals("test"));   //////////////////FIXME
    }

    @Test
    public void shutdown() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(new TCPServer(5000, null));
        TCPConnection t = new TCPConnection(null);
        t.shutdown();
        assertTrue(t.getSocket().isClosed());
    }
}