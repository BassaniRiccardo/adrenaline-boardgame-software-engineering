package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.server.TCPServer;
import org.junit.Test;
import static org.junit.Assert.*;

//TODO: add more tests once a client has been implemented
public class TCPServerTest {

    @Test
    public void setupTest(){
        TCPServer s = new TCPServer(4000, ServerMain.getInstance());
        assertEquals(s.getPort(), 4000);
    }
}