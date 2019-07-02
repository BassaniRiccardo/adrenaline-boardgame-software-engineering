package it.polimi.ingsw.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.network.server.TCPVirtualView;
import it.polimi.ingsw.network.server.VirtualView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test the methods of GameEngine, simulating the players answers.
 * The players always select the first option.
 *
 * @author BassaniRiccardo
 */
public class GameEngineTest {


    /**
     * A subclass of VirtualView simulating players answers.
     */
    class DummyVirtualView extends VirtualView{
        @Override
        public void refresh() {        }

        @Override
        public void shutdown() {        }

        @Override
        public void showSuspension() {        }

        @Override
        public void showEnd(String message) {      }

        @Override
        public void choose(String type, String msg, List<?> options) {
            notifyObservers("1");
        }

        @Override
        public void choose(String type, String msg, List<?> options, int timeoutSec) {
            notifyObservers("1");
        }

        @Override
        public void display(String msg) { }

        @Override
        public String getInputNow(String msg, int max) {
            return "1";
        }

        @Override
        public int chooseNow(String type, String msg, List<?> options) {
            return 1;
        }

        @Override
        public void update(JsonObject jsonObject) {        }
    }


    /**
     * Tests the method resolveWinner().
     * An arbitrary number of points is assigned to the players.
     * Tests the method in the event that a single player is the winner.
     * Checks that the player connections are ordinated according to the leaderboard.
     */
    @Test
    public void resolveWinner() {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        try {
            gameEngine.setup();
        }catch(NotEnoughPlayersException e){
            //
        }

        VirtualView distructor = connections.get(0);
        VirtualView banshee = connections.get(1);
        VirtualView dozer = connections.get(2);
        VirtualView violet = connections.get(3);
        VirtualView sprog = connections.get(4);

        distructor.getModel().setPoints(31);
        banshee.getModel().setPoints(11);
        dozer.getModel().setPoints(34);
        violet.getModel().setPoints(31);
        sprog.getModel().setPoints(17);

        Board b = gameEngine.getBoard();
        List<Player> killers = new ArrayList<>();
        try {
            killers = b.getKillShotTrack().getKillers();
        } catch (NotAvailableAttributeException e){e.printStackTrace();}

        killers.add(distructor.getModel());
        killers.add(distructor.getModel());
        killers.add(banshee.getModel());
        killers.add(dozer.getModel());
        killers.add(violet.getModel());
        killers.add(distructor.getModel());

        //Checks that the player connections are ordinated according to the ID.
        assertEquals(Arrays.asList(distructor, banshee, dozer, violet, sprog), gameEngine.getPlayers());
        assertTrue(gameEngine.getLeaderboard().isEmpty());

        gameEngine.resolve();

        //Checks that the player connections are ordinated according to the leaderboard.
        assertEquals(Arrays.asList(distructor, dozer, violet, banshee, sprog), gameEngine.getLeaderboard());

    }

    /**
     * Tests the method GameEngine.resolveWinner(), by displaying the operation performed on the console.
     * An arbitrary number of points is assigned to the players.
     * Tests the method in the case that the second and the third players have the same amount of points.
     * Checks that the player connections are ordinated according to the leaderboard.
     */
    @Test
    public void resolveBrokenTie() {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        try {
            gameEngine.setup();
        }catch(NotEnoughPlayersException e){
            //
        }

        VirtualView distructor = connections.get(0);
        VirtualView banshee = connections.get(1);
        VirtualView dozer = connections.get(2);
        VirtualView violet = connections.get(3);
        VirtualView sprog = connections.get(4);

        distructor.setName("destructor");
        banshee.setName("banshee");
        dozer.setName("dozer");
        violet.setName("violet");
        sprog.setName("sprog");


        distructor.getModel().setPoints(34);
        banshee.getModel().setPoints(11);
        dozer.getModel().setPoints(34);
        violet.getModel().setPoints(31);
        sprog.getModel().setPoints(17);

        try {
            gameEngine.getBoard().getKillShotTrack().getKillers().add(banshee.getModel());
            gameEngine.getBoard().getKillShotTrack().getKillers().add(violet.getModel());
        } catch (NotAvailableAttributeException e){e.printStackTrace();}


        //Checks that the player connections are ordinated according to the ID.
        assertEquals(Arrays.asList(distructor, banshee, dozer, violet, sprog), gameEngine.getPlayers());

        gameEngine.resolve();

        //Checks that the player connections are ordinated according to the leaderboard.
        assertEquals(Arrays.asList(violet, distructor, dozer, banshee, sprog), gameEngine.getLeaderboard());

    }



    /**
     * Tests the method manageEnd().
     * An arbitrary number of points is assigned to the players.
     * Tests the method in the event that a single player is the winner.
     * Checks that the player connections are ordinated according to the leaderboard.
     */
    @Test
    public void manageEnd() throws NotEnoughPlayersException, SlowAnswerException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        VirtualView distructor = connections.get(0);
        VirtualView banshee = connections.get(1);
        VirtualView dozer = connections.get(2);
        VirtualView violet = connections.get(3);
        VirtualView sprog = connections.get(4);

        distructor.getModel().setPoints(31);
        banshee.getModel().setPoints(11);
        dozer.getModel().setPoints(34);
        violet.getModel().setPoints(31);
        sprog.getModel().setPoints(17);

        distructor.getModel().sufferDamage(3, banshee.getModel());

        Board b = gameEngine.getBoard();
        List<Player> killers = new ArrayList<>();
        try {
            killers = b.getKillShotTrack().getKillers();
        } catch (NotAvailableAttributeException e){e.printStackTrace();}

        killers.add(distructor.getModel());
        killers.add(distructor.getModel());
        killers.add(banshee.getModel());
        killers.add(dozer.getModel());
        killers.add(violet.getModel());
        killers.add(distructor.getModel());

        gameEngine.setTimer(new Timer(10));
        gameEngine.manageGameEnd();
        assertEquals(1, gameEngine.getFrenzyActivator());
        for (VirtualView v : gameEngine.getPlayers()) {
            if (!v.equals(distructor)) {
                assertSame(Player.Status.FRENZY_1, v.getModel().getStatus());
                assertTrue(v.getModel().isFlipped());
            }
            else{
                assertSame(Player.Status.FRENZY_2, v.getModel().getStatus());
                assertFalse(v.getModel().isFlipped());
            }
        }

    }






    /**
     * Tests the method configureMap().
     * Since all the answer are "1", the map ID is set to 1.
     *
     * @throws NotEnoughPlayersException if thrown by configureMap().
     */
    @Test
    public void configureMap() throws NotEnoughPlayersException{

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);

        gameEngine.configureMap();

        assertEquals(1, gameEngine.getBoard().getId());
    }

    /**
     * Tests the method configureMap().
     * Since all the answer are "5", the number of skulls is set to 5.
     *
     * @throws NotEnoughPlayersException if thrown by configureMap() or configureKillShotTrack().
     * @throws NotAvailableAttributeException if thrown by getKillShotTrack().
     */
    @Test
    public void configureKillShotTrack() throws NotEnoughPlayersException, NotAvailableAttributeException{

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.configureMap();

        assertEquals(0, gameEngine.getBoard().getKillShotTrack().getSkullsLeft());

        gameEngine.configureKillShotTrack();

        assertEquals(5, gameEngine.getBoard().getKillShotTrack().getSkullsLeft());
    }

    /**
     * Tests the method configureFrenzyOptions().
     * Since all the answer are "YES", the frenzy is set to true.
     *
     * @throws NotEnoughPlayersException if thrown by configureFrenzyOptions().
     */
    @Test
    public void configureFrenzyOptions() throws NotEnoughPlayersException{

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);

        assertFalse(gameEngine.isFrenzy());

        gameEngine.configureFrenzyOption();

        assertTrue(gameEngine.isFrenzy());

    }


    /**
     * Tests the method configurePlayers().
     * Since all the answer are "1", then the order of selected heroes is D_struct_or, Banshee, Dozer, Violet, Sprog.
     *
     * @throws NotEnoughPlayersException if thrown by configureMap() or configurePlayers().
     */
    @Test
    public void configurePlayers() throws NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.configureMap();

        assertTrue(gameEngine.getBoard().getPlayers().isEmpty());

        gameEngine.configurePlayers();

        assertEquals("[Player 1 : (D_struct_or), Player 2 : (Banshee), Player 3 : (Dozer), Player 4 : (Violet), Player 5 : (Sprog)]", gameEngine.getBoard().getPlayers().toString());

    }


    /**
     * Tests the method setup(), in the case the first player selected destructor.
     *
     * @throws NotEnoughPlayersException if thrown by setup().
     */
    @Test
    public void setup() throws NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        assertEquals("Player 1 : (D_struct_or)", gameEngine.getBoard().getCurrentPlayer().toString());

    }


    /**
     * Test if the next player is correctly calculated in a standard case.
     */
    @Test
    public void getNextPlayerStandard(){

        Board b = BoardConfigurer.configureMap(1);

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);

        gameEngine.setCurrentPlayer(connections.get(0));
        gameEngine.setCurrentPlayer(gameEngine.getNextPlayer());
        assertEquals(connections.get(1), gameEngine.getCurrentPlayer());
    }


    /**
     * Tests if the next player is correctly calculated when the current player is the last one in the connection list.
     */
    @Test
    public void getNextPlayerAfterLast(){

        Board b = BoardConfigurer.configureMap(1);

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);
        gameEngine.setCurrentPlayer(connections.get(4));
        gameEngine.setCurrentPlayer(gameEngine.getNextPlayer());
        assertEquals(connections.get(0), gameEngine.getCurrentPlayer());
    }


    /**
     * Tests if the next player is correctly calculated when the player whose turn should come is suspended.
     */
    @Test
    public void getNextPlayerWithSuspendedPlayers(){

        Board b = BoardConfigurer.configureMap(1);

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);

        gameEngine.setCurrentPlayer(connections.get(0));
        connections.get(1).setSuspended(true);
        gameEngine.setCurrentPlayer(gameEngine.getNextPlayer());
        assertEquals(connections.get(2), gameEngine.getCurrentPlayer());
    }


    /**
     * Tests if the next player is correctly calculated when the player whose turn should come is suspended.
     */
    @Test
    public void changePlayerWhenNotEnoughPlayers(){

        Board b = BoardConfigurer.configureMap(1);

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);

        gameEngine.setCurrentPlayer(connections.get(0));
        connections.get(1).setSuspended(true);
        connections.get(2).setSuspended(true);
        connections.get(3).setSuspended(true);

        assertFalse(gameEngine.isGameOver());
        gameEngine.changePlayer();
        assertTrue(gameEngine.isGameOver());
    }


    /**
     * Tests the method addLeaderboard().
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     */
    @Test
    public void addLeaderboard() throws UnacceptableItemNumberException, NoMoreCardsException {

        Board b = BoardConfigurer.simulateScenario();

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);
        for (int i=0; i<connections.size(); i++) {
            gameEngine.getPlayers().get(i).setPlayer(b.getPlayers().get(i));
        }

        gameEngine.setLeaderboard(gameEngine.getPlayers());

        assertEquals("The leaderBoard is coming:\n" +
                "\n" +
                "Leaderboard:\n" +
                "anonymous: 0 points\n" +
                "anonymous: 0 points\n" +
                "anonymous: 0 points\n" +
                "anonymous: 0 points\n" +
                "anonymous: 0 points\n", gameEngine.addLeaderboard("The leaderBoard is coming:"));
    }


    /**
     * Tests the method simulateTillEndPhase().
     *
     * @throws NotAvailableAttributeException       if thrown by simulateTillEndPhase, getPosition() or getKillShotTrack().
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or simulateTillEndPhase.
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario() or simulateTillEndPhase.
     * @throws WrongTimeException                   if thrown by simulateTillEndPhase.
     */
    @Test
    public void simulateTillEndPhase() throws UnacceptableItemNumberException, WrongTimeException, NotAvailableAttributeException, NoMoreCardsException{

        Board b = BoardConfigurer.simulateScenario();

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));
        connections.add(new TCPVirtualView(null));

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setBoard(b);
        for (int i=0; i<connections.size(); i++) {
            gameEngine.getPlayers().get(i).setPlayer(b.getPlayers().get(i));
        }

        gameEngine.simulateTillEndphase();

        Player p1 = b.getPlayers().get(0);
        Player p2 = b.getPlayers().get(1);
        Player p3 = b.getPlayers().get(2);

        assertTrue(p1.isInGame());
        assertTrue(p2.isInGame());
        assertTrue(p3.isInGame());

        assertEquals(b.getSpawnPoints().get(0), p1.getPosition());
        assertEquals(b.getSpawnPoints().get(1), p2.getPosition());
        assertEquals(b.getSpawnPoints().get(2), p3.getPosition());

        assertEquals(1, b.getKillShotTrack().getSkullsLeft());
        assertEquals(Arrays.asList(p2, p3, p1, p2, p1), b.getKillShotTrack().getKillers());

        assertEquals(2, p1.getDeaths());
        assertEquals(1, p2.getDeaths());
        assertEquals(2, p3.getDeaths());

        assertEquals(23, p1.getPoints());
        assertEquals(27, p2.getPoints());
        assertEquals(17, p3.getPoints());

        assertEquals(4, p1.getPointsToGive());
        assertEquals(6, p2.getPointsToGive());
        assertEquals(4, p3.getPointsToGive());

        assertEquals(Arrays.asList(p2, p2), p1.getDamages());
        assertEquals(Arrays.asList(p1, p1, p1, p1, p3, p3, p3, p3, p3, p3), p2.getDamages());
        assertEquals(Arrays.asList(p2, p2, p2, p2, p1, p2), p3.getDamages());

        assertEquals(Player.Status.BASIC, p1.getStatus());
        assertEquals(Player.Status.ADRENALINE_2, p2.getStatus());
        assertEquals(Player.Status.ADRENALINE_1, p3.getStatus());

    }


    /**
     * Tests the method hasAnswerd().
     *
     * @throws NotEnoughPlayersException if thrown by setup().
     */
    @Test
    public void hasAnswered() throws NotEnoughPlayersException{

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);

        assertFalse(gameEngine.hasAnswered(gameEngine.getCurrentPlayer()));

        gameEngine.setup();

        //checks that the current player has answered the last question
        assertTrue(gameEngine.hasAnswered(gameEngine.getCurrentPlayer()));

    }

    /**
     * Tests the method wait().
     * Since all the answer are "1", the selected answer is "1".
     *
     * @throws NotEnoughPlayersException     if thrown by setup() or wait.
     * @throws SlowAnswerException           if thrown by wait.
     */
    @Test
    public void waitTest() throws NotEnoughPlayersException, SlowAnswerException{

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        gameEngine.getCurrentPlayer().choose("string", "test", Arrays.asList("a", "b"));
        int selected = Integer.parseInt(gameEngine.wait(gameEngine.getCurrentPlayer()));
        assertEquals(1, selected);

    }


    /**
     * Tests the method waitShort() in the case the answer is given in time.
     * Since all the answer are "1", the selected answer is "1".
     *
     * @throws NotEnoughPlayersException     if thrown by setup() or waitShort().
     */
    @Test
    public void waitShortTest() throws NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        gameEngine.getCurrentPlayer().choose("string", "test", Arrays.asList("a", "b"));
        int selected = Integer.parseInt(gameEngine.waitShort(gameEngine.getCurrentPlayer(), 10));
        assertEquals(1, selected);

    }


    /**
     * Tests the method checkForSuspension(), in the case enough players reamin in the game.
     *
     * @throws NotEnoughPlayersException i fthrown by setup(), checkForSuspension().
     */
    @Test
    public void checkForSuspensionNoGameOver() throws NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        connections.get(0).setSuspended(true);
        connections.get(1).setSuspended(true);
        gameEngine.checkForSuspension();
    }

    /**
     * Tests the method checkForSuspension(), in the case too many player disconnected to continue the game.
     *
     * @throws NotEnoughPlayersException since three players disconnected.
     */
    @Test(expected = NotEnoughPlayersException.class)
    public void checkForSuspension() throws NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        connections.get(0).setSuspended(true);
        connections.get(1).setSuspended(true);
        connections.get(2).setSuspended(true);
        gameEngine.checkForSuspension();
    }

    /**
     * Tests the method allowPlayersToResume(), in the case a player can resume.
     *
     * @throws NotEnoughPlayersException if thrown by setup.
     */
    @Test
    public void allowPlayersToResume() throws NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        VirtualView suspended = connections.get(4);
        suspended.setName("customizedName");
        VirtualView newConnection = new DummyVirtualView();
        newConnection.setName(suspended.getName());
        suspended.setSuspended(true);
        connections.add(new DummyVirtualView());
        gameEngine.tryResuming(newConnection);
        gameEngine.allowPlayersToResume();
        assertEquals(newConnection, gameEngine.getPlayers().get(4));
    }



    /**
     * Tests the method allowPlayersToResume(), in the case a player cannot resume.
     *
     * @throws NotEnoughPlayersException if thrown by setup.
     */
    @Test
    public void allowPlayersToResumeImpossible() throws NotEnoughPlayersException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.setup();

        VirtualView suspended = connections.get(4);
        suspended.setName("customizedName");
        VirtualView newConnection = new DummyVirtualView();
        newConnection.setName("different");
        suspended.setSuspended(true);
        connections.add(new DummyVirtualView());
        gameEngine.tryResuming(newConnection);
        gameEngine.allowPlayersToResume();
        assertNotEquals(newConnection, gameEngine.getPlayers().get(4));
    }


    /**
     * Tests the method simulationTillEndPhaseSetup();
     *
     * @throws NotAvailableAttributeException   if thrown by getKillShotTrack().

     */
    @Test
    public void simulationTillEndPhaseSetup() throws NotAvailableAttributeException {

        List<VirtualView> connections = new ArrayList<>();
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());
        connections.add(new DummyVirtualView());

        GameEngine gameEngine = new GameEngine(connections);
        gameEngine.simulationTillEndphaseSetup();

        assertEquals(4, gameEngine.getBoard().getId());
        assertEquals(6, gameEngine.getBoard().getKillShotTrack().getSkullsLeft());
        assertTrue(gameEngine.isFrenzy());
    }


}