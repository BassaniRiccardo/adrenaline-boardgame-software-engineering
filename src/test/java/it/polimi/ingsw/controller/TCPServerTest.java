package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.server.TCPServer;
import org.junit.Test;
import static org.junit.Assert.*;

public class TCPServerTest {

    @Test
    public void setupTest(){
        TCPServer s = new TCPServer(4000);
        assertEquals(4000, s.getPort());
    }
}