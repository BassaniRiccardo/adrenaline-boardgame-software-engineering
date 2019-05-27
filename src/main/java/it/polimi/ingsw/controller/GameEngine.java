package it.polimi.ingsw.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.KillShotTrack;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.SlowAnswerException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import static it.polimi.ingsw.model.board.Player.HeroName.*;
import static java.util.Collections.frequency;

/**
 * Class responsible of running a game.
 * The interaction with the user is simulated by the method receive() of VirtualView.
 * Must be updated implementing the connection with the client.
 *
 * @author BassaniRiccardo
 */

//TODO: implement the connection with the client. Finish testing.
//      understand why handleUsingPowerUp, ConvertPowerUp and Reload are sometimes called unexpectedly (maybe it is solved now).

public class GameEngine implements Runnable{

    private List<VirtualView> players;
    private VirtualView currentPlayer;
    private Board board;
    private boolean gameOver;
    private KillShotTrack killShotTrack;
    private boolean frenzy;
    private StatusSaver statusSaver;
    private Map<VirtualView, String> notifications;
    private Timer timer = new Timer(120);

    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static final String P = "Player ";


    /**
     * Constructs a GameEngine with a list of Player Controller.
     *
     * @param players           the players in the game.
     */
    public GameEngine(List<VirtualView> players){
        this.players = players;
        currentPlayer = null;
        board = null;
        gameOver = false;
        killShotTrack = null;
        frenzy = false;
        this.statusSaver = null;
        for(VirtualView p : players){
            p.setGame(this);
        }
        notifications = new HashMap<>();
    }

    /**
     *  Getters
     */
    public List<VirtualView> getPlayers() {
        return players;
    }

    public VirtualView getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    /**
     *  Setters
     */
    public void setPlayers(List<VirtualView> players) {
        this.players = players;
    }

    public void setCurrentPlayer(VirtualView currentPlayer) {
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
     * Adds a VirtualView to the game.
     *
     * @param index         the position of the new VirtualView in the VirtualView list.
     * @param p             the player connection to addList.
     */
    public void setPlayer(int index, VirtualView p) {
        this.players.set(index, p);     //this and other methods need to be synchronized
    }


    /**
     * Runs a game.
     *
     * @requires 3 <= players.size() && players.size() <= 5
     */
    public void run(){

        final int TURN_TIME = 3;
        setup();
        ExecutorService executor = Executors.newCachedThreadPool();
        while (!gameOver){
            runTurn(executor, TURN_TIME, false);
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
        //try {
        //    board.getKillShotTrack().removeSkulls(4);
        //} catch ( NotAvailableAttributeException | UnacceptableItemNumberException e){};
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
        for (VirtualView p: players) {
            p.choose("Do you want to play with the frenzy?", frenzyOptions);
        }
        for(VirtualView p : players){
            if(Integer.parseInt(waitShort(p, 20))==1) yes++;
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

        for(VirtualView p : players) {
            board.addToUpdateQueue(Updater.getModel(board, p.getModel()));
        }
        System.out.println("about to notify");
        board.notifyObservers();

    }


    /**
     * Configures the map asking the players for their preference.
     */
    private void configureMap(){

        List<Integer> mapIDs = new ArrayList<>(Arrays.asList(1,2,3,4));
        List<Integer> votes = new ArrayList<>(Arrays.asList(0,0,0,0));


        for(VirtualView p : players) {
            p.choose("Vote for the map you want:", mapIDs);
        }

        for(VirtualView p : players) {
            int vote = Integer.parseInt(waitShort(p, 20));
            votes.set(vote-1, votes.get(vote-1)+1);
        }
        int mapId = votes.indexOf(Collections.max(votes)) + 1;

        board = BoardConfigurer.configureMap(mapId);

        for(VirtualView p : players) {
            board.registerObserver(p);
            //adds virtual view as observer to board and adds another queue of updates
        }


        LOGGER.log(Level.INFO,() -> "Players voted: map " + mapId + " selected.");

    }

    /**
     * Configures the kill shot track asking the players for their preference.
     */
    private void configureKillShotTrack(){

        List<Integer> skullsOptions = new ArrayList<>(Arrays.asList(5,6,7,8));
        int totalSkullNumber = 0;

        for (VirtualView p : players) {
            p.choose("How many skulls do you want?", skullsOptions);
        }
        for (VirtualView p : players) {
            int selected = Integer.parseInt(waitShort(p, 20));
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

        for(VirtualView p : players) {
            p.choose("What hero do you want?", heroList);
            int selected = Integer.parseInt(waitShort(p, 20));
            System.out.println("selected " + selected);
            Player.HeroName selectedName = heroList.get(selected-1);
            p.setPlayer(new Player(id, selectedName, board));
            System.out.println("setplayer");
            board.getPlayers().add(p.getModel());
            System.out.println("added");
            p.getModel().setUsername(p.getName());
            heroList.remove(selectedName);
            LOGGER.log(Level.INFO,P + id + " selected " + selectedName + ".");
            p.display("You selected " + selectedName);
            id++;

        }

    }

    /**
     * Returns the player who has to play after the current one.
     *
     * @return      the next player.
     */
    public VirtualView getNextPlayer(){   //must be robust for an empty list of player

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
        timer.start();
        new TurnManager(this, board, currentPlayer, players, statusSaver,  frenzy).run();
        timer.stop();
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

    public String waitShort(VirtualView current, int timeout){
        long start = System.currentTimeMillis();
        while(!hasAnswered(current)){
            checkForSuspension();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
            if(System.currentTimeMillis()>start+timeout*10000){
                return "1"; //what to return?
            }
        }
        //check how many players are left!
        return notifications.remove(current);
    }

    public String wait(VirtualView current) throws SlowAnswerException{
        while(!hasAnswered(current)){
            checkForSuspension();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
            if(timer.isOver()){
                throw new SlowAnswerException();
            }
        }
        //check how many players are left!
        return notifications.remove(current);
    }

    public List<VirtualView> waitAll(int timeout) {
        long start = System.currentTimeMillis();
        boolean loop = true;
        List<VirtualView> list = new ArrayList<>();
        while(loop){
            checkForSuspension();
            loop = false;
            for(VirtualView p : players){
                if(!hasAnswered(p)&&!p.isSuspended()){
                    loop=true;
                } else if(!list.contains(p)){
                    list.add(p);
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
            if(System.currentTimeMillis()>start+timeout*1000){
                break;
            }
        }
        return list;    //contains only virtualviews that answered
    }


        public boolean hasAnswered(VirtualView p){
        return notifications.containsKey(p);
    }

    public void notify(VirtualView p, String message){
        //only do if it is waiting for a message!
        notifications.putIfAbsent(p, message);
        //catch exceptions
        //if a message is already there, it is ignored
    }

    public Map<VirtualView, String> getNotifications(){
        //also set waiting to true?
        return notifications;
    }

    private void checkForSuspension(){
        List<VirtualView> justSuspended = new ArrayList<>();
        for(VirtualView v : players){
            if(v.isJustSuspended()) {
                justSuspended.add(v);
                v.setJustSuspended(false);
            }
        }
        for(VirtualView v : justSuspended){
            for(VirtualView p : players){
                p.display("Player " + v.getName() + " was disconnected");
            }
        }

        //other behaviours depending on n of connected players
    }

}
