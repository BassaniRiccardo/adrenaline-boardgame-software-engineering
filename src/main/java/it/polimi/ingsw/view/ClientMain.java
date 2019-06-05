package it.polimi.ingsw.view;

import com.google.gson.*;
import it.polimi.ingsw.network.client.RMIConnection;
import it.polimi.ingsw.network.client.TCPConnection;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
/**
 * Main client class, containing most of the client's logic and structures
 *
 * @author  marcobaga
 */
public class ClientMain {

    private UI ui;
    private Runnable connection;
    private ExecutorService executor;
    private static final Logger LOGGER = Logger.getLogger("clientLogger");
    private ClientModel clientModel;

    /**
     * Constructor
     */
    public ClientMain() {
        executor = Executors.newCachedThreadPool();
        clientModel = null;
    }

    /**
     * Main method, instantiates the class and initiates setup
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain();
        clientMain.initializeLogger();
        clientMain.setup(args);
        LOGGER.log(Level.INFO, "Setup finished");
    }

    /**
     * Initializes a logger for all the classes used by the client.
     */
    private void initializeLogger() {
        try {
            FileHandler fileHandler = new FileHandler("clientLog.txt");
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IOException thrown while creating logger", ex);
        }
        LOGGER.setLevel(Level.ALL);
    }

    /**
     * Loads server IP and port from args or client.properties
     */
    private Properties loadConfig(String[] args) {
        Properties prop = new Properties();

        if (args.length == 2) {     //test reading from args
            prop.put("serverIP", args[0]);
            prop.put("RMIPort", args[1]);
            prop.put("TCPPort", args[1]);
        } else {
            try{
                InputStream input = getClass().getResourceAsStream("/client.properties");
                prop.load(input);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot load client config from file", ex);
            }
        }
        return prop;
    }

    /**
     * Method that initializes ui and connection as chosen by the user. Both are executed in a different thread.
     *
     * @param args the server's ip and port
     */
    private void setup(String[] args) {

        Properties prop = loadConfig(args);

        Scanner in = new Scanner(System.in);
        System.out.println("Client started. Which interface do you want to use (GUI/CLI)?");
        String buff = in.nextLine();
        while (!(buff.equals("GUI") || buff.equals("CLI"))) {
            System.out.println("Invalid choice. Try again.");
            buff = in.nextLine();
        }
        String selectedInterface;
        if(buff.equals("GUI")){
            new Thread() {
                @Override
                public void run() {
                    javafx.application.Application.launch(GUI.class);
                }
            }.start();
            ui = GUI.waitGUI();
            ((GUI)ui).setClientMain(this);
            selectedInterface = "GUI selected.";
        }else{
            ui = new CLI(this);
            selectedInterface = "CLI selezionata.";
        }
        executor.submit(ui);
        ui.display(selectedInterface);

        ui.display("Which type of connection do you want to use?", new ArrayList<>(Arrays.asList("Socket", "RMI")));
        buff = ui.get(new ArrayList<>(Arrays.asList("Socket", "RMI")));
        while (!(buff.equals("1") || buff.equals("2"))) {
            ui.display("Invalid choice. Try again.");
            buff = ui.get(new ArrayList<>(Arrays.asList("Socket", "RMI")));
        }
        if (buff.equals("2")) {
            connection = new RMIConnection(this, prop.getProperty("serverIP", "localhost"), Integer.parseInt(prop.getProperty("RMIPort", "1420")));
            //connection=new RMIConnection(this, "192.168.43.244", 1420);
        } else {
            connection = new TCPConnection(this, prop.getProperty("serverIP", "localhost"), Integer.parseInt(prop.getProperty("TCPPort", "5000")));
            //connection=new TCPConnection(this, "192.168.43.244", 5000);
        }
        executor.submit(connection);
    }


    public int choose(String msg, List<String> options) {
        ui.display(msg, options);
        return Integer.parseInt(ui.get(options));
    }

    public void display(String msg) {
        ui.display(msg);

    }

    public void render() {
        ui.render();
    }

    public String getInput(String msg, int max){
        ui.display(msg, Integer.toString(max));
        return ui.get(Integer.toString(max));
    }

    public ClientModel getClientModel() {
        return clientModel;
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel;
    }

    public void shutdown() {
        //graceful shutdown
        System.exit(0);
    }

    public void update(JsonObject j) {

        System.out.println("update received: " + j.get("type").getAsString());

        switch (j.get("type").getAsString()) {

            case("loaded"):
                clientModel.getCurrentPlayer().getWeapon(j.get("weapon").getAsString()).setLoaded(j.get("loaded").getAsBoolean());
                ui.render();
                break;
            case ("skullRemoved"):
                clientModel.removeSkulls(j.get("number").getAsInt());
                //add to killshottrack simpleplayers
                //redraw model
                ui.render();
                break;
            case ("pDeckRegen"):
                clientModel.setPowerUpCardsLeft(j.get("number").getAsInt());
                //redraw
                ui.render();
                break;
            case ("drawPowerUp"):
                clientModel.setPowerUpCardsLeft(clientModel.getPowerUpCardsLeft()-1);
                clientModel.getPlayer(j.get("player").getAsInt()).setCardNumber(clientModel.getPlayer(j.get("player").getAsInt()).getCardNumber()+1);
                if(clientModel.getPlayerID()==j.get("player").getAsInt()) {
                    clientModel.getPowerUpInHand().add(j.get("powerup").getAsString());
                }//redraw model
                ui.render();
                break;
            case ("discardPowerUp"):
                clientModel.getPlayer(j.get("player").getAsInt()).setCardNumber(clientModel.getPlayer(j.get("player").getAsInt()).getCardNumber()-1);
                if(clientModel.getPlayerID()==j.get("player").getAsInt()) {
                    clientModel.getPowerUpInHand().remove(j.get("powerup").getAsString());
                }                //redraw model
                ui.render();
                break;
            case ("pickUpWeapon"):
                clientModel.getCurrentPlayer().pickUpWeapon(j.get("weapon").getAsString());
                //ui.flash(j.get("weapon").getAsString());
                //wait a little
                //redraw model
                ui.render();
                break;
            case ("discardWeapon"):
                clientModel.getPlayer(j.get("player").getAsInt()).discardWeapon(j.get("weapon").getAsString());
                //redraw model
                ui.render();
                break;
            case ("addWeapon"):
                ClientModel.SimpleWeapon w = new ClientModel().new SimpleWeapon(j.get("weapon").getAsString(), true);
                clientModel.getSquare(j.get("square").getAsInt()).getWeapons().add(w);
                ui.render();
                break;
            case ("useAmmo"):
                clientModel.getCurrentPlayer().subAmmo(j.get("blueammo").getAsInt(), j.get("redammo").getAsInt(), j.get("yellowammo").getAsInt());
                //redraw model
                ui.render();
                break;
            case ("addAmmo"):
                clientModel.getCurrentPlayer().addAmmo(j.get("blueammo").getAsInt(), j.get("redammo").getAsInt(), j.get("yellowammo").getAsInt());
                //redraw model
                ui.render();
                break;
            case ("move"):
                clientModel.moveTo(j.get("player").getAsInt(), j.get("square").getAsInt());
                //ui.move(j.get("player").getAsInt(), j.get("square").getAsInt());
                //wait a little
                //redraw model
                ui.render();
                break;
            case ("flip"):
                clientModel.flip(j.get("player").getAsInt());
                //redraw model
                ui.render();
                break;
            case ("addDeath"):
                clientModel.getPlayer(j.get("player").getAsInt()).addDeath();
                //redraw model
                ui.render();
                break;
            case ("damaged"):
                clientModel.damage(j.get("player").getAsInt(), j.getAsJsonArray("list"));
                //ui.flash(j.get("player"));
                //wait a little
                //redraw model
                ui.render();
                break;
            case ("marked"):
                clientModel.mark(j.get("player").getAsInt(), j.getAsJsonArray("list"));
                //ui.flash(j.get("player"));
                //wait a little
                //redraw model
                ui.render();
                break;
            case ("weaponRemoved"):
                (clientModel.getSquare(j.get("square").getAsInt())).removeWeapon(j.get("weapon").getAsString());
                //redraw model
                ui.render();
                break;
            case ("setInGame"):
                clientModel.getPlayer(j.get("player").getAsInt()).setInGame(j.get("ingame").getAsBoolean());
                ui.render();
                break;
            case ("removeAmmoTile"):
                clientModel.getSquare(j.get("square").getAsInt()).setBlueAmmo(0);
                clientModel.getSquare(j.get("square").getAsInt()).setYellowAmmo(0);
                clientModel.getSquare(j.get("square").getAsInt()).setRedAmmo(0);
                clientModel.getSquare(j.get("square").getAsInt()).setPowerup(false);
                break;
            case ("mod"):
                try {
                    JsonObject mod = new JsonParser().parse(j.get("mod").getAsString()).getAsJsonObject();
                    setClientModel(new Gson().fromJson(mod, ClientModel.class));
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                ui.render();
                //ui.onUpdate();
                //wait a little
                break;
                /*
            case ("revert"):
                JsonArray players = j.get("players").getAsJsonArray();
                for(int i = 0; i<players.size(); i++){
                    ClientModel.SimplePlayer p = clientModel.getPlayer(players.get(i).getAsInt());
                    p.setPosition(clientModel.getSquare(j.getAsJsonArray("positions").get(i).getAsInt()));
                    clientModel.damage(players.get(i).getAsInt(), j.getAsJsonArray("damage").get(i).getAsJsonArray());
                }
                JsonArray powerup = j.get("powerup").getAsJsonArray();
                clientModel.getPowerUpInHand().clear();
                for(JsonElement e : powerup){
                    clientModel.getPowerUpInHand().add(e.getAsString());
                }
                clientModel.getCurrentPlayer().setAmmo(j.get("blueammo").getAsInt(), j.get("redammo").getAsInt(), j.get("yellowammo").getAsInt());

                JsonArray weapons = j.getAsJsonArray("weapons");
                JsonArray loadedWeapons = j.getAsJsonArray("loadedweapons");
                clientModel.getCurrentPlayer().getWeapons().clear();
                for(JsonElement e : weapons){
                    clientModel.getCurrentPlayer().getWeapons().add(clientModel.new SimpleWeapon(e.toString(), false));
                }
                for(JsonElement e : loadedWeapons) {
                    clientModel.getCurrentPlayer().getWeapon(e.getAsString()).setLoaded(true);
                }
                JsonArray squares = j.getAsJsonArray("squares");
                JsonArray weaponInSquare = j.getAsJsonArray("weaponsinsquare");
                for(JsonElement e : squares) {
                    List<ClientModel.SimpleWeapon> list = clientModel.getSquare(e.getAsInt()).getWeapons();
                    list.clear();
                    for(JsonElement f : weaponInSquare) {
                        list.add(clientModel.new SimpleWeapon(f.getAsString(), false));
                    }
                }
                //ui.onUpdate();
                //wait a little
                //redraw model
                ui.render();
                break;
                */
            default: //fill in
        }

    }
}