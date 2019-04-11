package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

import java.util.Iterator;

import static java.util.Collections.*;

import static it.polimi.ingsw.model.Color.*;


/**
 * Represents a player of the game.
 * There are from 3 up to 5 instances of Player.
 * The id attribute is unique.
 *
 * @author  davidealde
 */

public class Player {

    public enum HeroName {
        D_STRUCT_OR, BANSHEE, DOZER, VIOLET, SPROG
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

    private List<Weapon> weaponList;
    private List<PowerUp> powerUpList;
    private AmmoPack ammoPack;

    private List<Action> actionList;
    private List<Player> mainTargets;
    private List<Player> optionalTargets;

    private int pointsToGive;
    private boolean justDamaged;
    private boolean overkilled;



    /**
     * Constructs a player with an id and a name.
     *
     * @param id               the player's id
     * @param name             the player's name
     */
    public Player(int id, HeroName name) {

        this.id = id;
        this.name = name;
        this.status = Status.BASIC;
        this.points=0;
        this.dead=false;
        this.flipped =false;

        this.damages = new ArrayList<>();
        this.marks = new ArrayList<>();

        this.position = null;
        this.previousPosition = null;

        this.weaponList = new ArrayList<>();
        this.powerUpList = new ArrayList<>();
        this.ammoPack =new AmmoPack(0,0,0);

        this.actionList = new ArrayList<>();
        this.mainTargets=new ArrayList<>();
        this.optionalTargets=new ArrayList<>();

        this.pointsToGive=8;
        this.justDamaged=false;
        this.overkilled = false;

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

    public AmmoPack getAmmopack(){return ammoPack;}

    public int getPointsToGive() {return pointsToGive;}

    public boolean isJustDamaged(){return justDamaged;}

    public Square getPosition(){return position;}

    public Square getPreviousPosition(){return previousPosition;}

    public List<Player> getMainTargets(){return mainTargets;}

    public List<Player> getOptionalTargets(){return optionalTargets;}

    public boolean isOverkilled(){return overkilled;}


    /**
     * Setters
     *
     */


    public void setPosition(Square square) {
        previousPosition = position;
        this.position = square;
        square.addPlayer(this);
    }

    public void setPointsToGive(int pointsToGive) {this.pointsToGive = pointsToGive;}

    public void setStatusFrenzy(Status status){this.status=status;}

    public void setJustDamaged(boolean justDamaged){this.justDamaged = justDamaged;}

    public void setFlipped(boolean flipped){this.flipped = flipped;}


    /**
     * Adds damages to the player.
     * Every damage is a reference to the shooter.
     *
     * @param amount         the amount of damage to add.
     * @param shooter        player who shoot
     */
    public void sufferDamage(int amount, Player shooter) {

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
     * @param number            the number of marks added.
     * @param shooter           the player who shoot.
     */
    public void addMarks(int number, Player shooter){

        for (int i = 0; i< number; i++){
            if (frequency(marks, shooter) < 3){
                marks.add(shooter);
            }
        }

    }


    /**
     * Adds a weapon to the player weapons list.
     *
     * @param addedWeapon           weapon added.
     */
    public void addWeapon(Weapon addedWeapon) {
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
    public void collect(Card collectedCard) throws NoMoreCardsException {

        position.removeCard(collectedCard);

        if (Board.getInstance().getSpawnPoints().contains(position)){
            addWeapon((Weapon)collectedCard);
        }
        else {
            addAmmoPack(((AmmoTile)collectedCard).getAmmoPack());
            if (((AmmoTile)collectedCard).hasPowerUp()) drawPowerUp();
        }

    }



    /**
     * Draws a random power up from the deck of power ups and adds it at the player power ups list.
     */
    public void drawPowerUp() throws NoMoreCardsException {

        PowerUp p = (PowerUp)Board.getInstance().getPowerUpDeck().drawCard();
        p.setHolder(this);
        powerUpList.add(p);

    }
    

    /**
     * Removes a weapon from the player weapons list.
     *
     * @param removedWeapon         the removed weapon.
     */
    public void discardWeapon(Card removedWeapon) {
        weaponList.remove(removedWeapon);
    }


    /**
     * Removes a power up from the player power up list.
     *
     * @param removedPowerUp        the removed power up.
     */
    public void discardPowerUp(Card removedPowerUp) {
        powerUpList.remove(removedPowerUp);
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
     */
    public void useAmmo(AmmoPack usedAmmo) {
        this.ammoPack.subAmmoPack(usedAmmo);
    }


    /**
     * Discards a power up to gain an ammo of the same color of the power up.
     *
     * @param p                     the discarded power up.
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
     * Adds a player to the main targets.
     *
     * @param target         player added to the main targets.
     */
    public void addMainTarget(Player target) {this.mainTargets.add(target); }


    /**
     * Updates the points the player will give to his killers the next time he'll die.
     * Called after the death of the player.
     */
    public void updateAwards() {

        if (pointsToGive!=1){
            if (pointsToGive==2) pointsToGive-= 1;
            else pointsToGive -= 2;
        }
    }


    /**
     * Gives point to the killers based on the number of damages every player did
     * and on the number of the player previous death.
     * Called when the player dies.
     */
    public void rewardKillers() {

        //firstblood
        damages.get(0).addPoints(1);

        //asks the board for the players
        List<Player> playersToReward = Board.getInstance().getPlayers();

        //properly orders the playersToReward
        sort(playersToReward, (p1, p2) -> {
            if (frequency(damages, p1) > frequency(damages, p2)) return -1;
            else if (frequency(damages, p1) < frequency(damages, p2)) return 1;
            else {
                if (damages.indexOf(p1) < damages.indexOf(p2)) return -1;
                else if (damages.indexOf(p1) > damages.indexOf(p2)) return 1;
                return 0;
            }
        });

        //assign the points
        int nextPointsToGive;
        if (pointsToGive == 2 || pointsToGive == 1)   nextPointsToGive = 1;
        else   nextPointsToGive = pointsToGive - 2;

        Iterator<Player> playerToRewardIt = playersToReward.iterator();
        while (playerToRewardIt.hasNext()) {
            Player p = playerToRewardIt.next();
            if (damages.contains(p)){
                p.addPoints(pointsToGive);
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
     */
    public void addPoints(int points) {
        this.points += points;
    }


    /**
     * Produces the list of possible actions for the player in his current status.
     * Called when the status changes.
     */
    public void refreshActionList() {

        actionList.clear();

        if (status == Status.BASIC) {

            actionList.add(new Action(3, false, false, false));
            actionList.add(new Action(1, true, false, false));
            actionList.add(new Action(0, false, true, false));

        } else if (status == Status.FRENZY_1) {

            actionList.add(new Action(1, false, true, true));
            actionList.add(new Action(4, false, false, false));
            actionList.add(new Action(2, true, false, false));

        } else if (status == Status.FRENZY_2) {

            actionList.add(new Action(2, false, true, true));
            actionList.add(new Action(3, true, false, false));

        } else if (status == Status.ADRENALINE_1) {

            actionList.add(new Action(3, false, false, false));
            actionList.add(new Action(2, true, false, false));
            actionList.add(new Action(0, false, true, false));

        } else if (status == Status.ADRENALINE_2) {

            actionList.add(new Action(3, false, false, false));
            actionList.add(new Action(2, true, false, false));
            actionList.add(new Action(1, false, true, false));

        }

    }


}

