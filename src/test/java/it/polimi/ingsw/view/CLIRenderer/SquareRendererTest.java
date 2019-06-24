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
import it.polimi.ingsw.view.ClientMain;
import it.polimi.ingsw.view.ClientModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SquareRendererTest {

    @Test
    public void getBox() {

        List<String> ammo = new ArrayList<>();
        ammo.add("x");
        ammo.add("y");
        ammo.add("z");
        List<String> players = new ArrayList<>();
        players.add("o");
        SquareRenderer squareRenderer = new SquareRenderer(1, ammo, 2, players);
        MainRenderer.drawModel(squareRenderer.getBox());
    }
}