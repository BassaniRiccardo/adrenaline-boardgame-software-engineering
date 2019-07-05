package it.polimi.ingsw.model.board;

import it.polimi.ingsw.controller.ModelDataReader;
import it.polimi.ingsw.model.Updater;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import it.polimi.ingsw.view.ClientModel;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.model.cards.AmmoPack.MAX_AMMO_AMOUNT;
import static it.polimi.ingsw.model.cards.FireMode.FireModeName.*;
import static java.util.Collections.*;
import static it.polimi.ingsw.model.cards.Color.*;


/**
 * Represents a player of the game.
 * There are from 3 up to 5 instances of Player.
 * The id attribute is unique.
 *
 * @author BassaniRiccardo, davidealde
 */


public class Player {

    /**
     * An enumeration for the hero names.
     */
    public enum HeroName {

        D_STRUCT_OR, BANSHEE, DOZER, VIOLET, SPROG;

        /**
         * Returns a string representing the hero name.
         *
         * @return a string representing the hero name.
         */
        @Override
        public String toString(){
            return (this.name().substring(0,1) + this.name().toLowerCase().substring(1));
        }

    }

    /**
     * An enumeration for the player status.
     */
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
    private static final String RESET = "\u001b[0m";
    private static final Logger LOGGER = Logger.getLogger("serverLogger");
    private static final String SHOOT = "shoot";
    private static final String STATUS_TAG = "status";
    private static final String STEPS = "steps";
    private static final String COLLECT = "collect";
    private static final String RELOAD = "reload";
    private static final String NUMBER_OF_ACTIONS = "numberOfActions";
    private static final String NO_SELF_SHOOTING = "A player can not shoot himself";



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


    /*
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
        return previousPosition;
    }

    public List<Player> getMainTargets(){return mainTargets;}

    public List<Player> getOptionalTargets(){return optionalTargets;}

    public boolean isOverkilled(){return overkilled;}

    public boolean isInGame(){ return inGame;}

    public Board getBoard() {return board; }

    public String getUsername() { return username; }


    /*
     * Setters
     */

    public void setPosition(Square square) {
        setVirtualPosition(square);
        board.addToUpdateQueue(Updater.get(Updater.MOVE_UPD, this, square));
    }

    /**
     * Virtual position used for calculating shooting squares. Needs to be manually RESET.
     * @param square    virtual movement destination.
     */
    private void setVirtualPosition(Square square){
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
     * Adds damages to the player in such a situation that the damage must be incremented by possible marks.
     * Every damage is a reference to the shooter.
     *
     * @param amount         the amount of damage to addList.
     * @param shooter        player who shoot
     */
    public void sufferDamage(int amount, Player shooter) {

        if (amount < 0) throw new IllegalArgumentException("Not valid amount of damage");
        if (shooter == this) throw new IllegalArgumentException(NO_SELF_SHOOTING);

        justDamaged = true;
        amount += frequency(getMarks(),shooter);
        marks.removeAll(singleton(shooter));
        board.addToUpdateQueue(Updater.get(Updater.REMOVE_MARKS, this, marks));

        addDamages(amount, shooter);
    }


    /**
     * Adds damages to the player in such a situation that the damage must not be incremented by possible marks.
     * Every damage is a reference to the shooter.
     *
     * @param amount         the amount of damage to addList.
     * @param shooter        the player who shot.
     */
    public void sufferDamageNoMarksExtra(int amount, Player shooter) {

        if (amount < 0) throw new IllegalArgumentException("Not valid amount of damage");
        if (shooter == this) throw new IllegalArgumentException(NO_SELF_SHOOTING);
        addDamages(amount, shooter);

    }


    /**
     * Adds damages to the player.
     * Every damage is a reference to the shooter.
     *
     * @param amount         the amount of damage to addList.
     * @param shooter        the player who shot.
     */
    private void addDamages(int amount, Player shooter){

        boolean addMarkToShooter = false;
        for (int i = 0; i < amount; i++) {
            if (damages.size() < 12){
                addMarkToShooter = true;
                damages.add(shooter);
            }
        }
        if (damages.size() >= 11){
            dead = true;
        }
        if (damages.size() == 12){
            overkilled = true;
            if (frequency(shooter.getMarks(), this) <= 3 && addMarkToShooter){
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
        if (shooter == this) throw new IllegalArgumentException(NO_SELF_SHOOTING);

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
     * @throws UnacceptableItemNumberException if the player already holds the maximum number of weapons.
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

        int red = Math.min(MAX_AMMO_AMOUNT - this.ammoPack.getRedAmmo(), ammoPack.getRedAmmo());
        int blue = Math.min(MAX_AMMO_AMOUNT - this.ammoPack.getBlueAmmo(), ammoPack.getBlueAmmo());
        int yellow = Math.min(MAX_AMMO_AMOUNT - this.ammoPack.getYellowAmmo(), ammoPack.getYellowAmmo());
        AmmoPack ap = new AmmoPack(red, blue, yellow);

        this.ammoPack.addAmmoPack(ap);
        board.addToUpdateQueue(Updater.get(Updater.ADD_AMMO_UPD, this, ap));
    }


    /**
     * Collects a card.
     *
     * @param collectedCard the card to collect
     * @throws NoMoreCardsException if no cards are present in the player position.
     * @throws UnacceptableItemNumberException if the player tries to collect a card when he already holds the maximum number allowed for its type.
     * @throws WrongTimeException if thrown by drawPowerUp().
     * @return false if it could not draw a powerup
     */
    public boolean collect(Card collectedCard)  throws NoMoreCardsException, UnacceptableItemNumberException, WrongTimeException {

        if (this.board.getSpawnPoints().contains(position)){
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

        position.removeCard(collectedCard);

        return true;
    }


    /**
     * Draws a random power up from the deck of power ups and adds it to the player power ups list.
     * If there are no drawable cards left in the deck, the deck is regenerated.
     * @throws NoMoreCardsException if no cards are present in the powerup deck tp be drawn.
     * @throws UnacceptableItemNumberException if the player tries to draw a powerup when he already holds the maximum number allowed.
     * @throws WrongTimeException if thrown by regenerate().
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
     * @param  rw         the removed weapon.
     * @throws UnacceptableItemNumberException  if thrown by addCard().
     */
    public void discardWeapon(Card rw) throws UnacceptableItemNumberException{
        Weapon removedWeapon = (Weapon)rw;
        if (!weaponList.contains(removedWeapon)) throw new IllegalArgumentException("The player does not own this weapon");
        weaponList.remove(removedWeapon);
        ((WeaponSquare)position).addCard(removedWeapon);
        board.addToUpdateQueue(Updater.get(Updater.DISCARD_WEAPON_UPD, this, removedWeapon));
    }


    /**
     * Removes a power up from the player power up list.
     *
     * @param rp        the removed power up.
     */
    public void discardPowerUp(Card rp) {

        PowerUp removedPowerUp = (PowerUp)rp;
        if (!powerUpList.contains(removedPowerUp)) throw  new IllegalArgumentException("The player does not own this powerup.");
        powerUpList.remove(removedPowerUp);
        board.getPowerUpDeck().addDiscardedCard(removedPowerUp);
        board.addToUpdateQueue(Updater.get(Updater.DISCARD_POWER_UP_UPD, this, removedPowerUp));
    }


    /**
     * Returns whether the player owns a teleporter or a newton which can be used against an enemy.
     *
     * @return  true if the player owns a teleporter or a newton which can be used against an enemy.
     *          false otherwise.
     * @throws  NotAvailableAttributeException      if thrown by PowerUp.findTargets().
     */
    public boolean hasUsableTeleporterOrNewton() throws NotAvailableAttributeException {
        for (PowerUp p : getPowerUpList()) {
            if ((p.getName() == PowerUp.PowerUpName.NEWTON && !(p.findTargets().isEmpty()))|| p.getName() == PowerUp.PowerUpName.TELEPORTER) return true;
        }
        return false;
    }

    /**
     * Returns whether the player owns a tagback grenade.
     *
     * @return  true if the player owns a tagback grenade.
     *          false otherwise.
     */
    public boolean hasUsableTagbackGrenade() {
        for (PowerUp p : getPowerUpList()) {
            if (p.getName() == PowerUp.PowerUpName.TAGBACK_GRENADE) return true;
        }
        return false;
    }


    /**
     * Returns whether the player owns a targeting scope which can be used against an enemy.
     *
     * @return  true if the player owns a targeting scope which can be used against an enemy.
     *          false otherwise.
     * @throws  NotAvailableAttributeException if thrown by PowerUp.findTargets().
     */
    public boolean hasUsableTargetingScope() throws NotAvailableAttributeException {
        for (PowerUp p : getPowerUpList()) {
            if (p.getName() == PowerUp.PowerUpName.TARGETING_SCOPE && !(p.findTargets().isEmpty())) return true;
        }
        return false;
    }


    /**
     * Returns true if the player can pay an amount of ammo, considering his ammo and the powerups he can convert.
     *
     * @param ammoPack        the price to pay.
     * @return                true if payment is possible
     */
    public boolean canPay(AmmoPack ammoPack){

        return (this.ammoPack.getRedAmmo() + getPowerUps(RED).size() >= ammoPack.getRedAmmo()&&
                this.ammoPack.getBlueAmmo() + getPowerUps(BLUE).size() >= ammoPack.getBlueAmmo()&&
                this.ammoPack.getYellowAmmo() + getPowerUps(YELLOW).size() >= ammoPack.getYellowAmmo());
    }


    /**
     * Removes ammo from the player ammo pack.
     *
     * @param usedAmmo        the used ammo.
     */
    public void useAmmo(AmmoPack usedAmmo) {
        this.ammoPack.subAmmoPack(usedAmmo);
        board.addToUpdateQueue(Updater.get(Updater.USE_AMMO_UPD, this, usedAmmo));
    }


    /**
     * Returns the weapons that the player can reload, considering his ammo and the weapon status.
     *
     * @return          the list of the weapons the the player can reload.
     */
    public List<Weapon> getReloadableWeapons(){
        List<Weapon> reloadable = new ArrayList<>();
        for (Weapon w: weaponList){
            if (!w.isLoaded() && this.canPay(w.getFullCost())){
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


    /**
     * Return all the player powerUps of a specific type.
     *
     * @param name      the type of powerup
     * @return           the player powerUps of the specific type.
     */
    public List<PowerUp> getPowerUps(PowerUp.PowerUpName name){
        List<PowerUp> powerUps
                = new ArrayList<>();
        for (PowerUp p : powerUpList){
            // the cost is needed only in the case of the targeting scope. It is doubled since the targeting scope which is being used cannot be converted.
            AmmoPack doubledCost = new AmmoPack(p.getCost().getRedAmmo(), p.getCost().getBlueAmmo(), p.getCost().getYellowAmmo());
            doubledCost.addAmmoPack(p.getCost());
            if (p.getName()== name && this.canPay(doubledCost)){
                powerUps.add(p);
            }
        }
        return powerUps;
    }


    /**
     * Returns all the player powerups of a specific color.
     *
     * @param color     the desired color.
     * @return          the player powerups of the specified color.
     */
    public List<PowerUp> getPowerUps(Color color){
        List<PowerUp> coloredPowerUps = new ArrayList<>();
        for (PowerUp p : powerUpList){
            if (p.getColor().equals(color)){
                coloredPowerUps.add(p);
            }
        }
        return coloredPowerUps;
    }


    /**
     * Adds a player to the main targets.
     *
     * @param target         player added to the main targets.
     * @throws IllegalArgumentException if the target list contains the player himself.
     */
    public void addMainTarget(Player target) {
        if (this==target) throw new IllegalArgumentException("The player can not be in the list of his own targets.");
        this.mainTargets.add(target);
    }


    /**
     * Adds a list of players to the main targets.
     *
     * @param targets         players added to the main targets.
     * @throws IllegalArgumentException if the target list contains the player himself.
     */
    public void addMainTargets(List<Player> targets) {
        if (targets.contains(this)) throw new IllegalArgumentException("The player can not be in the list of his own targets.");
        this.mainTargets.addAll(targets);
    }


    /**
     * Adds a list of players to the optional targets.
     *
     * @param target         player added to the optional targets.
     */
    public void addOptionalTarget(Player target) {
        this.optionalTargets.add(target);
    }


    /**
     * Adds a list of players to the optional targets.
     *
     * @param targets         players added to the optional targets.
     */
    public void addOptionalTargets(List<Player> targets) {
        this.optionalTargets.addAll(targets);
    }


    /**
     * Updates the points the player will give to his killers the next time he will die.
     * It also set the player damages to zero.
     * Called after the death of the player.
     *
     * @throws WrongTimeException if the player is not dead.
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
     *
     * @throws WrongTimeException if the player is not dead.
     */
    public void rewardKillers() throws WrongTimeException{

        if(!this.isDead()) throw new WrongTimeException("The killers are rewarded only when the player dies.");

        //firstblood
        if (!this.flipped) {
            damages.get(0).addPoints(1);
        }

        //asks the board for the players
        List<Player> playersToReward = new ArrayList<>(this.board.getPlayers());

        //properly orders the playersToReward
        board.sort(playersToReward, damages);

        //assign the points
        int nextPointsToGive;
        if (pointsToGive == 2 || pointsToGive == 1)   nextPointsToGive = 1;
        else   nextPointsToGive = pointsToGive - 2;

        for (Player p : playersToReward){
            if (damages.contains(p)){
                p.addPoints(pointsToGive);
                int totalGivenPoints = pointsToGive;
                if (damages.get(0) == p) totalGivenPoints++;
                String msg = p + " gains " + totalGivenPoints + " points.";
                LOGGER.log(Level.FINE, msg);
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
     * @throws      IllegalArgumentException if the amount of points added is negative.
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

            for(int i = 1; i<= j.getInt(NUMBER_OF_ACTIONS, STATUS_TAG,0); i++)
                actionList.add(new Action(j.getInt(STEPS + i, STATUS_TAG,0),
                        j.getBoolean(COLLECT + i, STATUS_TAG,0),
                        j.getBoolean(SHOOT + i, STATUS_TAG,0),
                        j.getBoolean(RELOAD + i, STATUS_TAG,0)));

        } else if (status == Status.ADRENALINE_1) {

            for(int i = 1; i<= j.getInt(NUMBER_OF_ACTIONS, STATUS_TAG,1); i++)
                actionList.add(new Action(j.getInt(STEPS + i, STATUS_TAG,1),
                        j.getBoolean(COLLECT + i, STATUS_TAG,1),
                        j.getBoolean(SHOOT + i, STATUS_TAG,1),
                        j.getBoolean(RELOAD + i, STATUS_TAG,1)));

        } else if (status == Status.ADRENALINE_2) {

            for(int i = 1; i<= j.getInt(NUMBER_OF_ACTIONS, STATUS_TAG,2); i++)
                actionList.add(new Action(j.getInt(STEPS + i , STATUS_TAG,2),
                        j.getBoolean(COLLECT + i , STATUS_TAG,2),
                        j.getBoolean(SHOOT + i , STATUS_TAG,2),
                        j.getBoolean(RELOAD + i , STATUS_TAG,2)));

        } else if (status == Status.FRENZY_1) {

            for(int i = 1; i<= j.getInt(NUMBER_OF_ACTIONS, STATUS_TAG,3); i++)
                actionList.add(new Action(j.getInt(STEPS + i , STATUS_TAG,3),
                        j.getBoolean(COLLECT + i, STATUS_TAG,3),
                        j.getBoolean(SHOOT + i, STATUS_TAG,3),
                        j.getBoolean(RELOAD + i, STATUS_TAG,3)));

        } else if (status == Status.FRENZY_2) {

            for(int i = 1; i<= j.getInt(NUMBER_OF_ACTIONS, STATUS_TAG,4); i++)
                actionList.add(new Action(j.getInt(STEPS + i, STATUS_TAG,4),
                        j.getBoolean(COLLECT + i, STATUS_TAG,4),
                        j.getBoolean(SHOOT + i, STATUS_TAG,4),
                        j.getBoolean(RELOAD + i, STATUS_TAG,4)));

        }

    }


    /**
     * Returns the squares the player can shoot from after moving up to a specified number of steps.
     *
     * @param steps         the maximum number of steps the player can takes before shooting.
     * @param toUse         the weapon to be used for shooting
     * @return              true if the player can shoot someone.
     *                      false otherwise.
     * @throws NotAvailableAttributeException if thrown by Weapon.listAvailableFiremodes or by DestinationFinder.find().
     */
    public List<Square> getShootingSquares(int steps, List<Weapon> toUse) throws NotAvailableAttributeException{

        List<Square> starting = new ArrayList<>();
        List<Square> start = board.getReachable(position, steps);
        Square square = this.position;

        //for every square
        for (Square s1: start){
            boolean found = false;
            boolean option1 = false;
            FireMode preMove = null;
            this.setVirtualPosition(s1);
            for (Weapon w: toUse){

                // if the player can shoot from thhe square with MAIN or SECONDARY firemode, set found to true.
                // The square will be added to the list.
                for (FireMode f : w.listAvailableFireModes()){
                    if (f.getName()==MAIN || f.getName() == SECONDARY)  found = true;
                    if (f.getName()==OPTION1){
                        option1 = true;
                        preMove = f;
                    }
                }
                // If only OPT1 is usable, check if it will unlock further squares.
                if (!found && option1) {
                    for (Square dest : preMove.getDestinationFinder().find(this, new ArrayList<>(Collections.singletonList(this)))) {
                        this.setVirtualPosition(dest);
                        for (FireMode f : w.listAvailableFireModes()) {
                            if (f.getName() == MAIN) found = true;
                        }
                        if (found) break;
                    }
                }
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
     * @return a list of the available action.
     * @throws NotAvailableAttributeException if thrown by removeShootingAction().
     */
    public List<Action> getAvailableActions() throws NotAvailableAttributeException{

        List<Action> availableActions = new ArrayList<>(getActionList());
        removeShootingAction(availableActions);
        removeCollectingAction(availableActions);
        return availableActions;

    }


    /**
     * Modifies and returns a list of actions, removing the shooting action if it cannot be executed.
     *
     * @param availableActions          the list of action before the possible removal.
     * @return                          the list of action after the possible removal.
     * @throws NotAvailableAttributeException if thrown by getShootingSquares().
     */
    public List<Action> removeShootingAction(List<Action> availableActions)  throws NotAvailableAttributeException{

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

        List<Square> possibleDest = new ArrayList<>(board.getMap());
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
     * @return the list of weapons the player can collect from the specified weapon square
     */
    public List<Weapon> getCollectibleWeapons(WeaponSquare weaponSquare){

        List<Weapon> collectible = new ArrayList<>();
        for (Weapon w : weaponSquare.getWeapons()){
            if (canPay(w.getReducedCost())) collectible.add(w);
        }
        return collectible;

    }


    /**
     * Returns the player color, depending on his hero.
     *
     * @return      the player color.
     */
    public String getColor(){
        if (name==HeroName.BANSHEE) return "blue";
        if (name==HeroName.D_STRUCT_OR) return "yellow";
        if (name==HeroName.DOZER) return "grey";
        if (name==HeroName.VIOLET) return "purple";
        if (name==HeroName.SPROG) return "green";
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

