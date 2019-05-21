package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.KillShotTrack;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.network.server.PlayerController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.*;

import static it.polimi.ingsw.controller.Encoder.Header.OPT;
import static it.polimi.ingsw.controller.Encoder.encode;
import static it.polimi.ingsw.model.board.Player.HeroName.*;
import static java.util.Collections.frequency;

/**
 * Class responsible of running a game.
 * The interaction with the user is simulated by the method receive() of PlayerController.
 * Must be updated implementing the connection with the client.
 *
 * @author BassaniRiccardo
 */

//TODO: implement the connection with the client. Finish testing.
//      understand why handleUsingPowerUp, ConvertPowerUp and Reload are sometimes called unexpectedly (maybe it is solved now).

public class GameEngine implements Runnable{

    private List<PlayerController> players;
    private PlayerController currentPlayer;
    private Board board;
    private boolean gameOver;
    private KillShotTrack killShotTrack;
    private boolean frenzy;
    private StatusSaver statusSaver;

    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static final String P = "Player ";


    /**
     * Constructs a GameEngine with a list of Player Controller.
     *
     * @param players           the players in the game.
     */
    public GameEngine(List<PlayerController> players){
        this.players = players;
        currentPlayer = null;
        board = null;
        gameOver = false;
        killShotTrack = null;
        frenzy = false;
        this.statusSaver = null;
    }

    /**
     *  Getters
     */
    public List<PlayerController> getPlayers() {
        return players;
    }

    public PlayerController getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }


    /**
     *  Setters
     */
    public void setPlayers(List<PlayerController> players) {
        this.players = players;
    }

    public void setCurrentPlayer(PlayerController currentPlayer) {
        this.board.setCurrentPlayer(currentPlayer.getModel());
        this.currentPlayer = currentPlayer;
    }

    /**
     * Only for testing
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Adds a PlayerController to the game.
     *
     * @param index         the position of the new PlayerController in the PlayerController list.
     * @param p             the player connection to addList.
     */
    public void setPlayer(int index, PlayerController p) {
        this.players.set(index, p);     //this and other methods need to be synchronized
    }


    /**
     * Runs a game.
     *
     * @requires 3 <= players.size() && players.size() <= 5
     */
    public void run(){

        setup();
        ExecutorService executor = Executors.newCachedThreadPool();
        while (!gameOver){
            runTurn(executor, 1, false);
            if (killShotTrack.getSkullsLeft() == 0) {
                manageGameEnd(executor);
            }
            changePlayer();
        }
        resolve();

    }


    /**
     * Sets up the game.
     *
     */
    public void setup(){

        LOGGER.log(Level.INFO,"\n\nAll the players are connected.\n");
        configureMap();
        configureKillShotTrack();
        try {
            board.getKillShotTrack().removeSkulls(4);

        } catch ( NotAvailableAttributeException | UnacceptableItemNumberException e){};
        BoardConfigurer.configureDecks(board);
        LOGGER.log(Level.INFO,"Decks configured.");

        try {
            BoardConfigurer.setAmmoTilesAndWeapons(board);
            LOGGER.log(Level.INFO,"Ammo tiles and weapons set.");
        } catch (UnacceptableItemNumberException | NoMoreCardsException e) {LOGGER.log(Level.SEVERE,"Exception thrown while setting ammo tiles and weapons", e);}
        configurePlayers();

        //set frenzy options
        List<String> frenzyOptions = new ArrayList<>();
        int yes = 0;
        int no = 0;
        frenzyOptions.addAll(Arrays.asList("yes", "no"));
        for (PlayerController p: players){
            p.send(encode(OPT, "Do you wan to play with the frenzy?", frenzyOptions));
            if (p.receive(2, 10) == 1) yes++;
            else no++;
        }
        if (yes>=no) {
            frenzy=true;
            LOGGER.log(Level.INFO,"Frenzy active.");
        }
        else  LOGGER.log(Level.INFO,"Frenzy not active.");


        setCurrentPlayer(players.get(0));
        statusSaver = new StatusSaver(board);
        LOGGER.log(Level.INFO,"\n");

    }


    /**
     * Configures the map asking the players for their preference.
     */
    private void configureMap(){

        List<Integer> mapIDs = new ArrayList<>(Arrays.asList(1,2,3,4));
        List<Integer> votes = new ArrayList<>(Arrays.asList(0,0,0,0));

        for (PlayerController p : players) {
            p.send(encode(OPT, "Vote for the map you want:", mapIDs));
            int vote = p.receive(4,10);
            votes.set(vote-1, votes.get(vote-1)+1);
        }
        int mapId = Collections.max(votes);

        board = BoardConfigurer.configureMap(mapId);

        LOGGER.log(Level.INFO,() -> "Players voted: map " + mapId + " selected.");

    }

    /**
     * Configures the kill shot track asking the players for their preference.
     */
    private void configureKillShotTrack(){

        List<Integer> skullsOptions = new ArrayList<>(Arrays.asList(5,6,7,8));
        int totalSkullNumber = 0;

        for (PlayerController p : players) {
            p.send(encode(OPT, "How many skulls do you want?", skullsOptions));
            int selected = p.receive(4, 10);
            totalSkullNumber = totalSkullNumber + selected + 4;
        }
        int averageSkullNumber = Math.round((float)totalSkullNumber/(float)players.size());
        BoardConfigurer.configureKillShotTrack(averageSkullNumber, board);
        try {
            this.killShotTrack = board.getKillShotTrack();
        } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE,"NotAvailableAttributeException thrown while configuring the kill shot track", e);}

        LOGGER.log(Level.INFO,() -> "Players voted. Number of skulls: " + averageSkullNumber + ".");

    }

    /**
     * Adds to the board the players connected to the game.
     * Asks the players which hero they want, providing only the remaining options.
     */
    private void configurePlayers(){

        List<Player.HeroName> heroList = new ArrayList<>(Arrays.asList(D_STRUCT_OR, BANSHEE, DOZER, VIOLET, SPROG));
        int id = 1;

        for (PlayerController p : players) {

            p.send(encode(OPT, "What hero do you want?", heroList));
            int selected = p.receive(heroList.size(), 10);
            Player.HeroName selectedName = heroList.get(selected-1);
            p.setPlayer(new Player(id, selectedName, board));
            board.getPlayers().add(p.getModel());
            heroList.remove(selectedName);
            LOGGER.log(Level.INFO,P + id + " selected " + selectedName + ".");
            id++;

        }

    }

    /**
     * Returns the player who has to play after the current one.
     *
     * @return      the next player.
     */
    public PlayerController getNextPlayer(){   //must be robust for an empty list of player

        int ind = players.indexOf(currentPlayer);
        ind++ ;

        if (ind > players.size()-1)  {
            ind = 0;
        }

        while(players.get(ind).isSuspended()){
            ind++;
            if(ind > players.size()-1){
                ind = 0;
            }
        }

        return players.get(ind);

    }


    /**
     * Called at the end of the game, assigns the points for the kill shot track and decides the winner.
     */
    public void resolve(){

        if (!frenzy) LOGGER.log(Level.INFO,"The last skull has been removed. Points are added to the players according to the kill shot track.");
        else LOGGER.log(Level.INFO,"Frenzy ended. Points are added to the players according to the kill shot track.");

        killShotTrack.rewardKillers();

        Collections.sort(players, (p1,p2) -> {
            if (p1.getModel().getPoints() > p2.getModel().getPoints()) return -1;
            else if (p1.getModel().getPoints() < p2.getModel().getPoints()) return 1;
            else {
                if (frequency(killShotTrack.getKillers(), p1) > frequency(killShotTrack.getKillers(), p2))
                    return -1;
                else if (frequency(killShotTrack.getKillers(), p1) < frequency(killShotTrack.getKillers(), p2))
                    return 1;
                else {
                    if (killShotTrack.getKillers().indexOf(p1) < killShotTrack.getKillers().indexOf(p2))
                        return -1;
                    else if (killShotTrack.getKillers().indexOf(p1) > killShotTrack.getKillers().indexOf(p2))
                        return 1;
                    return 0;
                }
            }
        });

        gameOver();

    }


    /**
     * Runs a turn, starting a timer representing the maximum time the user can use to complete his turn.
     * A turn can be a normal turn or a turn of the frenzy phase.
     *
     * @param executor              the executor which execute the  thread of TurnManager.
     * @param timeout               the maximum time to complete a turn.
     * @param frenzy                whether the frenzy is active during the turn.
     */
    public void runTurn (ExecutorService executor, int timeout, boolean frenzy){

        Future future = executor.submit(new TurnManager(board, currentPlayer, players, statusSaver,  frenzy));

        try {
            future.get(timeout, TimeUnit.MINUTES);
        } catch (TimeoutException e) { currentPlayer.suspend();
        } catch (Exception e) {LOGGER.log(Level.SEVERE,"Exception thrown while running a turn", e);} //proper handling to be implemented

    }


    /**
     * Updates the current player and checks if there are enough players to continue the game.
     */
    public void changePlayer(){

        setCurrentPlayer(getNextPlayer());
        long playerCount = players.stream().filter(x->!x.isSuspended()).count();
        if(playerCount<3){
            gameOver = true;
        }

    }


    /**
     * Manages the end of the game, depending on whether the frenzy mode is active.
     *
     * @param executor      the executor which execute the  thread of TurnManager.
     */
    public void manageGameEnd(ExecutorService executor){

        if (!frenzy) gameOver = true;
        else {
            for (Player p : board.getPlayers()){
                if (p.getDamages().isEmpty()){
                    p.setFlipped(true);
                    p.setPointsToGive(2);
                    int frenzyActivator = currentPlayer.getModel().getId();
                    if (p.getId() > frenzyActivator){
                        p.setStatus(Player.Status.FRENZY_1);
                    }
                    else p.setStatus(Player.Status.FRENZY_2);
                }
            }
            LOGGER.log(Level.INFO,"\nNo more skulls left:\n\nFrenzy mode!!!!!!\n");
            for (int i=0; i<players.size(); i++){
                runTurn(executor, 1, true);
                changePlayer();
            }
            gameOver = true;
        }

    }


    /**
     * Decides the winner.
     */
    public void gameOver(){

        System.out.println("\nGame over.\n");

        if (players.get(0).getModel().getPoints() == players.get(1).getModel().getPoints() && !killShotTrack.getKillers().contains(players.get(0).getModel()) && !killShotTrack.getKillers().contains(players.get(1).getModel())) {
            LOGGER.log(Level.INFO,() -> P + players.get(0).getModel().getId() + " and Player " + players.get(1).getModel().getId() + ", you did not kill anyone. Shame on you! The game ends with a draw.\n");
            for (int i = 2; i < players.size(); i++) {
                LOGGER.log(Level.INFO, P + players.get(i).getModel().getId() + ", " + players.get(i).getModel().getPoints() + " points.");
            }
        } else {
            LOGGER.log(Level.INFO,() -> "Winner: Player " + players.get(0).getModel().getId() + ", with " + players.get(0).getModel().getPoints() + " points!\n");
            for (int i = 1; i < players.size(); i++) {
                LOGGER.log(Level.INFO, P + players.get(i).getModel().getId() + ", " + players.get(i).getModel().getPoints() + " points.");
            }
        }

    }


}
