package it.polimi.ingsw.model;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import static java.util.Collections.*;

import static it.polimi.ingsw.model.Color.*;

/**
 * Tests all methods of the class Player, covering all the instructions.
 */

public class PlayerTest {

    @Before
    public void setup() {

        BoardConfigurer.getInstance().configureMap(1);
        BoardConfigurer.getInstance().configureDecks();
    }


    /**
     * Tests addMarks()
     */
    @Test
    public void addMarks() {

        //instantiates the player who takes damage and two shooters
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //adds 2 marks from shooter
        player.addMarks(2, shooter);

        //checks that player has only 2 marks from shooter
        assertTrue(frequency(player.getMarks(), shooter) == 2 && player.getMarks().size() == 2);
    }


    /**
     * Tests addMarks()
     */
    @Test
    public void addMarksMultipleShooters() {

        //instantiates the player who takes damage and two shooters
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter1 = new Player(2, Player.HeroName.D_STRUCT_OR);
        Player shooter2 = new Player(3, Player.HeroName.DOZER);

        //adds 1 marks from shooter1 and 1 from shooter2
        player.addMarks(1, shooter1);
        player.addMarks(1, shooter2);

        //checks that player has only 1 marks from shooter1 and 1 from shooter2
        assertEquals(1,frequency(player.getMarks(), shooter1));
        assertEquals(1,frequency(player.getMarks(), shooter2));
        assertEquals(2,player.getMarks().size());

    }


    /**
     * Tests addMarks()
     */
    @Test
    public void addMarksMaximum3() {

        //instantiates the player who takes damage and two shooters
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //adds 3 marks from shooter
        player.addMarks(4, shooter);

        //checks that player has only 3 marks from shooter
        assertTrue(frequency(player.getMarks(), shooter) == 3 && player.getMarks().size() == 3);
    }


    /**
     * Tests collect() in an ammo square, when the ammo tile is available.
     */
    @Test(expected = NotAvailableAttributeException.class)
    public void collectAmmo() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException {

        //simulates a scenario, all the squares are filled
        BoardConfigurer.getInstance().simulateScenario();

        //a player with no ammo pack
        Player player = Board.getInstance().getPlayers().get(0);

        //checks that the player has no ammo
        assertEquals(0, player.getAmmoPack().getBlueAmmo());
        assertEquals(0, player.getAmmoPack().getRedAmmo());
        assertEquals(0, player.getAmmoPack().getYellowAmmo());

        assertNotNull(((AmmoSquare) player.getPosition()).getAmmoTile());

        //the player collects the ammo tile
        player.collect(((AmmoSquare)player.getPosition()).getAmmoTile());

        //checks that the player has some ammo
        assertFalse(player.getAmmoPack().getBlueAmmo()==0 && player.getAmmoPack().getRedAmmo()==0 &&
                    player.getAmmoPack().getYellowAmmo()==0 );

        //checks that the ammo square does not have an ammo tile anymore: exception thrown
        ((AmmoSquare) player.getPosition()).getAmmoTile();

    }

    /**
     * Tests collect() in an ammo square, when no ammo tile is available.
     */
    @Test
    public void collectNoAmmo() throws NoMoreCardsException, UnacceptableItemNumberException, NotAvailableAttributeException {

        //simulates a scenario, all the squares are filled
        BoardConfigurer.getInstance().simulateScenario();

        //a player with no ammo pack
        Player player1 = Board.getInstance().getPlayers().get(0);
        Player player2 = Board.getInstance().getPlayers().get(1);

        //checks there is a ammo tile in the square
        assertNotNull(((AmmoSquare) player1.getPosition()).getAmmoTile());

        //the first player collects the ammo tile
        player1.collect(((AmmoSquare)player1.getPosition()).getAmmoTile());

        //the second player moves in the square of the first player
        player2.setPosition(player1.getPosition());

        //the second players try to collect the ammo tile and fails
        try{
            player2.collect(((AmmoSquare)player2.getPosition()).getAmmoTile());
        }
        catch (NotAvailableAttributeException notAvailableAttributeException) {
        }
        catch (NoMoreCardsException noMoreCardsException) {
        }

        //checks that the first player has some ammo
        assertFalse(player1.getAmmoPack().getBlueAmmo()==0 && player1.getAmmoPack().getRedAmmo()==0 &&
                player1.getAmmoPack().getYellowAmmo()==0 );

        //checks that the second player has no ammo
        assertEquals(0, player2.getAmmoPack().getBlueAmmo());
        assertEquals(0, player2.getAmmoPack().getRedAmmo());
        assertEquals(0, player2.getAmmoPack().getYellowAmmo());

        //checks that the square has no ammo left
        try{
            ((AmmoSquare) player1.getPosition()).getAmmoTile();
        }
        catch (NotAvailableAttributeException notAvailableAttributeException) {
        }

    }

    /**
     * Tests drawPowerUp().
     */
    @Test
    public void drawPowerUp() throws NoMoreCardsException, UnacceptableItemNumberException {

        //instantiates the player
        Player player = new Player(1, Player.HeroName.VIOLET);


        //checks that poweruplist size is 0
        assertEquals(0,player.getPowerUpList().size());
        //draws powerUp
        player.drawPowerUp();

        //checks that poweruplist size is 1
        assertEquals(1,player.getPowerUpList().size());

    }


    /**
     * Tests drawPowerUp(), drawing 2 power ups.
     */
    @Test
    public void drawPowerUpMultiple() throws NoMoreCardsException, UnacceptableItemNumberException {

        //instantiates the player
        Player player = new Player(1, Player.HeroName.VIOLET);


        //checks that poweruplist size is 0
        assertEquals(0,player.getPowerUpList().size());
        //draws powerUp
        player.drawPowerUp();

        //checks that poweruplist size is 1
        assertEquals(1,player.getPowerUpList().size());

        //saves this power up for a later confrontation
        Card confr = player.getPowerUpList().get(0);

        //draws another powerUp
        player.drawPowerUp();

        //checks that poweruplist size is 2
        assertEquals(2,player.getPowerUpList().size());

        //checks that the previous powerUp did not changed
        assertEquals(confr,player.getPowerUpList().get(0));

    }


    /**
     * Tests discardPowerUp()
     */
    @Test
    public void discardPowerUp() throws NoMoreCardsException, UnacceptableItemNumberException {

        //instantiates the player
        Player player = new Player(1, Player.HeroName.VIOLET);

        //draws 2 times
        player.drawPowerUp();
        player.drawPowerUp();

        //saves what powerUp that there are
        Card powerUp1;
        Card powerUp2;
        powerUp1 = player.getPowerUpList().get(0);
        powerUp2 = player.getPowerUpList().get(1);

        //discards powerUp1
        player.discardPowerUp(powerUp1);

        //checks that powerUpList contains only powerUp2
        assertTrue(player.getPowerUpList().contains(powerUp2) && player.getPowerUpList().size() == 1);

    }


    /**
     * Tests addPoints()
     */
    @Test
    public void addPoints() {

        //instantiates the player
        Player player = new Player(1, Player.HeroName.VIOLET);

        //adds 2 point
        player.addPoints(2);

        //checks that player has 2 point
        assertEquals(2,player.getPoints());

    }


    /**
     * Tests addPoints()
     */
    @Test
    public void addPointsMultiple() {

        //instantiates the player
        Player player = new Player(1, Player.HeroName.VIOLET);

        //adds 1 point
        player.addPoints(1);

        //checks that player has 1 point
        assertEquals(1,player.getPoints());

        //adds 2 point
        player.addPoints(2);

        //checks that player has 3 point
        assertEquals(3,player.getPoints() );
    }


    /**
     * Tests addWeapon()
     */
    @Test
    public void addWeapon() throws UnacceptableItemNumberException, NoMoreCardsException {

        //simulates a scenario
        BoardConfigurer.getInstance().simulateScenario();

        //instantiates the player
        Player player = Board.getInstance().getPlayers().get(0);

        //instantiates a weapon
        Weapon weapon = WeaponFactory.createWeapon(Weapon.WeaponName.THOR);

        //adds weapon
        player.addWeapon(weapon);

        //checks that weaponsList contains only weapon
        assertTrue(player.getWeaponList().contains(weapon) && player.getWeaponList().size() == 1);

    }


    /**
     * Tests addWeapon()
     */
    @Test
    public void addWeaponMultiple() throws UnacceptableItemNumberException, NoMoreCardsException {

        //simulates a scenario
        BoardConfigurer.getInstance().simulateScenario();

        //instantiates the player
        Player player = Board.getInstance().getPlayers().get(2);

        //instantiates 2 weapons
        Weapon weapon1 = WeaponFactory.createWeapon(Weapon.WeaponName.THOR);
        Weapon weapon2 = WeaponFactory.createWeapon(Weapon.WeaponName.SHOTGUN);

        //adds weapons
        player.addWeapon(weapon1);
        player.addWeapon(weapon2);

        //checks that weaponsList contains only the rights weapons
        assertTrue(player.getWeaponList().contains(weapon1) &&
                player.getWeaponList().contains(weapon2) &&
                player.getWeaponList().size() == 2);

    }


    /**
     * Tests discardWeapon()
     */
    @Test
    public void discardWeapon()  throws UnacceptableItemNumberException, NoMoreCardsException {

        //simulates a scenario
        BoardConfigurer.getInstance().simulateScenario();

        //instantiates the player
        Player player = Board.getInstance().getPlayers().get(3);

        //instantiates 2 weapons
        Weapon weapon1 = WeaponFactory.createWeapon(Weapon.WeaponName.THOR);
        Weapon weapon2 = WeaponFactory.createWeapon(Weapon.WeaponName.SHOTGUN);

        //adds weapons
        player.addWeapon(weapon1);
        player.addWeapon(weapon2);

        //discards weapon2
        player.discardWeapon(weapon2);

        //checks that weaponslist contains only weapon1
        assertTrue(player.getWeaponList().contains(weapon1) && player.getWeaponList().size() == 1);
    }


    /**
     * Tests updateAwards()
     */
    @Test
    public void updateAwards() throws WrongTimeException {

        //instantiates the player
        Player player = new Player(1, Player.HeroName.VIOLET);

        //kills the player
        player.sufferDamage(11, new Player (2, Player.HeroName.DOZER));

        //calls updateAwards
        player.updateAwards();

        //checks that pointsToGive is 6
        assertEquals(6,player.getPointsToGive());

        //calls updateAwards
        player.updateAwards();

        //checks that pointsToGive is 4
        assertEquals(4,player.getPointsToGive());

        //calls updateAwards
        player.updateAwards();

        //checks that pointsToGive is 2
        assertEquals(2,player.getPointsToGive());

        //calls updateAwards
        player.updateAwards();

        //checks that pointsToGive is 1
        assertEquals(1,player.getPointsToGive());

        //calls updateAwards
        player.updateAwards();

        //checks that pointsToGive is 1
        assertEquals(1,player.getPointsToGive());

    }


    /**
     * Tests useAsAmmo()
     */
    @Test
    public void useAsAmmo() throws NoMoreCardsException, UnacceptableItemNumberException {
        //instantiates the player
        Player player = new Player(1, Player.HeroName.VIOLET);

        //draws a powerUp
        player.drawPowerUp();

        //save the powerUp
        PowerUp powerUp;
        powerUp = player.getPowerUpList().get(0);
        Color color;
        color = powerUp.getColor();

        //calls useAsAmmo
        player.useAsAmmo(powerUp);

        //checks that powerUpList is empty
        assertTrue(player.getPowerUpList().isEmpty());

        //checks that myAmmoPack contains only the ammo of the right color
        if (color == RED) {
            assertTrue(player.getAmmoPack().getRedAmmo() == 1 && player.getAmmoPack().getYellowAmmo() == 0 && player.getAmmoPack().getBlueAmmo() == 0);
        } else if (color == YELLOW) {
            assertTrue(player.getAmmoPack().getRedAmmo() == 0 && player.getAmmoPack().getYellowAmmo() == 1 && player.getAmmoPack().getBlueAmmo() == 0);
        } else {
            assertTrue(player.getAmmoPack().getRedAmmo() == 0 && player.getAmmoPack().getYellowAmmo() == 0 && player.getAmmoPack().getBlueAmmo() == 1);
        }

    }


    /**
     * Tests addAmmoPack()
     */
    @Test
    public void addAmmoPackPlayer() {

        //instantiates the player
        Player player = new Player(1, Player.HeroName.VIOLET);

        //instantiates an AmmoPack
        AmmoPack ammoPack = new AmmoPack(1, 2, 3);

        //calls addAmmoPack
        player.addAmmoPack(ammoPack);

        //checks that myAmmoPacks contains the right amount of ammos of every color
        assertTrue(player.getAmmoPack().getRedAmmo() == 1 && player.getAmmoPack().getBlueAmmo() == 2 && player.getAmmoPack().getYellowAmmo() == 3);
    }


    /**
     * Tests useAmmo()
     */
    @Test
    public void useAmmo() {
        //instantiates the player
        Player player = new Player(1, Player.HeroName.VIOLET);

        //instantiates 2 AmmoPacks
        AmmoPack ammoPack1 = new AmmoPack(1, 2, 3);
        AmmoPack ammoPack2 = new AmmoPack(1, 1, 2);

        //adds ammos
        player.addAmmoPack(ammoPack1);

        //calls useAmmo
        player.useAmmo(ammoPack2);

        //checks that myAmmoPacks contains the right amount of ammos of every color
        assertTrue(player.getAmmoPack().getRedAmmo() == 0 && player.getAmmoPack().getBlueAmmo() == 1 && player.getAmmoPack().getYellowAmmo() == 1);
    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamage() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(2, shooter);

        //checks that damages are done and right
        assertEquals(shooter,player.getDamages().get(0));
        assertEquals(shooter,player.getDamages().get(1));
        assertEquals(2,player.getDamages().size());

    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageMultipleShooters() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter1 = new Player(2, Player.HeroName.D_STRUCT_OR);
        Player shooter2 = new Player(3, Player.HeroName.DOZER);

        //calls sufferDamage
        player.sufferDamage(1, shooter1);
        player.sufferDamage(2, shooter2);

        //checks that the damages and the damages.size() are right
        assertEquals(shooter1,player.getDamages().get(0));
        assertEquals(shooter2,player.getDamages().get(1));
        assertEquals(shooter2,player.getDamages().get(2));
        assertEquals(3,player.getDamages().size());

    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageJustDamaged() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(1, shooter);

        //checks that JustDamaged is right
        assertTrue(player.isJustDamaged());

    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageNotOverkilled() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(1, shooter);

        //checks that overkilled is right
        assertFalse(player.isOverkilled());

    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageNotDead() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(1, shooter);

        //checks that dead is right
        assertFalse(player.isDead());

    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageStatus1() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(1, shooter);

        //checks that status is right
        assertEquals(Player.Status.BASIC,player.getStatus());
    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageStatus2() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);


        //calls sufferDamage
        player.sufferDamage(3, shooter);

        //checks that status is right
        assertEquals(Player.Status.ADRENALINE_1,player.getStatus());

    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageStatus3() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(7, shooter);


        //checks that status is right
        assertEquals(Player.Status.ADRENALINE_2,player.getStatus());
    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageAndMarks() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //adds marks
        player.addMarks(3, shooter);

        //calls sufferDamage
        player.sufferDamage(7, shooter);

        //checks that the damages and marks amount are correct
        assertEquals(10, player.getDamages().size());
        assertEquals(0, player.getMarks().size());
    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageDeath() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(11, shooter);

        //checks that the damages and marks amount are correct
        assertTrue(player.isDead());
        assertFalse(player.isOverkilled());
    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageOverkilled() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(12, shooter);

        //checks that the damages and marks amount are correct
        assertTrue(player.isOverkilled());
    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageMarksToOverkiller() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(12, shooter);

        //checks that overkiller has only the right mark
        assertTrue(shooter.getMarks().get(0) == player && shooter.getMarks().size() == 1);
    }


    /**
     * Tests sufferDamage()
     */
    @Test
    public void sufferDamageMax12Dmgs() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //calls sufferDamage
        player.sufferDamage(13, shooter);

        //checks that the size of damages is 12
        assertEquals(12,player.getDamages().size());
    }



    /**
     * Tests rewardKillers()
     */
    @Test
    public void rewardKillers() throws WrongTimeException {

        BoardConfigurer.getInstance().configurePlayerOptions(3);

        //instantiates the players
        Player player1 = Board.getInstance().getPlayers().get(0);
        Player shooter = Board.getInstance().getPlayers().get(1);
        Player player2 = Board.getInstance().getPlayers().get(2);


        //puts a damage from shooter in damages of player
        player1.sufferDamage(11, shooter);

        //rewards the killers
        player1.rewardKillers();

        //check that gives awards only to the killer, first blood included
        assertEquals(9, shooter.getPoints());
        assertEquals(0, player1.getPoints());
        assertEquals(0, player2.getPoints());
    }


    /**
     * Tests rewardKillers()
     */
    @Test
    public void rewardKillersManyShooters1() throws WrongTimeException {

        BoardConfigurer.getInstance().configurePlayerOptions(5);

        //initializes the killShotTrack and five players, with 0 points

        Player player = Board.getInstance().getPlayers().get(0);
        Player shooter1 = Board.getInstance().getPlayers().get(1);
        Player shooter2 = Board.getInstance().getPlayers().get(2);
        Player shooter3 = Board.getInstance().getPlayers().get(3);
        Player shooter4 = Board.getInstance().getPlayers().get(4);



        //adds damages
        player.sufferDamage(1, shooter1);
        player.sufferDamage(2, shooter2);
        player.sufferDamage(3, shooter3);
        player.sufferDamage(5, shooter4);

        //rewards the killers
        player.rewardKillers();


        //checks that gives awards right
        assertEquals(3,shooter1.getPoints());
        assertEquals(4,shooter2.getPoints());
        assertEquals(6,shooter3.getPoints());
        assertEquals(8,shooter4.getPoints());
    }


    /**
     * Tests rewardKillers()
     */
    @Test
    public void rewardKillersManyShooters2() throws WrongTimeException{

        BoardConfigurer.getInstance().configurePlayerOptions(5);

        Player player = Board.getInstance().getPlayers().get(0);
        Player shooter1 = Board.getInstance().getPlayers().get(1);
        Player shooter2 = Board.getInstance().getPlayers().get(2);
        Player shooter3 = Board.getInstance().getPlayers().get(3);
        Player shooter4 = Board.getInstance().getPlayers().get(4);

        //simulates 2 deaths of the player
        player.sufferDamage(11,shooter1);
        player.updateAwards();
        player.sufferDamage(11,shooter1);
        player.updateAwards();

        //adds damages
        player.sufferDamage(1, shooter1);
        player.sufferDamage(2, shooter2);
        player.sufferDamage(3, shooter3);
        player.sufferDamage(5, shooter4);

        //rewards the killers
        player.rewardKillers();

        //checks that gives awards right
        assertEquals(2,shooter1.getPoints());
        assertEquals(1,shooter2.getPoints());
        assertEquals(2,shooter3.getPoints());
        assertEquals(4,shooter4.getPoints());
    }


    /**
     * Tests refreshActionList()
     */
    @Test
    public void refreshActionListBASIC() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);

        //calls refreshActionList
        player.refreshActionList();

        //checks that she has only the rights abilities
        assertTrue(player.getActionList().get(0).equals(new Action(3, false, false, false))
                        &&player.getActionList().get(1).equals(new Action(1, true, false, false))
                        &&player.getActionList().get(2).equals(new Action(0, false, true, false))
                        &&player.getActionList().size() == 3);
    }


    /**
     * Tests refreshActionList()
     */
    @Test
    public void refreshActionListFRENZY_1() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);

        //set the status FRENZY_1
        player.setStatusFrenzy(Player.Status.FRENZY_1);

        //calls refreshActionList
        player.refreshActionList();

        //checks that she has only the rights abilities
        assertTrue(player.getActionList().get(0).equals(new Action(1, false, true, true))
                &&player.getActionList().get(1).equals(new Action(4, false, false, false))
                &&player.getActionList().get(2).equals(new Action(2, true, false, false))
                && player.getActionList().size() == 3);
    }


    /**
     * Tests refreshActionList()
     */
    @Test
    public void refreshActionListFRENZY_2() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);

        //set the status FRENZY_2
        player.setStatusFrenzy(Player.Status.FRENZY_2);

        //calls refreshActionList
        player.refreshActionList();

        //checks that she has only the rights abilities
        assertTrue(player.getActionList().get(0).equals(new Action(2, false, true, true)) &&
                player.getActionList().get(1).equals(new Action(3, true, false, false)) &&
                player.getActionList().size() == 2);
    }


    /**
     * Tests refreshActionList()
     */
    @Test
    public void refreshActionListADRENALINE_1() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //set the status ADRENALINE_1
        player.sufferDamage(3, shooter);

        //calls refreshActionList
        player.refreshActionList();

        //checks that she has only the rights abilities
        assertTrue(player.getActionList().get(0).equals(new Action(3, false, false, false)) &&
                player.getActionList().get(1).equals(new Action(2, true, false, false)) &&
                player.getActionList().get(2).equals(new Action(0, false, true, false)) &&
                player.getActionList().size() == 3);
    }


    /**
     * Tests refreshActionList()
     */
    @Test
    public void refreshActionListADRENALINE_2() {

        //instantiates the players
        Player player = new Player(1, Player.HeroName.VIOLET);
        Player shooter = new Player(2, Player.HeroName.D_STRUCT_OR);

        //set the status ADRENALINE_2
        player.sufferDamage(6, shooter);

        //calls refreshActionList
        player.refreshActionList();

        //checks that she has only the rights abilities
        assertTrue(player.getActionList().get(0).equals(new Action(3, false, false, false)) &&
                player.getActionList().get(1).equals(new Action(2, true, false, false)) &&
                player.getActionList().get(2).equals(new Action(1, false, true, false)) &&
                player.getActionList().size() == 3);
    }

}