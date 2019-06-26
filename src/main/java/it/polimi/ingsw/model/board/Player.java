package it.polimi.ingsw.model.board;

import it.polimi.ingsw.controller.ModelDataReader;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import it.polimi.ingsw.view.ClientModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.model.cards.AmmoPack.MAX_AMMO;
import static it.polimi.ingsw.model.cards.FireMode.FireModeName.*;
import static java.util.Collections.*;
import static it.polimi.ingsw.model.cards.Color.*;


/**
 * Represents a player of the game.
 * There are from 3 up to 5 instances of Player.
 * The id attribute is unique.
 *
 * @authors davidealde, BassaniRiccardo
 */

//TODO
// Look at the comments at line 246, 287/
// "die" message should include whether the player is or is not dead, since it is sent every time an action in reset.

public class Player {

    public enum HeroName {

        D_STRUCT_OR, BANSHEE, DOZER, VIOLET, SPROG;

        @Override
        public String toString(){
            return (this.name().substring(0,1) + this.name().toLowerCase().substring(1));
        }

    }

    public enum Status {
        BASIC, ADRENALINE_1, ADRENALINE_2, FRENZY_1, FRENZY_2
    }

    private final int id;
    private final HeroName name;
    private String username;
    private Status status;
    private int points;
    private boolean dead;
    private boolean flipped;

    private List<Player> damages;
    private List<Player> marks;

    private Square position;
    private Square previousPosition;
    private Board board;

    private List<Weapon> weaponList;
    private List<PowerUp> powerUpList;
    private AmmoPack ammoPack;

    private List<Action> actionList;
    private List<Player> mainTargets;
    private List<Player> optionalTargets;

    private int deaths;
    private int pointsToGive;
    private boolean justDamaged;
    private boolean overkilled;

    private boolean inGame;
    private static ModelDataReader j = new ModelDataReader();
    private final String RESET = "\u001b[0m";
    private static final Logger LOGGER = Logger.getLogger("serverLogger");


    /**
     * Constructs a player with an id, a name and a reference to the game board.
     *
     * @param id               the player's id
     * @param name             the player's name
     * @param board            the board the player is in
     */
    public Player(int id, HeroName name, Board board) {

        this.id = id;
        this.name = name;
        this.username = "anonymous";
        this.board = board;
        this.status = Status.BASIC;
        this.points = 0;
        this.dead = false;
        this.flipped = false;

        this.damages = new ArrayList<>();
        this.marks = new ArrayList<>();

        this.position = null;
        this.previousPosition = null;

        this.weaponList = new ArrayList<>();
        this.powerUpList = new ArrayList<>();
        this.ammoPack =new AmmoPack(j.getInt("initialRAmmo"), j.getInt("initialBAmmo"), j.getInt("initialYAmmo"));

        this.actionList = new ArrayList<>();
        this.mainTargets=new ArrayList<>();
        this.optionalTargets=new ArrayList<>();

        this.deaths = 0;
        this.pointsToGive=8;
        this.justDamaged=false;
        this.overkilled = false;

        this.inGame = false;

        refreshActionList();

    }

    //TODO: to remove if a fake player is not created
    public Player(Player toCopy) {

        this.id = toCopy.getId();
        this.name = toCopy.getName();
        this.board = toCopy.getBoard();
        this.status = toCopy.getStatus();
        this.points = toCopy.getPoints();
        this.dead = toCopy.isDead();
        this.flipped = toCopy.isFlipped();

        this.damages = toCopy.getDamages();
        this.marks = toCopy.getMarks();

        this.position = null;
        this.previousPosition = null;

        this.weaponList = toCopy.getWeaponList();
        this.powerUpList = toCopy.getPowerUpList();
        this.ammoPack = toCopy.getAmmoPack();

        this.actionList = toCopy.getActionList();
        this.mainTargets= toCopy.getMainTargets();
        this.optionalTargets= toCopy.getOptionalTargets();

        this. pointsToGive = toCopy.getPointsToGive();
        this.pointsToGive= toCopy.getPointsToGive();
        this.justDamaged=toCopy.isJustDamaged();
        this.overkilled = toCopy.isOverkilled();

        this.inGame = toCopy.isInGame();

    }



    /**
     * Getters
     */

    public int getId() { return this.id; }

    public HeroName getName(){return name;}

    public Status getStatus(){return status;}

    public int getPoints(){return this.points;}

    public List<Action> getActionList(){return actionList;}

    public List<Weapon> getWeaponList(){return weaponList;}

    public List<PowerUp> getPowerUpList(){return powerUpList;}

    public List<Player> getMarks() {return marks;}

    public List<Player> getDamages() {return damages;}

    public Player getFirstBlood() { return damages.get(0);}

    public boolean isFlipped(){return flipped;}

    public boolean isDead() {return dead;}

    public AmmoPack getAmmoPack(){return ammoPack;}

    public int getPointsToGive() {return pointsToGive;}

    public int getDeaths() {return deaths;}

    public boolean isJustDamaged(){return justDamaged;}

    public Square getPosition() throws NotAvailableAttributeException {
        if (position == null) throw new NotAvailableAttributeException("The player is not on the board.");
        return position;}

    public Square getPreviousPosition() {

        if (previousPosition == null) return position;
            //throw new NotAvailableAttributeException("The player was not on the board.");
        return previousPosition;}

    public List<Player> getMainTargets(){return mainTargets;}

    public List<Player> getOptionalTargets(){return optionalTargets;}

    public boolean isOverkilled(){return overkilled;}

    public boolean isInGame(){ return inGame;}

    public Board getBoard() {return board; }

    public String getUsername() { return username; }

    /**
     * Setters
     *
     */


    public void setPosition(Square square) {
        setVirtualPosition(square);
        board.addToUpdateQueue(Updater.get(Updater.MOVE_UPD, this, square));
    }

    /**
     * Virtual position used for calculating shooting squares. Needs to be manually reset
     * @param square    virtual movement destination
     */
    public void setVirtualPosition(Square square){
        if (!this.board.getMap().contains(square)) throw new IllegalArgumentException("The player must be located in a square that belongs to the board.");
        if (this.position!=null){
            this.position.removePlayer(this);
        }
        previousPosition = position;
        this.position = square;
        square.addPlayer(this);
    }

    public void setPointsToGive(int p) {
        if (!(p==8 || p==6 || p==4 || p==2 || p==1)) throw new IllegalArgumentException("Not valid number of points.");
        this.pointsToGive = p;
    }

    public void setPoints(int points) { this.points = points;}

    public void setStatus(Status status){this.status=status;board.addToUpdateQueue(Updater.get(Updater.STATUS_UPD, this));}

    public void setJustDamaged(boolean justDamaged){this.justDamaged = justDamaged;}

    public void setFlipped(boolean flipped){this.flipped = flipped; board.addToUpdateQueue(Updater.get(Updater.STATUS_UPD, this));
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
        board.addToUpdateQueue(Updater.get(Updater.SET_IN_GAME_UPD, this, inGame));
    }

    public void setDead(boolean dead) {this.dead = dead;}

    public void setDamages(List<Player> damages) {
        this.damages = damages;
    }

    public void setWeaponList(List<Weapon> weaponList) { this.weaponList = weaponList;}

    public void setPowerUpList(List<PowerUp> powerUpList) {
        this.powerUpList = powerUpList;
        for (PowerUp p : powerUpList){
            p.setHolder(this);
        }
    }

    public void setAmmoPack(AmmoPack ammoPack) { this.ammoPack = ammoPack; }

    public void setMarks(List<Player> marks) { this.marks = marks;  }

    public void setUsername(String username) {this.username = username; }

    public void addDeath() {
        this.deaths++;
        board.addToUpdateQueue(Updater.get(Updater.ADD_DEATH_UPD, this, pointsToGive));
    }

    /**
     * Adds damages to the player.
     * Every damage is a reference to the shooter.
     *
     * @param amount         the amount of damage to addList.
     * @param shooter        player who shoot
     */
    public void sufferDamage(int amount, Player shooter) {

        if (amount < 0) throw new IllegalArgumentException("Not valid amount of damage");
        if (shooter == this) throw new IllegalArgumentException("A player can not shoot himself");

        justDamaged = true;

        amount += frequency(getMarks(),shooter);
        marks.removeAll(singleton(shooter));
        for (int i = 0; i < amount; i++) {
            if (damages.size() < 12){
                damages.add(shooter);
            }
        }
        if (damages.size() >= 11){
            dead = true;
        }
        if (damages.size() == 12){
            overkilled = true;
            if (frequency(shooter.getMarks(), this) <= 3){
                shooter.getMarks().add(this);
            }
        }
        if (damages.size() >= 6 && !flipped){
            status = Status.ADRENALINE_2;
        }
        else {
            if (damages.size() >= 3 && !flipped){
                status = Status.ADRENALINE_1;
            }
        }
        board.addToUpdateQueue(Updater.get(Updater.DAMAGE_UPD, this, damages));
    }


    /**
     * Adds marks to the player marks.
     * Every mark is a reference to the shooter.
     *
     * @param amount            the number of marks added.
     * @param shooter           the player who shoot.
     */
    public void addMarks(int amount, Player shooter){

        if (amount < 0) throw new IllegalArgumentException("Not valid number of marks");
        if (shooter == this) throw new IllegalArgumentException("A player can not shoot himself");

        for (int i = 0; i< amount; i++){
            if (frequency(marks, shooter) < j.getInt("maximumMarks")){
                marks.add(shooter);
            }
        }
        board.addToUpdateQueue(Updater.get(Updater.MARK_UPD, this, marks));
    }


    /**
     * Adds a weapon to the player weapons list.
     *
     * @param addedWeapon           weapon added.
     */
    public void addWeapon(Weapon addedWeapon) throws UnacceptableItemNumberException {

        if (this.weaponList.size()> j.getInt("maxWeapons"))
            throw new UnacceptableItemNumberException("A player can hold up to 3 weapons; 4 are allowed while choosing " +
                    "which one to discard.");
        addedWeapon.setHolder(this);
        weaponList.add(addedWeapon);
        board.addToUpdateQueue(Updater.get(Updater.PICKUP_WEAPON_UPD, this, addedWeapon));
    }
    

    /**
     * Adds ammo to the AmmoPack of the player.
     *
     * @param ammoPack         ammo added.
     */
    public void addAmmoPack(AmmoPack ammoPack) {

        int blue = Math.min(MAX_AMMO - this.ammoPack.getBlueAmmo(), ammoPack.getBlueAmmo());
        int red = Math.min(MAX_AMMO - this.ammoPack.getRedAmmo(), ammoPack.getRedAmmo());
        int yellow = Math.min(MAX_AMMO - this.ammoPack.getYellowAmmo(), ammoPack.getYellowAmmo());
        AmmoPack ap = new AmmoPack(red, blue, yellow);

        this.ammoPack.addAmmoPack(ap);
        board.addToUpdateQueue(Updater.get(Updater.ADD_AMMO_UPD, this, ap));
    }


    /**
     * Collects a weapon.
     *
     */
    public boolean collect(Card collectedCard) throws NoMoreCardsException, UnacceptableItemNumberException, WrongTimeException {

        position.removeCard(collectedCard);

        if (this.board.getSpawnPoints().contains(position)){
            this.getAmmoPack().subAmmoPack(((Weapon)collectedCard).getReducedCost());
            board.addToUpdateQueue(Updater.get(Updater.USE_AMMO_UPD, this, ((Weapon)collectedCard).getReducedCost()));
            addWeapon((Weapon) collectedCard);
            ((Weapon)collectedCard).setLoaded(true);
            ((Weapon)collectedCard).setHolder(this);
        } else {
            addAmmoPack(((AmmoTile)collectedCard).getAmmoPack());
            if (((AmmoTile)collectedCard).hasPowerUp()) {
                if (powerUpList.size()>2) return false;
                drawPowerUp();
            }
            board.getAmmoDeck().getDiscarded().add(collectedCard);
        }
        return true;
    }


    /**
     * Draws a random power up from the deck of power ups and adds it to the player power ups list.
     * If there are no drawable cards left in the deck, the deck is regenerated.
     */
    public void drawPowerUp() throws NoMoreCardsException, UnacceptableItemNumberException, WrongTimeException {

        if (this.powerUpList.size() > 4)
            throw new UnacceptableItemNumberException("A player can normally hold up to 3 power ups; 4 are allowed in " +
                    "the process of rebirth. More than 4 are never allowed.");
        if (this.board.getPowerUpDeck().getDrawable().isEmpty()){
            this.board.getPowerUpDeck().regenerate();
            board.addToUpdateQueue(Updater.get(Updater.POWER_UP_DECK_REGEN_UPD, board.getPowerUpDeck().getDrawable().size()));
        }
        PowerUp p = (PowerUp)this.board.getPowerUpDeck().drawCard();
        p.setHolder(this);
        powerUpList.add(p);
        board.addToUpdateQueue(Updater.get(Updater.DRAW_POWER_UP_UPD, this, p));
    }
    

    /**
     * Removes a weapon from the player weapons list.
     *
     * @param removedWeapon         the removed weapon.
     */
    public void discardWeapon(Card removedWeapon) {
        if (!weaponList.contains(removedWeapon)) throw new IllegalArgumentException("The player does not own this weapon");
        weaponList.remove(removedWeapon);
        /*
        try {
            removedWeapon.setHolder(null);
        } catch (NotAvailableAttributeException e){LOGGER.log(Level.SEVERE, "This type of card cannot have an holder");}
        */
        board.addToUpdateQueue(Updater.get(Updater.DISCARD_WEAPON_UPD, this, (Weapon)removedWeapon));
    }


    /**
     * Removes a power up from the player power up list.
     *
     * @param removedPowerUp        the removed power up.
     */
    public void discardPowerUp(Card removedPowerUp) {

        if (!powerUpList.contains(removedPowerUp)) throw  new IllegalArgumentException("The player does not own this powerup.");
        powerUpList.remove(removedPowerUp);
        board.getPowerUpDeck().addDiscardedCard(removedPowerUp);
        board.addToUpdateQueue(Updater.get(Updater.DISCARD_POWER_UP_UPD, this, (PowerUp)removedPowerUp));
    }


    /**
     * Returns whether the player owns a teleporter or a newton which can be used against an enemy.
     *
     * @return  true if the player owns a teleporter or a newton which can be used against an enemy.
     *          false otherwise.
     * @throws  NotAvailableAttributeException
     */
    public boolean hasUsableTeleporterOrNewton() throws NotAvailableAttributeException {
        for (PowerUp p : getPowerUpList()) {
            if ((p.getName() == PowerUp.PowerUpName.NEWTON && !(p.findTargets().isEmpty()))|| p.getName() == PowerUp.PowerUpName.TELEPORTER) return true;
        }
        return false;
    }

    /**
     * Returns whether the player owns a tagback grenade which can be used against an enemy.
     *
     * @return  true if the player owns a tagback grenade which can be used against an enemy.
     *          false otherwise.
     * @throws  NotAvailableAttributeException
     */
    public boolean hasUsableTagbackGrenade() throws NotAvailableAttributeException {
        for (PowerUp p : getPowerUpList()) {
            if (p.getName() == PowerUp.PowerUpName.TAGBACK_GRENADE && !(p.findTargets().isEmpty())) return true;
        }
        return false;
    }


    /**
     * Returns whether the player owns a targeting scope which can be used against an enemy.
     *
     * @return  true if the player owns a targeting scope which can be used against an enemy.
     *          false otherwise.
     * @throws  NotAvailableAttributeException
     */
    public boolean hasUsableTargetingScope() throws NotAvailableAttributeException {
        for (PowerUp p : getPowerUpList()) {
            if (p.getName() == PowerUp.PowerUpName.TARGETING_SCOPE && !(p.findTargets().isEmpty())) return true;
        }
        return false;
    }


    /**
     * Returns true if the player has enough ammo to spend.
     *
     * @param ammoPack        the price to spend.
     */
    public boolean hasEnoughAmmo(AmmoPack ammoPack){

        return (this.ammoPack.getRedAmmo()>=ammoPack.getRedAmmo()&&
                this.ammoPack.getBlueAmmo()>=ammoPack.getBlueAmmo()&&
                this.ammoPack.getYellowAmmo()>=ammoPack.getYellowAmmo());
    }


    /**
     * Removes ammo from the player ammo pack.
     *
     * @param usedAmmo        the used ammo.
     * @throws      IllegalArgumentException
     */
    public void useAmmo(AmmoPack usedAmmo) {
        this.ammoPack.subAmmoPack(usedAmmo);
        board.addToUpdateQueue(Updater.get(Updater.USE_AMMO_UPD, this, usedAmmo));
    }


    /**
     * Discards a power up to gain an ammo of the same color of the power up.
     *
     * @param p                     the discarded power up.
     * @throws      IllegalArgumentException
     */
    public void useAsAmmo(PowerUp p) {

        this.discardPowerUp(p);
        AmmoPack ap;
        if (p.getColor() == RED) {
            ap= new AmmoPack(1, 0, 0);
        } else if ((p.getColor() == YELLOW)) {
            ap=new AmmoPack(0, 0, 1);
        } else {
            ap= new AmmoPack(0, 1, 0);
        }
        ammoPack.addAmmoPack(ap);
        board.addToUpdateQueue(Updater.get(Updater.ADD_AMMO_UPD, this, ap));

    }

    public String getColor(){
        if (name==HeroName.BANSHEE) return "blue";
        if (name==HeroName.D_STRUCT_OR) return "yellow";
        if (name==HeroName.DOZER) return "grey";
        if (name==HeroName.VIOLET) return "purple";
        if (name==HeroName.SPROG) return "green";
        else return "white";
    }

    /**
     * Returns the weapons that the player can reload, considering his ammo and the weapon status.
     *
     * @return          the list of the weapons the the player can reload.
     */
    public List<Weapon> getReloadableWeapons(){
        List<Weapon> reloadable = new ArrayList<>();
        for (Weapon w: weaponList){
            if (!w.isLoaded() && this.hasEnoughAmmo(w.getFullCost())){
                reloadable.add(w);
            }
        }
        return reloadable;
    }

    /**
     * Returns loaded weapons
     *
     * @return          the list of the weapons the the player can use.
     */
    public List<Weapon> getLoadedWeapons(){
        List<Weapon> loaded = new ArrayList<>();
        for (Weapon w: weaponList){
            if (w.isLoaded()){
                loaded.add(w);
            }
        }
        return loaded;
    }

    /**
     * Returns the weapons that the player can use.
     *
     * @return          the list of the weapons the the player can use.
     */
    public List<Weapon> getAvailableWeapons(){
        List<Weapon> available = new ArrayList<>();
        for(Weapon w: weaponList){
            if(w.isLoaded() && w.canFire()){
                available.add(w);
            }
        }
        return available;
    }

    public List<PowerUp> getPowerUps(PowerUp.PowerUpName name){
        List<PowerUp> powerUps
                = new ArrayList<>();
        for (PowerUp p : powerUpList){
            if (p.getName()== name){
                powerUps.add(p);
            }
        }
        return powerUps;
    }


    /**
     * Adds a player to the main targets.
     *
     * @param target         player added to the main targets.
     */
    public void addMainTarget(Player target) {
        if (this==target) throw new IllegalArgumentException("The player can not be in the list of his own targets.");
        this.mainTargets.add(target); }


    /**
     * Adds a list of players to the main targets.
     *
     * @param targets         players added to the main targets.
     */
    public void addMainTargets(List<Player> targets) {
        if (targets.contains(this)) throw new IllegalArgumentException("The player can not be in the list of his own targets.");
        this.mainTargets.addAll(targets); }

    /**
     * Adds a list of players to the optional targets.
     *
     * @param target         player added to the optional targets.
     */
    public void addOptionalTarget(Player target) {
        this.optionalTargets.add(target); }


    /**
     * Adds a list of players to the optional targets.
     *
     * @param targets         players added to the optional targets.
     */
    public void addOptionalTargets(List<Player> targets) {
        this.optionalTargets.addAll(targets); }

    /**
     * Updates the points the player will give to his killers the next time he will die.
     * It also set the player damages to zero.
     * Called after the death of the player.
     */
    public void updateAwards() throws WrongTimeException{

        if (!this.isDead()) throw new WrongTimeException("The points given for a death are updated only after a player dies.");
        this.addDeath();
        if (pointsToGive!=1){
            if (pointsToGive==2) pointsToGive-= 1;
            else pointsToGive -= 2;
        }
        this.damages.clear();
        this.setStatus(Status.BASIC);
        this.dead = false;
    }


    /**
     * Gives point to the killers based on the number of damages every player did
     * and on the number of the player previous death.
     * Called when the player dies.
     */
    public void rewardKillers() throws WrongTimeException{

        if(!this.isDead()) throw new WrongTimeException("The killers are rewarded only when the player dies.");
        //firstblood
        if (!this.flipped) {
            damages.get(0).addPoints(1);
        }

        //asks the board for the players
        List<Player> playersToReward = new ArrayList<>();
        playersToReward.addAll(this.board.getPlayers());

        //properly orders the playersToReward
        board.sort(playersToReward, damages);

        //assign the points
        int nextPointsToGive;
        if (pointsToGive == 2 || pointsToGive == 1)   nextPointsToGive = 1;
        else   nextPointsToGive = pointsToGive - 2;

        Iterator<Player> playerToRewardIt = playersToReward.iterator();
        while (playerToRewardIt.hasNext()) {
            Player p = playerToRewardIt.next();
            if (damages.contains(p)){
                p.addPoints(pointsToGive);
                int totalGivenPoints = pointsToGive;
                if (damages.get(0) == p) totalGivenPoints++;
                LOGGER.log(Level.FINE, p + " gains " + totalGivenPoints + " points.");
            }
            if (pointsToGive != 1) {
                if (pointsToGive == 2) pointsToGive -= 1;
                else pointsToGive -= 2;
            }
        }
        pointsToGive = nextPointsToGive;
    }


    /**
     * Increases player points.
     *
     * @param points          points earned.
     * @throws      IllegalArgumentException
     */
    public void addPoints(int points) {

        if (points<0) throw new IllegalArgumentException("Points can not be subtracted.");
        this.points += points;
    }


    /**
     * Produces the list of possible actions for the player in his current status.
     * Called when the status changes.
     */
    public void refreshActionList() {

        actionList.clear();

        if (status == Status.BASIC) {

            for(int i = 1; i<= j.getInt("numberOfActions","status",0); i++)
                actionList.add(new Action(j.getInt("steps"+i,"status",0),
                        j.getBoolean("collect"+i,"status",0),
                        j.getBoolean("shoot"+i,"status",0),
                        j.getBoolean("reload"+i,"status",0)));

        } else if (status == Status.ADRENALINE_1) {

            for(int i = 1; i<= j.getInt("numberOfActions","status",1); i++)
                actionList.add(new Action(j.getInt("steps"+i,"status",1),
                        j.getBoolean("collect"+i,"status",1),
                        j.getBoolean("shoot"+i,"status",1),
                        j.getBoolean("reload"+i,"status",1)));

        } else if (status == Status.ADRENALINE_2) {

            for(int i = 1; i<= j.getInt("numberOfActions","status",2); i++)
                actionList.add(new Action(j.getInt("steps"+i,"status",2),
                        j.getBoolean("collect"+i,"status",2),
                        j.getBoolean("shoot"+i,"status",2),
                        j.getBoolean("reload"+i,"status",2)));

        } else if (status == Status.FRENZY_1) {

            for(int i = 1; i<= j.getInt("numberOfActions","status",3); i++)
                actionList.add(new Action(j.getInt("steps"+i,"status",3),
                        j.getBoolean("collect"+i,"status",3),
                        j.getBoolean("shoot"+i,"status",3),
                        j.getBoolean("reload"+i,"status",3)));

        } else if (status == Status.FRENZY_2) {

            for(int i = 1; i<= j.getInt("numberOfActions","status",4); i++)
                actionList.add(new Action(j.getInt("steps"+i,"status",4),
                        j.getBoolean("collect"+i,"status",4),
                        j.getBoolean("shoot"+i,"status",4),
                        j.getBoolean("reload"+i,"status",4)));

        }

    }


    /**
     * Returns the squares the player can shoot from after moving up to a specified number of steps.
     *
     * @param steps         the maximum number of steps the player can takes before shooting.
     * @return              true if the player can shoot someone.
     *                      false otherwise.
     */

    //TODO the model should not be modified, it is better to create a fake player and work on it.

    public List<Square> getShootingSquares(int steps, List<Weapon> weaponToConsider) throws NotAvailableAttributeException{

        List<Square> starting = new ArrayList<>();
        List<Square> start = board.getReachable(position, steps);
        Square square = this.position;
        for (Square s1: start){
            boolean found = false;
            boolean option1 = false;
            FireMode preMove = null;
            this.setVirtualPosition(s1);
            for (Weapon w: weaponToConsider){
                for (FireMode f : w.listAvailableFireModes()){
                    if (f.getName()==MAIN || f.getName() == SECONDARY)  found = true;
                    if (f.getName()==OPTION1){
                        option1 = true;
                        preMove = f;
                    }
                }
                if (!found && option1) {
                    for (Square dest : preMove.getDestinationFinder().find(this, new ArrayList<>(Arrays.asList(this)))) {
                        this.setVirtualPosition(dest);
                        for (FireMode f : w.listAvailableFireModes()) {
                            if (f.getName() == MAIN || f.getName() == SECONDARY) found = true;
                        }
                        if (true) break;
                    }
                }
                //if only option1 is available??
                //if (!w.listAvailableFireModes().isEmpty()) found = true;
                //for (FireMode f : w.getFireModeList()){
                  //  if (!(f.getTargetFinder().find(this).isEmpty())) {
                    //    found = true;
                    //}
                //}
            }
            if (found && !starting.contains(s1)) starting.add(s1);
        }
        setVirtualPosition(square);
        return starting;
    }


    /**
     * Returns a list of the available action, considering that an action that includes collecting, shooting or reloading
     * can not always be executed.
     *
     * @return
     */
    public List<Action> getAvailableActions() throws NotAvailableAttributeException{

        List<Action> availableActions = new ArrayList<>();
        availableActions.addAll(getActionList());
        removeShootingAction(availableActions);
        removeCollectingAction(availableActions);
        return availableActions;

    }


    /**
     * Modifies and returns a list of actions, removing the shooting action if it cannot be executed.
     *
     * @param availableActions          the list of action before the possible removal.
     * @return                          the list of action after the possible removal.
     */
    public List<Action> removeShootingAction(List<Action> availableActions) throws NotAvailableAttributeException{

        if (status == Status.BASIC || status == Status.ADRENALINE_1){
            if(getShootingSquares(0, getLoadedWeapons()).isEmpty()){
                availableActions.remove(2);
            }
        }
        else if (status == Status.ADRENALINE_2){
            if(getShootingSquares(1, getLoadedWeapons()).isEmpty()){
                availableActions.remove(2);
            }
        }
        else if (status == Status.FRENZY_1){
            List<Weapon> weapons = new ArrayList<>();
            weapons.addAll(getLoadedWeapons());
            weapons.addAll(getReloadableWeapons());
            if(getShootingSquares(1, weapons ).isEmpty()){
                availableActions.remove(2);
            }
        }
        else if (status == Status.FRENZY_2){
            List<Weapon> weapons = new ArrayList<>();
            weapons.addAll(getLoadedWeapons());
            weapons.addAll(getReloadableWeapons());
            if(getShootingSquares(2, weapons).isEmpty()){
                availableActions.remove(1);
            }
        }
        return availableActions;

    }


    /**
     * Modifies and returns a list of actions, removing the collecting action if it cannot be executed.
     *
     * @param availableActions          the list of action before the possible removal.
     * @return                          the list of action after the possible removal.
     */
    public List<Action> removeCollectingAction(List<Action> availableActions){

        List<Square> possibleDest = new ArrayList<>();
        possibleDest.addAll(board.getMap());
        for (Square s: board.getMap()){

            if (s.isEmpty()){
                possibleDest.remove(s);
            }
            else {
                if (status == Status.BASIC) {
                    if (board.getDistance(s, position) > 1) possibleDest.remove(s);

                } else if (status == Status.ADRENALINE_1 || status == Status.ADRENALINE_2 || status == Status.FRENZY_1) {
                    if (board.getDistance(s, position) > 2) possibleDest.remove(s);
                } else if (status == Status.FRENZY_2 && board.getDistance(s, position) > 3) possibleDest.remove(s);
            }
            if (possibleDest.contains(s) && board.getSpawnPoints().contains(s) && getCollectibleWeapons((WeaponSquare)s)
                    .isEmpty()) possibleDest.remove(s);

        }

        if (possibleDest.isEmpty()) {
            if (status == Status.FRENZY_2)  availableActions.remove(0);
            else availableActions.remove(1);
        }

        return availableActions;

    }


    /**
     * Returns the list of weapons the player can collect from the specified weapon square.
     *
     * @param weaponSquare         the square the player wants to collect a weapon from.
     * @return
     */
    public List<Weapon> getCollectibleWeapons(WeaponSquare weaponSquare){

        List<Weapon> collectible = new ArrayList<>();
        for (Weapon w : weaponSquare.getWeapons()){
            if (hasEnoughAmmo(w.getReducedCost())) collectible.add(w);
        }
        return collectible;

    }

    public String getstringColor(){
        if (name==HeroName.D_STRUCT_OR) return "yellow";
        if (name==HeroName.BANSHEE) return "blue";
        if (name==HeroName.DOZER) return "grey";
        if (name==HeroName.SPROG) return "green";
        if (name==HeroName.VIOLET) return "purple";
        else return "wrong hero name: no color";
    }


    /**
     * Returns a string representing the player.
     *
     * @return      the description of the player.
     */
    @Override
    public String toString() {
        return "Player " + id + " : " + username + "(" + name + ")";
    }


    /**
     * Returns a string representing the player, to display in the message sent to the user.
     *
     * @return      the description of the player.
     */
    public String userToString() {
        return ClientModel.getEscapeCode(getColor()) + username + RESET;
    }


    /**
     * Returns true if the compared objects are two players belonging to the same board with the same id.
     *
     * @param o     the object to compare to the current player.
     * @return      true if the compared objects are two players belonging to the same board with the same id.
     *              false otherwise.
     */
    @Override
    public boolean equals(Object o){

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }
        // Checks if o is an instance of Player or not
        if (!(o instanceof Player)) {
            return false;
        }
        // typecast o to Player in order to compare the id
        Player p = (Player) o;
        // Compares the IDs and returns accordingly
        return p.getId() == getId() && p.getBoard().equals(getBoard());

    }

    /**
     * Returns the hashCode of the player.
     *
     * @return      the hashCode of the player.
     */
    @Override
    public int hashCode() {
        int result;
        result = id + getBoard().hashCode();
        return result;
    }

}

