package it.polimi.ingsw.view.CLIRenderer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import it.polimi.ingsw.view.CLI;
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.ClientModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainRendererTest {

    @Test
    public void stringToBoxTest(){
        MainRenderer mainRenderer = new MainRenderer(new ClientMain());
        draw(mainRenderer.stringToBox("Prova\n1 opzione\n2 opzione\nmax", 3, 55, false));
        draw(mainRenderer.stringToBox("Prova\n1 opzione\n2 opzione\n3 opzione\n4 opzione\n5 opzione\nmax", 3, 55, false));
    }

    @Test
    public void render() throws UnacceptableItemNumberException, NotAvailableAttributeException, NoMoreCardsException, WrongTimeException {
        Board board = BoardConfigurer.simulateScenario();

        board.getPlayers().get(0).sufferDamage(3,board.getPlayers().get(2));
        board.getPlayers().get(2).sufferDamage(12,board.getPlayers().get(1));
        board.getPlayers().get(0).addMarks(2,board.getPlayers().get(3));
        board.getPlayers().get(2).setDead(true);
        board.getKillShotTrack().registerKill(board.getPlayers().get(0),board.getPlayers().get(2),false);
        board.getPlayers().get(2).setAmmoPack(new AmmoPack(1,2,3));

        ClientMain clientMain = new ClientMain();
        CLI cli = new CLI(clientMain);

        cli.render();

        JsonObject mod = new JsonParser().parse((Updater.getModel(board, board.getPlayers().get(0))).get("mod").getAsString()).getAsJsonObject();
        clientMain.setClientModel(new Gson().fromJson(mod, ClientModel.class));
        clientMain.getClientModel().setCurrentPlayerId(2);

        cli.render();
    }

    @Test
    public void showQuitScreen() {
        MainRenderer.showQuitScreen();
    }

    @Test
    public void showInfoScreen() {
        MainRenderer.showInfoScreen("flamethrower");
    }

    @Test
    public void stringToBox() {
        MainRenderer.drawModel(MainRenderer.stringToBox("foo", 3, 5, true));
        StringBuilder bld = new StringBuilder();
        for(int i=0; i<1000; i++)
            bld.append("foo");
        MainRenderer.drawModel(MainRenderer.stringToBox(bld.toString(), 3, 48, true));
        MainRenderer.drawModel(MainRenderer.stringToBox(bld.toString(), 3, 48, false));
    }

    @Test
    public void join() {
        String[][] map1 = new String[2][2];
        map1[0][0]  = "a";
        map1[0][1]  = "b";
        map1[1][0]  = "c";
        map1[1][1]  = "d";

        String[][] map2 = new String[2][2];
        map2[0][0]  = "a";
        map2[0][1]  = "b";
        map2[1][0]  = "c";
        map2[1][1]  = "d";

        MainRenderer.drawModel(MainRenderer.join(true, map1, map2, true));
        MainRenderer.drawModel(MainRenderer.join(true, map1, map2, false));
        MainRenderer.drawModel(MainRenderer.join(false, map1, map2, true));
        MainRenderer.drawModel(MainRenderer.join(false, map1, map2, false));
    }

    @Test
    public void drawModel() {
        String[][] map = new String[2][2];
        map[0][0]  = "a";
        map[0][1]  = "b";
        map[1][0]  = "c";
        map[1][1]  = "d";
        MainRenderer.drawModel(map);
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