package it.polimi.ingsw.view.CLIRenderer;

import it.polimi.ingsw.view.ClientMain;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainRendererTest {

    @Test
    public void stringToBoxTest(){
        MainRenderer mainRenderer = new MainRenderer(new ClientMain());
        draw(mainRenderer.stringToBox("Prova\n1 opzione\n2 opzione\nmax", 3, 55, false));


        draw(mainRenderer.stringToBox("Prova\n1 opzione\n2 opzione\n3 opzione\n4 opzione\n5 opzione\nmax", 3, 55, false));

    }

    private void draw(String[][] s){
        for(int i=0; i<s.length; i++){
            for(int j=0; j<s[i].length; j++){
                System.out.print(s[i][j]);
            }
            System.out.print("\t "+ s[i].length+"\n");
        }
    }

}