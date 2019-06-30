package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.KillShotTrack;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.WeaponSquare;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.VirtualView;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.controller.ServerMain.MIN_PLAYERS;
import static it.polimi.ingsw.model.board.Player.HeroName.*;
import static java.util.Collections.frequency;
import static it.polimi.ingsw.controller.ServerMain.SLEEP_TIMEOUT;
import static it.polimi.ingsw.network.server.VirtualView.ChooseOptionsType.*;

//FIXME: game setup temporarily commented out to allow for quicker testing

/**
 * Class responsible of running a game.
 *
 * @author BassaniRiccardo
 */

public class GameEngine implements Runnable{

    private List<VirtualView> players;
    private VirtualView currentPlayer;
    int frenzyActivator;
    private Board board;
    private KillShotTrack killShotTrack;
    private Timer timer;
    private StatusSaver statusSaver;

    private boolean gameOver;
    private boolean frenzy;
    private boolean lastFrenzyPlayer;
    private boolean exitGame;

    private final Map<VirtualView, String> notifications;
    private List<VirtualView> resuming;

    private boolean endphaseSimulation;
    private int turnDuration;

    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static final String P = "Player ";

    private static final String WAIT_SHORT_MESSAGE = "Your answer did not arrive in time. You have not been suspended, but a default value has been selected. Press 1 and enter to proceed.";
    private static final String WAS_DISCONNECTED = " was disconnected";
    private static final String IS_BACK_MESSAGE = " is back!";
    private static final String YOU_ARE_BACK_MESSAGE = "You are back!";
    private static final String DEFAULT_ANSWER = "1";

    private static final String MAP_REQUEST = "Vote for the map you want:";
    private static final String MAP_SELECTED = "Voting ended. Selected map ";
    private static final String SKULL_NUMBER_REQUEST = "How many skulls do you want?";
    private static final String SKULL_NUMBER_SELECTED = "Voting ended. Number of skulls selected: ";
    private static final String HERO_REQUEST = "What hero do you want?";
    private static final String HERO_SELECTED = "You selected ";
    private static final String FRENZY_REQUEST = "Do you want to play with the frenzy?";
    private static final String YES ="Yes";
    private static final String NO ="No";
    private static final String FRENZY_ACTIVE = "Voting ended: Frenzy active.";
    private static final String FRENZY_NOT_ACTIVE = "Voting ended: Frenzy not active.";
    private static final String CRIES_OUT = " cries out: \n";

    private static final String ENTER = "\n";
    private static final String DOT = ".";
    private static final String COLON = ": ";
    private static final String EXCLAMATION_POINT = "!";

    private static final String NOT_ENOUGH_PLAYER_GAME_OVER = "Game Over : less then three players in the game.";
    private static final String WINNER_MESSAGE = "\n\nGAME OVER\n\nYou Won!";
    private static final String DRAW_MESSAGE = "\n\nGAME OVER\n\nYou and other players made the most points but did not kill anyone. Shame on you! The game ends with a draw.";
    private static final String POSITION_MESSAGE = "\n\nGAME OVER\n\nYour position: ";
    private static final String LEADERBOARD = "\n\nLeaderboard:\n";
    private static final String POINTS = " points\n";
    private static final String KILLSHOT_TRACK_ABSENT_EX = "NotAvailableAttributeException thrown while configuring the kill shot track";

    private static final String ENDPHASE_SIMULATION = "endPhaseSimulation";
    private static final String DEFAULT_ENDPHASE_SIMULATION = "false";
    private static final String TURN_DURATION = "turnDuration";
    private static final String DEFAULT_TURN_DURATION = "60";

    private static final int SETUP_TIMEOUT = 10;
    private static final List<Integer> MAP_ID_OPTIONS = new ArrayList<>(Arrays.asList(1,2,3,4));
    private static final List<Integer> EMPTY_MAP_VOTES = Arrays.asList(0,0,0,0);
    private static final List<Integer> SKULL_NUMBER_OPTIONS = new ArrayList<>(Arrays.asList(5,6,7,8));

    /**
     * Constructs a GameEngine with a list of Player Controller.
     *
     * @param players           the players in the game.
     */
    GameEngine(List<VirtualView> players){

        this.players = players;
        this.currentPlayer = null;
        this.frenzyActivator = 0;
        this.board = null;
        this.killShotTrack = null;
        this.statusSaver = null;

        this.gameOver = false;
        this.frenzy = false;
        this.lastFrenzyPlayer = false;
        this.exitGame = false;

        for(VirtualView p : players){
            p.setGame(this);
        }

        this.notifications = new HashMap<>();
        this.resuming = new ArrayList<>();

        LOGGER.log(Level.FINE, "Initialized GameEngine {0}", this);
    }


    /**
     *  Getters
     */
    public synchronized  List<VirtualView> getPlayers() {
        return players;
    }

    public VirtualView getCurrentPlayer() {
        return currentPlayer;
    }

    public int getFrenzyActivator() {return frenzyActivator;}

    public Board getBoard() {
        return board;
    }

    boolean isLastFrenzyPlayer() {return lastFrenzyPlayer; }

    boolean isGameOver() {return gameOver; }


    /**
     *  Setters
     */
    public synchronized void setPlayers(List<VirtualView> players) {
        this.players = players;
        LOGGER.log(Level.FINEST, "Set players to a new list sized {0}", players.size());
    }

    void setCurrentPlayer(VirtualView currentPlayer) {
        this.currentPlayer = currentPlayer;
        this.board.setCurrentPlayer(currentPlayer.getModel());
        LOGGER.log(Level.FINE, "CurrentPlayer set to " + currentPlayer.getName());
    }


    /**
     * Only for testing
     */
    public void setBoard(Board board) {
        this.board = board;
    }


    /**
     * Runs a game.
     *
     * @requires 3 <= players.size() && players.size() <= 5
     */
    public void run(){

        loadParams();
        this.timer = new Timer(turnDuration);

        try {

            LOGGER.log(Level.FINE, "GameEngine running");

            /*
            try {
                setup();
            }catch (NotEnoughPlayersException e) {
                for (VirtualView p : players) {
                    p.showEnd("There are no enough player to start the game. Try to join another game.");
                }
                ServerMain.getInstance().untrackGame(this);
                return;
            }
            */

            fakeSetup();

            if (endphaseSimulation) {
                try {
                    for (VirtualView p : players) {
                        board.addToUpdateQueue(Updater.getModel(board, p.getModel()), p);
                    }
                    simulateTillEndphase();
                } catch (NotAvailableAttributeException | UnacceptableItemNumberException | NoMoreCardsException | WrongTimeException e) {
                    LOGGER.log(Level.SEVERE, "Exception thrown while simulating the game", e);
                }
            }

            else {
                battleCry();
            }

            while (!gameOver) {
                LOGGER.log(Level.FINE, "Running turn");
                allowPlayersToResume();
                try {
                    try {
                        runTurn(false);
                    } catch (SlowAnswerException ex) {
                        currentPlayer.suspend();
                        checkForSuspension();
                    }
                } catch (NotEnoughPlayersException e) {
                    exitGame = true;
                    break;
                }

                if (killShotTrack.getSkullsLeft() == 0) {
                    LOGGER.log(Level.FINE, "There are no skulls left, managing the end of the game");
                    try {
                        manageGameEnd();
                    } catch (NotEnoughPlayersException e) {
                        exitGame = true;
                        break;
                    } catch (SlowAnswerException ex) {
                        currentPlayer.suspend();
                    }
                }
                changePlayer();

            }
            resolve();

            ServerMain.getInstance().untrackGame(this);

        } catch (Exception e) {e.printStackTrace(); throw e;}

    }


    /**
     * Sets up the game.
     *
     * @throws NotEnoughPlayersException    if thrown by configureMap(), configureKillShotTrack(), configureFrenzyOption() or configurePlayers().
     */
    public void setup() throws NotEnoughPlayersException{

        LOGGER.log(Level.FINE,"All the players are connected.");
        configureMap();
        configureKillShotTrack();
        BoardConfigurer.configureDecks(board);
        LOGGER.log(Level.INFO,"Decks configured.");

        try {
            BoardConfigurer.setAmmoTilesAndWeapons(board);
            LOGGER.log(Level.INFO,"Ammo tiles and weapons set.");
        } catch (UnacceptableItemNumberException | NoMoreCardsException e) {LOGGER.log(Level.SEVERE,"Exception thrown while setting ammo tiles and weapons", e);}

        configureFrenzyOption();

        configurePlayers();

        setCurrentPlayer(players.get(0));
        statusSaver = new StatusSaver(board);
    }


    /**
     * Configures the map asking the players for their preference.
     *
     * @throws      NotEnoughPlayersException           if thrown by waitShort().
     */
    private void configureMap() throws NotEnoughPlayersException{

        List<Integer> votes = new ArrayList<>(EMPTY_MAP_VOTES);

        for(VirtualView p : players) {
            p.display("");
            p.choose(CHOOSE_STRING.toString(), MAP_REQUEST, MAP_ID_OPTIONS, SETUP_TIMEOUT);
        }

        for(VirtualView p : players) {
            int vote = Integer.parseInt(waitShort(p, SETUP_TIMEOUT));
            votes.set(vote-1, votes.get(vote-1)+1);
        }
        int mapId = votes.indexOf(Collections.max(votes)) + 1;

        board = BoardConfigurer.configureMap(mapId);

        for(VirtualView p : players) {
            p.display(MAP_SELECTED + mapId + DOT);
            board.registerObserver(p);
        }

        LOGGER.log(Level.INFO,"Players voted: map {0} selected.", mapId);

    }


    /**
     * Configures the kill shot track asking the players for their preference.
     *
     * @throws      NotEnoughPlayersException           if thrown by waitShort().
     */
    private void configureKillShotTrack() throws NotEnoughPlayersException{

        int totalSkullNumber = 0;

        for (VirtualView p : players) {
            p.choose(CHOOSE_STRING.toString(), SKULL_NUMBER_REQUEST, SKULL_NUMBER_OPTIONS, SETUP_TIMEOUT);
        }
        for (VirtualView p : players) {
            int selected = Integer.parseInt(waitShort(p, SETUP_TIMEOUT));
            totalSkullNumber = totalSkullNumber + selected + 4;
        }

        int averageSkullNumber = Math.round((float)totalSkullNumber/(float)players.size());
        BoardConfigurer.configureKillShotTrack(averageSkullNumber, board);
        try {
            this.killShotTrack = board.getKillShotTrack();
        } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE, KILLSHOT_TRACK_ABSENT_EX, e);}

        for (VirtualView p : players) {
            p.display(SKULL_NUMBER_SELECTED + averageSkullNumber + DOT);
        }

        LOGGER.log(Level.INFO,() -> "Players voted. Number of skulls: " + averageSkullNumber + ".");
    }


    /**
     * Configures the frenzy option asking the players for their preference.
     *
     * @throws      NotEnoughPlayersException           if thrown by waitShort().
     */
    private void configureFrenzyOption() throws NotEnoughPlayersException{

        List<String> frenzyOptions = new ArrayList<>();
        int yes = 0;
        int no = 0;
        frenzyOptions.addAll(Arrays.asList(YES, NO));
        for (VirtualView p: players) {
            p.choose(CHOOSE_STRING.toString(), FRENZY_REQUEST, frenzyOptions, SETUP_TIMEOUT);
        }
        for(VirtualView p : players){
            if(Integer.parseInt(waitShort(p, SETUP_TIMEOUT))==1) yes++;
            else no++;
        }
        if (yes>=no) {
            frenzy=true;
            LOGGER.log(Level.INFO,"Frenzy active.");
        }
        else  LOGGER.log(Level.INFO,"Frenzy not active.");

        for(VirtualView p : players){
            if(frenzy) p.display(FRENZY_ACTIVE);
            else p.display(FRENZY_NOT_ACTIVE);
        }
    }


    /**
     * Adds to the board the players connected to the game.
     * Asks the players which hero they want, providing only the remaining options.
     *
     * @throws      NotEnoughPlayersException           if thrown by waitShort().
     */
    private void configurePlayers() throws NotEnoughPlayersException{

        List<Player.HeroName> heroList = new ArrayList<>(Arrays.asList(D_STRUCT_OR, BANSHEE, DOZER, VIOLET, SPROG));
        int id = 1;

        for(VirtualView p : players) {
            p.choose(CHOOSE_STRING.toString(), HERO_REQUEST, heroList, SETUP_TIMEOUT);
            int selected = Integer.parseInt(waitShort(p, SETUP_TIMEOUT));
            LOGGER.log(Level.INFO, "selected {0}", selected);
            LOGGER.log(Level.INFO, "index: {0}", players.indexOf(p));
            LOGGER.log(Level.INFO, "{0}", heroList);
            Player.HeroName selectedName = heroList.get(selected-1);
            p.setPlayer(new Player(id, selectedName, board));
            LOGGER.log(Level.INFO,"setplayer");
            board.getPlayers().add(p.getModel());
            LOGGER.log(Level.INFO, "added");
            p.getModel().setUsername(p.getName());
            heroList.remove(selectedName);
            String msg = P + id + " selected " + selectedName + ".";
            LOGGER.log(Level.INFO,msg);
            p.display(HERO_SELECTED + selectedName);
            id++;
        }

    }


    /**
     * Asks each player to input a battle-cry.
     * After everybody chose his battle-cry, shows every player the other players battle-cries.
     */
    public void battleCry() {
        List<String> battleCries = new ArrayList<>();
        for (VirtualView p : players) {
            battleCries.add(p.getBattlecry());
        }
        for (VirtualView p : players) {
            StringBuilder builder = new StringBuilder();
            for (VirtualView otherPlayer : players){
                if (!otherPlayer.equals(p)){
                    if (!builder.toString().isEmpty())
                        builder.append(ENTER);
                    builder.append(otherPlayer.getModel().userToString() + CRIES_OUT + battleCries.get(players.indexOf(otherPlayer)) + ENTER);
                }
            }
            p.display(builder.toString());
        }
        try{
            Thread.sleep(1000); //give them time to read
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "Skipped waiting time", e);
        }
    }


    /**
     * Returns the player who has to play after the current one.
     *
     * @return      the next player.
     */
    VirtualView getNextPlayer(){   //must be robust for an empty list of player

        int ind = players.indexOf(currentPlayer);
        ind++;

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
    private void resolve(){

        if (exitGame) {
            LOGGER.log(Level.INFO,"Not enough player in the game. The game ends, points are added to the players according to the kill shot track.");
            for (VirtualView v : players) {
                v.display(NOT_ENOUGH_PLAYER_GAME_OVER);
            }
        }
        else if (!frenzy) LOGGER.log(Level.INFO,"The last skull has been removed. Points are added to the players according to the kill shot track.");
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
     * @param frenzy                whether the frenzy is active during the turn.
     * @throws NotEnoughPlayersException        if thrown by TurnManager.runTurn().
     * @throws SlowAnswerException              if thrown by TurnManager.runTurn().
     */
    private void runTurn (boolean frenzy) throws NotEnoughPlayersException, SlowAnswerException{
        for(VirtualView p : players) {
            board.addToUpdateQueue(Updater.getModel(board, p.getModel()), p);
        }
        board.notifyObservers();
        timer.start();
        try {
            new TurnManager(this, board, currentPlayer, players, statusSaver, frenzy, timer).runTurn();
        } catch (Exception e ){ e.printStackTrace(); throw e;}
        timer.stop();
    }


    /**
     * Updates the current player and checks if there are enough players to continue the game.
     */
    void changePlayer(){

        setCurrentPlayer(getNextPlayer());
        long playerCount = players.stream().filter(x->!x.isSuspended()).count();
        if(playerCount<3){
            gameOver = true;
        }

    }


    /**
     * Manages the end of the game, depending on whether the frenzy mode is active.
     */
    private void manageGameEnd() throws NotEnoughPlayersException, SlowAnswerException{

        if (!frenzy) {
            gameOver = true;
        }
        else {
            frenzyActivator = currentPlayer.getModel().getId();
            changePlayer();
            for (Player p : board.getPlayers()){
                if (p.getDamages().isEmpty()){
                    p.setFlipped(true);
                    p.setPointsToGive(2);
                }
                if (p.getId() > frenzyActivator){
                    p.setStatus(Player.Status.FRENZY_1);
                }
                else p.setStatus(Player.Status.FRENZY_2);
            }
            for (VirtualView v : players)
                v.display("FRENZY STARTED!!!");
            LOGGER.log(Level.INFO,"\nNo more skulls left:\n\nFrenzy mode!!!!!!\n");
            for (int i=0; i<players.size(); i++){
                if (i == players.size()-1)
                    lastFrenzyPlayer = true;
                runTurn(true);
                changePlayer();
            }
            gameOver = true;
        }

    }


    /**
     * Decides the winner.
     */
    private void gameOver(){

        LOGGER.log(Level.INFO, "\nGame over.\n");
        int maxScore = players.get(0).getModel().getPoints();
        List<VirtualView> winners = new ArrayList<>();
        boolean existsKill = false;
        List<VirtualView> suspended = new ArrayList<>();
        for (VirtualView p : players){
            if (p.isSuspended())
                suspended.add(p);
        }
        players.removeAll(suspended);
        for (VirtualView p : players){
            if (p.getModel().getPoints() == maxScore) {
                winners.add(p);
                if (killShotTrack.getKillers().contains(p.getModel())){
                    existsKill = true;
                }
            }
        }

        if (existsKill || winners.size()==1){
            players.get(0).showEnd(addLeaderboard(WINNER_MESSAGE));
            for (int i = 1; i < players.size(); i++) {
                showToLoser(i);
            }
        }
        else {
            for (VirtualView v : winners){
                v.showEnd(addLeaderboard(DRAW_MESSAGE));
            }
            for (int i = winners.size(); i < players.size(); i++) {
                showToLoser(i);
            }
        }

    }


    /**
     * Sends to a specific player a message showing his position at the end of the game.
     * Sent only to the player who did non win the game
     *
     * @param i     the index of the player the message must be sent to.
     */
    private void showToLoser(int i){
        LOGGER.log(Level.INFO, P + players.get(i).getModel().getId() + ", " + players.get(i).getModel().getPoints() + " points.");
        int pos = i + 1;
        players.get(i).showEnd(addLeaderboard(POSITION_MESSAGE + pos + EXCLAMATION_POINT));
    }


    /**
     * Append to a specified string a string containing information about the leaderboard.
     *
     * @param s     the string to modify
     * @return      the modified string
     */
    String addLeaderboard(String s){
        StringBuilder builder = new StringBuilder();
        builder.append(s);
        builder.append(LEADERBOARD);
        for (VirtualView v : players){
            builder.append(v.getModel().getUsername() + COLON + v.getModel().getPoints() + POINTS);
        }
        return builder.toString();
    }


    /**
     * Waits for a player's input. If player takes too long to answer, default answer "1" is returned. Used in the game configuration phase.
     *
     * @param current       the player to wait for.
     * @param timeout       timeout.
     * @throws NotEnoughPlayersException        if less than the minimum number of players are left.
     * @return the player's answer.
     */
    String waitShort(VirtualView current, int timeout) throws NotEnoughPlayersException{
        long start = System.currentTimeMillis();
        long timeoutMillis = TimeUnit.SECONDS.toMillis(timeout);
        LOGGER.log(Level.INFO ,"Waiting for {0} answer", current.getName());
        while(!hasAnswered(current)){
            checkForSuspension();
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMEOUT);
            }catch(InterruptedException ex){
                LOGGER.log(Level.FINE,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
            if(System.currentTimeMillis()>start+timeoutMillis||current.isSuspended()){
                LOGGER.log(Level.INFO,"Timeout ran out while waiting for " + current.getName() +". Returning default value");
                synchronized (notifications){
                    notifications.putIfAbsent(current, DEFAULT_ANSWER);
                }
                current.display(WAIT_SHORT_MESSAGE);
            }
        }
        LOGGER.log(Level.INFO, "Done waiting");
        checkForSuspension();
        synchronized (notifications) {
            return notifications.get(current);
        }
    }


    /**
     * Wait for a player's input for as long as needed (or until the turn timer runs out).
     *
     * @param current                       the player to wait for.
     * @throws SlowAnswerException          if the turn timer runs out.
     * @throws NotEnoughPlayersException    if less than the minimum number of players are left.
     * @return                              the player's answer.
     */
    String wait(VirtualView current) throws SlowAnswerException, NotEnoughPlayersException{
        LOGGER.log(Level.INFO ,"Waiting for {0} answer", current);
        while(!hasAnswered(current)){
            checkForSuspension();
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIMEOUT);
            }catch(InterruptedException ex){
                LOGGER.log(Level.INFO,"Skipped waiting time.");
                Thread.currentThread().interrupt();
            }
            if(timer.isOver()){
                LOGGER.log(Level.FINE, "Player {0} took too long to answer and will be suspended", current.getName());
                throw new SlowAnswerException("Maximum time exceeded for the user to answer.");
            }
        }
        LOGGER.log(Level.INFO, "Done waiting");
        synchronized (notifications) {
            return notifications.get(current);
        }
    }


    /**
     * States if a certain player's has answered a request from the server
     *
     * @param p         player to check
     * @return          true if player has answered, else false
     */
    private boolean hasAnswered(VirtualView p){
        synchronized (notifications) {
            return notifications.containsKey(p);
        }
    }


    /**
     * Method called externally to notify changes in an observed object. Usually called by a VirtualView to notify incoming messages
     *
     * @param p         the observed object
     * @param message   a notification from the observed object
     */
    public void notify(VirtualView p, String message){
        try {
            synchronized (notifications) {
                notifications.putIfAbsent(p, message);
            }
            LOGGER.log(Level.INFO, "{0} just notified the GameEngine", p.getName());
        }catch (Exception ex){
            LOGGER.log(Level.SEVERE, "Issue with being notified by " + p.getName(), ex);
        }
    }


    /**
     * Getter for notifications (map connecting each player to a list of messags they sent)
     *
     * @return      mapping of players to incoming messages
     */
    public Map<VirtualView, String> getNotifications(){
        synchronized (notifications) {
            return notifications;
        }
    }


    /**
     * Checks if a player was suspended recently and, if so, notifies other players.
     *
     * @throws NotEnoughPlayersException if less than the minimum number of  players are left
     */
    private synchronized void checkForSuspension() throws NotEnoughPlayersException{
        List<VirtualView> justSuspended = new ArrayList<>();
        for(VirtualView v : players){
            if(v.isJustSuspended()) {
                justSuspended.add(v);
                v.setJustSuspended(false);
            }
        }
        for(VirtualView v : justSuspended){
            for(VirtualView p : players){
                p.display(P + v.getName() + WAS_DISCONNECTED);
                synchronized (notifications) {
                    notifications.remove(p);
                }
            }
            LOGGER.log(Level.INFO, "Notified players of the disconnection of {0}", justSuspended);
        }
        if (players.stream().filter(x->!x.isSuspended()).count() < MIN_PLAYERS) throw new NotEnoughPlayersException("Not enough players to continue the game. Game over");
    }


    /**
     * Sets the game in such a way that only a skull is left on the killshot track.
     * Three players are set on the board and they are given some damages.
     * The killshot track, the player points, deaths and status are set in a coherent way.
     * The method is used to allow to reach quickly the end of the game.
     *
     * @throws NotAvailableAttributeException           if it is thrown by getKillShotTrack().
     */
    void simulateTillEndphase() throws NotAvailableAttributeException, NoMoreCardsException, WrongTimeException, UnacceptableItemNumberException {

        Player p1 = board.getPlayers().get(0);
        Player p2 = board.getPlayers().get(1);
        Player p3 = board.getPlayers().get(2);

        List<Player> simulationPlayers = new ArrayList<>(Arrays.asList(p1, p2, p3));

        for (Player p : simulationPlayers) {
            p.setInGame(true);
            p.addAmmoPack(new AmmoPack(2,2,2));
            p.setPosition(board.getSpawnPoints().get(simulationPlayers.indexOf(p) % 3));
            for (int i = 0; i < 3; i++) {
                p.collect(((WeaponSquare)p.getPosition()).getWeapons().get(i));
                ((WeaponSquare)p.getPosition()).addCard();
            }

            p.drawPowerUp();
            p.drawPowerUp();
            p.drawPowerUp();
        }

        //simulates all the previous deaths
        BoardConfigurer.configureKillShotTrack(1, board);
        try {
            this.killShotTrack = board.getKillShotTrack();
        } catch (NotAvailableAttributeException e) {
            LOGGER.log(Level.SEVERE, KILLSHOT_TRACK_ABSENT_EX, e);
        }
        p1.addDeath();
        p2.addDeath();
        p3.addDeath();
        p1.addDeath();
        p3.addDeath();
        board.getKillShotTrack().getKillers().addAll(Arrays.asList(p2, p3, p1, p2, p1));
        p1.setPoints(23);
        p2.setPoints(27);
        p3.setPoints(17);
        p1.setPointsToGive(4);
        p2.setPointsToGive(6);
        p3.setPointsToGive(4);

        //assigns damages and update status
        for (int i = 0; i < 2; i++)
            p1.getDamages().add(p2);
            p1.addMarks(1, p2);
        p1.setStatus(Player.Status.BASIC);

        for (int i = 0; i < 4; i++)
            p2.getDamages().add(p1);
        for (int i = 0; i < 6; i++)
            p2.getDamages().add(p3);
        p2.setStatus(Player.Status.ADRENALINE_2);
        p2.addMarks(1, p1);
        p2.addMarks(1, p3);

        for (int i = 0; i < 4; i++){
            p3.getDamages().add(p2);
        }
        p3.getDamages().add(p1);
        p3.getDamages().add(p2);
        p3.addMarks(2, p1);
        p3.setStatus(Player.Status.ADRENALINE_1);

    }


    /**
     * Checks if a certain player can resume, and if so, adds it to a waiting list
     *
     * @param p     player trying to resume
     * @return      true if resuming is possible, else false
     */
    synchronized boolean tryResuming(VirtualView p){
        for (VirtualView v : players){
            if (statusSaver!=null&&v.isSuspended()&&!resuming.stream().map(VirtualView::getName).collect(Collectors.toList()).contains(p)){
                resuming.add(p);
                LOGGER.log(Level.INFO, "{0} was added to the list of resuming players.", p.getName());
                return true;
            }
        }
        return false;
    }


    /**
     * After every turn, this function is called to allow players who have requested to resume back in the game.
     */
    private synchronized void allowPlayersToResume(){
        List<VirtualView> temp = new ArrayList<>(resuming);
        for(VirtualView v : temp){
            for(VirtualView old : players){
                if(v.getName().equals(old.getName())&&old.isSuspended()){
                    players.set(players.indexOf(old), v);
                    board.registerObserver(v);
                    resuming.remove(v);
                    ServerMain.getInstance().getPlayers().remove(old);
                    for(VirtualView p : players){
                        if(p.equals(v)){
                            p.display(YOU_ARE_BACK_MESSAGE);
                        }else{
                            p.display(v.getName() + IS_BACK_MESSAGE);
                        }
                    }
                    LOGGER.log(Level.INFO, "{0} successfully resumed and can now play.", v.getName());
                    break;
                }
            }
        }
    }


    /**
     * Loads parameters from properties
     */
    private void loadParams() {
        Properties prop = ServerMain.getInstance().loadConfig();
        this.endphaseSimulation = Boolean.parseBoolean(prop.getProperty(ENDPHASE_SIMULATION, DEFAULT_ENDPHASE_SIMULATION));
        this.turnDuration = Integer.parseInt(prop.getProperty(TURN_DURATION, DEFAULT_TURN_DURATION));
    }


    /**
     * A fake set up to allow for quicker testing.
     */
    void fakeSetup(){

        board = BoardConfigurer.configureMap(4);

        for(VirtualView p : players) {
            board.registerObserver(p);
        }

        BoardConfigurer.configureKillShotTrack(6, board);
        try {
            this.killShotTrack = board.getKillShotTrack();
        } catch (NotAvailableAttributeException e) {LOGGER.log(Level.SEVERE,KILLSHOT_TRACK_ABSENT_EX, e);}

        BoardConfigurer.configureDecks(board);

        try {
            BoardConfigurer.setAmmoTilesAndWeapons(board);
        } catch (UnacceptableItemNumberException | NoMoreCardsException e) {LOGGER.log(Level.SEVERE,"Exception thrown while setting ammo tiles and weapons", e);}

        List<Player.HeroName> heroList = new ArrayList<>(Arrays.asList(D_STRUCT_OR, BANSHEE, DOZER, VIOLET, SPROG));
        int id = 1;

        for(VirtualView p : players) {
            Player.HeroName selectedName = heroList.get(0);
            p.setPlayer(new Player(id, selectedName, board));
            board.getPlayers().add(p.getModel());
            p.getModel().setUsername(p.getName());
            heroList.remove(selectedName);
            String msg = P + id + " selected " + selectedName + ".";
            LOGGER.log(Level.INFO, msg);
            id++;
        }

        frenzy = true;

        setCurrentPlayer(players.get(0));
        statusSaver = new StatusSaver(board);
    }

}
