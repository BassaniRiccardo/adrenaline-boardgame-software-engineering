package it.polimi.ingsw.model.board;

import it.polimi.ingsw.controller.ModelDataReader;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
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

    private int pointsToGive;
    private boolean justDamaged;
    private boolean overkilled;

    private boolean inGame;
    private static ModelDataReader J = new ModelDataReader();


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
        this.ammoPack =new AmmoPack(J.getInt("initialRAmmo"),J.getInt("initialBAmmo"),J.getInt("initialYAmmo"));

        this.actionList = new ArrayList<>();
        this.mainTargets=new ArrayList<>();
        this.optionalTargets=new ArrayList<>();

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

    public boolean isJustDamaged(){return justDamaged;}

    public Square getPosition() throws NotAvailableAttributeException {
        if (position == null) throw new NotAvailableAttributeException("The player is not on the board.");
        return position;}

    public Square getPreviousPosition() throws NotAvailableAttributeException  {

        if (previousPosition == null) throw new NotAvailableAttributeException("The player was not on the board.");
        return previousPosition;}

    public List<Player> getMainTargets(){return mainTargets;}

    public List<Player> getOptionalTargets(){return optionalTargets;}

    public boolean isOverkilled(){return overkilled;}

    public boolean isInGame(){ return inGame;}

    public Board getBoard() {return board; }

    /**
     * Setters
     *
     */


    public void setPosition(Square square) {

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

    public void setStatus(Status status){this.status=status;}

    public void setJustDamaged(boolean justDamaged){this.justDamaged = justDamaged;}

    public void setFlipped(boolean flipped){this.flipped = flipped;}

    public void setInGame(boolean inGame) {this.inGame = inGame;  }

    public void setDead(boolean dead) {this.dead = dead;}

    public void setDamages(List<Player> damages) { this.damages = damages; }

    public void setWeaponList(List<Weapon> weaponList) { this.weaponList = weaponList;}

    public void setPowerUpList(List<PowerUp> powerUpList) {
        this.powerUpList = powerUpList;
        for (PowerUp p : powerUpList){
            p.setHolder(this);
        }
    }

    public void setAmmoPack(AmmoPack ammoPack) { this.ammoPack = ammoPack; }

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
    }


    /**
     * Adds marks to the player marks.
     * Every mark is a reference to the shooter.
     *
     * @param amount            the number of marks added.
     * @param shooter           the player who shoot.
     */
    public void addMarks(int amount, Player shooter){

        if (amount < 1) throw new IllegalArgumentException("Not valid number of marks");
        if (shooter == this) throw new IllegalArgumentException("A player can not shoot himself");

        for (int i = 0; i< amount; i++){
            if (frequency(marks, shooter) < J.getInt("maximumMarks")){
                marks.add(shooter);
            }
        }

    }


    /**
     * Adds a weapon to the player weapons list.
     *
     * @param addedWeapon           weapon added.
     */
    public void addWeapon(Weapon addedWeapon) throws UnacceptableItemNumberException {

        if (this.weaponList.size()>J.getInt("maxWeapons")) throw new UnacceptableItemNumberException("A player can hold up to 3 weapons; 4 are allowed while choosing which one to discard.");
        addedWeapon.setHolder(this);
        weaponList.add(addedWeapon);
    }
    

    /**
     * Adds ammo to the AmmoPack of the player.
     *
     * @param ammoPack         ammo added.
     */
    public void addAmmoPack(AmmoPack ammoPack) {this.ammoPack.addAmmoPack(ammoPack); }


    /**
     * Collects a weapon.
     *
     */
    public boolean collect(Card collectedCard) throws NoMoreCardsException, UnacceptableItemNumberException, WrongTimeException {

        position.removeCard(collectedCard);

        if (this.board.getSpawnPoints().contains(position)){
                this.getAmmoPack().subAmmoPack(((Weapon)collectedCard).getReducedCost());
                ((Weapon)collectedCard).setLoaded(true);
                addWeapon((Weapon) collectedCard);
        }
        else {
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

        if (this.powerUpList.size() > 4) throw new UnacceptableItemNumberException("A player can normally hold up to 3 power ups; 4 are allowed in the process of rebirth. More than 4 are never allowed.");
        if (this.board.getPowerUpDeck().getDrawable().isEmpty()){
            this.board.getPowerUpDeck().regenerate();
        }
        PowerUp p = (PowerUp)this.board.getPowerUpDeck().drawCard();
        p.setHolder(this);
        powerUpList.add(p);

    }
    

    /**
     * Removes a weapon from the player weapons list.
     *
     * @param removedWeapon         the removed weapon.
     */
    public void discardWeapon(Card removedWeapon) {
        if (!weaponList.contains(removedWeapon)) throw new IllegalArgumentException("The player does not own this weapon");
        weaponList.remove(removedWeapon);
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
    }


    /**
     * Discards a power up to gain an ammo of the same color of the power up.
     *
     * @param p                     the discarded power up.
     * @throws      IllegalArgumentException
     */
    public void useAsAmmo(PowerUp p) {

        this.discardPowerUp(p);
        if (p.getColor() == RED) {
            ammoPack.addAmmoPack(new AmmoPack(1, 0, 0));
        } else if ((p.getColor() == YELLOW)) {
            ammoPack.addAmmoPack(new AmmoPack(0, 0, 1));
        } else {
            ammoPack.addAmmoPack(new AmmoPack(0, 1, 0));
        }

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
            if(w.canFire()){
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
        if (pointsToGive!=1){
            if (pointsToGive==2) pointsToGive-= 1;
            else pointsToGive -= 2;
        }
        this.damages.clear();
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
                System.out.println("Player " + p.getId() + " gains " + totalGivenPoints + " points.");
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

            for(int i=1;i<=J.getInt("numberOfActions","status",0);i++)
                actionList.add(new Action(J.getInt("steps"+i,"status",0),
                        J.getBoolean("collect"+i,"status",0),
                        J.getBoolean("shoot"+i,"status",0),
                        J.getBoolean("reload"+i,"status",0)));

        } else if (status == Status.ADRENALINE_1) {

            for(int i=1;i<=J.getInt("numberOfActions","status",1);i++)
                actionList.add(new Action(J.getInt("steps"+i,"status",1),
                        J.getBoolean("collect"+i,"status",1),
                        J.getBoolean("shoot"+i,"status",1),
                        J.getBoolean("reload"+i,"status",1)));

        } else if (status == Status.ADRENALINE_2) {

            for(int i=1;i<=J.getInt("numberOfActions","status",2);i++)
                actionList.add(new Action(J.getInt("steps"+i,"status",2),
                        J.getBoolean("collect"+i,"status",2),
                        J.getBoolean("shoot"+i,"status",2),
                        J.getBoolean("reload"+i,"status",2)));

        } else if (status == Status.FRENZY_1) {

            for(int i=1;i<=J.getInt("numberOfActions","status",3);i++)
                actionList.add(new Action(J.getInt("steps"+i,"status",3),
                        J.getBoolean("collect"+i,"status",3),
                        J.getBoolean("shoot"+i,"status",3),
                        J.getBoolean("reload"+i,"status",3)));

        } else if (status == Status.FRENZY_2) {

            for(int i=1;i<=J.getInt("numberOfActions","status",4);i++)
                actionList.add(new Action(J.getInt("steps"+i,"status",4),
                        J.getBoolean("collect"+i,"status",4),
                        J.getBoolean("shoot"+i,"status",4),
                        J.getBoolean("reload"+i,"status",4)));

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
        for (Square s1: start){
            boolean found = false;
            this.setPosition(s1);
            for (Weapon w: weaponToConsider){
                if (!w.listAvailableFireModes().isEmpty()) found = true;
                //for (FireMode f : w.getFireModeList()){
                  //  if (!(f.getTargetFinder().find(this).isEmpty())) {
                    //    found = true;
                    //}
                //}
            }
            if (found && !starting.contains(s1)) starting.add(s1);
        }
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
            if (possibleDest.contains(s) && board.getSpawnPoints().contains(s) && getCollectibleWeapons((WeaponSquare)s).isEmpty()) possibleDest.remove(s);

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

        List<Weapon> collectable = new ArrayList<>();
        for (Weapon w : weaponSquare.getWeapons()){
            if (hasEnoughAmmo(w.getReducedCost())) collectable.add(w);
        }
        return collectable;

    }


    /**
     * Returns a string representing the player.
     *
     * @return      the description of the player.
     */
    @Override
    public String toString() {
        return "Player " + id + " : " + name;
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

