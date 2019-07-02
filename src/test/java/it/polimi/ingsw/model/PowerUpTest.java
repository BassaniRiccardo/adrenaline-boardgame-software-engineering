
package it.polimi.ingsw.model;

import java.util.Collections;
import java.util.List;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.controller.PowerUpFactory;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.board.Square;
import it.polimi.ingsw.model.cards.Color;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the methods in PowerUp, for the different type of powerUps when relevant.
 *
 * @author BassaniRiccardo, marcobaga.
 */

public class PowerUpTest {


    /**
     * Checks that a targeting scope is correctly marked as available.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by isAvailable().
     */
    @Test
    public void isAvailableScope() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TARGETING_SCOPE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the scope
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        //Checks that the scope is available if a player has just been damaged
        b.getPlayers().get(1).setJustDamaged(true);
        assertTrue(p.isAvailable());
    }


    /**
     * Checks that a targeting scope is not available when it has no targets since no targets can be seen.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by isAvailable().
     */
    @Test
    public void isAvailableScope2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TARGETING_SCOPE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the scope
        p.setHolder(b.getPlayers().get(4));
        b.getPlayers().get(4).getPowerUpList().add(p);
        p = b.getPlayers().get(4).getPowerUpList().get(0);

        //Checks that the scope is not available if no player can be seen
        assertFalse(p.isAvailable());
    }


    /**
     * Checks that a targeting scope is not available when it has no targets since players are visible but cannot be selected.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by isAvailable().
     */
    @Test
    public void isAvailableScope3() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TARGETING_SCOPE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the scope
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        //Checks that the scope is available if no player has just been damaged
        assertFalse(p.isAvailable());
    }


    /**
     * Checks that a targeting scope is not available since the holder cannot pay to use it.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by isAvailable().
     */
    @Test
    public void isAvailableScope4() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TARGETING_SCOPE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the scope
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        //the player is initialized with only a ammo for every color
        //checks  that subtracting him an ammo of the scope color, the scope becomes unusable
        b.getPlayers().get(0).getAmmoPack().subAmmo(p.getColor());
        assertFalse(p.isAvailable());
    }


    /**
     * Checks that a targeting scope applies its effect.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by getHolder().
     */
    @Test
    public void applyEffectsScope() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TARGETING_SCOPE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the scope
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        int oldDamages = b.getPlayers().get(1).getDamages().size();
        p.applyEffects(Collections.singletonList(b.getPlayers().get(1)), p.getHolder().getPosition());
        int newDamages = b.getPlayers().get(1).getDamages().size();

        //checks the target got a damage
        assertEquals(newDamages, oldDamages + 1);
    }


    /**
     * Checks, for a targeting scope, that targets are selected when there are some.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findDestination().
     */
    @Test
    public void findTargetsScope() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TARGETING_SCOPE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the scope
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        b.getPlayers().get(1).setJustDamaged(true);

        //check p1 is the only target
        List<Player> ap = p.findTargets().get(0);
        assertEquals(Collections.singletonList(b.getPlayers().get(1)),ap);
    }


    /**
     * Checks that targets are selected when there are none.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findTargets().
     */
    @Test
    public void findTargetsScope2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TARGETING_SCOPE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the scope
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        // checks there are no targets
        assertTrue(p.findTargets().isEmpty());
    }


    /**
     * Checks, for a targeting scope that destinationFinder is working correctly.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findDestination().
     */
    @Test
    public void findDestinationsScope()throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TARGETING_SCOPE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the scope
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        //checks the scope does not meve its targets even if it is available
        b.getPlayers().get(1).setJustDamaged(true);
        assertTrue(p.isAvailable());
        assertTrue(p.findDestinations(b.getPlayers()).isEmpty());

    }


    /**
     * Checks that a tagback grenade is correctly marked as available.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by isAvailable().
     */
    @Test
    public void isAvailableGrenade() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TAGBACK_GRENADE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the grenade
        p.setHolder(b.getPlayers().get(1));
        b.getPlayers().get(1).getPowerUpList().add(p);
        p = b.getPlayers().get(1).getPowerUpList().get(0);

        //sets the current player
        b.setCurrentPlayer(b.getPlayers().get(0));

        //if the holder has not been damaged, he cannot use the grenade
        b.getPlayers().get(1).setJustDamaged(true);
        assertTrue(p.isAvailable());
    }


    /**
     * Checks that a tagback grenade is not available when it has no targets.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by isAvailable().
     */
    @Test
    public void isAvailableGrenade2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TAGBACK_GRENADE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the grenade
        p.setHolder(b.getPlayers().get(1));
        b.getPlayers().get(1).getPowerUpList().add(p);
        p = b.getPlayers().get(1).getPowerUpList().get(0);

        //sets the current player
        b.setCurrentPlayer(b.getPlayers().get(0));

        //if the holder has not been damaged, he cannot use the grenade
        assertFalse(p.isAvailable());
    }


    /**
     * Checks that a tagback grenade applies its effect.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by getHolder().
     */
    @Test
    public void applyEffectsGrenade() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TAGBACK_GRENADE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the grenade
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        int oldMarks = b.getPlayers().get(1).getMarks().size();

        //applies the effect with p1 as a target
        p.applyEffects(Collections.singletonList(b.getPlayers().get(1)), p.getHolder().getPosition());

        int newMarks = b.getPlayers().get(1).getMarks().size();

        assertEquals(newMarks, oldMarks + 1);
    }


    /**
     * Checks, for a tagback grenade,  that targets are selected when there are some.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findDestination().
     */
    @Test
    public void findTargetsGrenade() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TAGBACK_GRENADE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the grenade
        p.setHolder(b.getPlayers().get(1));
        b.getPlayers().get(1).getPowerUpList().add(p);
        p = b.getPlayers().get(1).getPowerUpList().get(0);
        b.getPlayers().get(1).setJustDamaged(true);

        //sets the current player
        b.setCurrentPlayer(b.getPlayers().get(0));

        assertFalse(p.findTargets().isEmpty());
        List<Player> ap = p.findTargets().get(0);
        assertTrue(ap.contains(b.getPlayers().get(0)));
    }


    /**
     * Checks, for a tagback grenade, that targets are selected when there are none.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findTargets().
     */
    @Test
    public void findTargetsGrenade2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TAGBACK_GRENADE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the grenade
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        //sets the current player
        b.setCurrentPlayer(b.getPlayers().get(1));

        //p0 is not damaged, then there are no targets
        assertTrue(p.findTargets().isEmpty());
    }


    /**
     * Checks, for a tagback grenade, that destinationFinder is working correctly.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findDestination().
     */
    @Test
    public void findDestinationsGrenade()throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TAGBACK_GRENADE){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the grenade
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        //makes available the grenade
        b.setCurrentPlayer(b.getPlayers().get(1));
        b.getPlayers().get(0).setJustDamaged(true);

        //the grenade is available, but it does not move the target
        assertTrue(p.isAvailable());
        assertTrue(p.findDestinations(b.getPlayers()).isEmpty());

    }


    /**
     * Checks that a newton is correctly marked as available.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by isAvailable().
     */
    @Test
    public void isAvailableNewton() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.NEWTON){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the newton
        p.setHolder(b.getPlayers().get(1));
        b.getPlayers().get(1).getPowerUpList().add(p);
        p = b.getPlayers().get(1).getPowerUpList().get(0);

        //checks that a newton is available when there are other players
        assertTrue(p.isAvailable());
    }


    /**
     * Checks that a newton is not available when it has no targets.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by isAvailable().
     */
    @Test
    public void isAvailableNewton2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.NEWTON){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the newton
        p.setHolder(b.getPlayers().get(4));
        b.getPlayers().get(4).getPowerUpList().add(p);
        p = b.getPlayers().get(4).getPowerUpList().get(0);

        for (int i = 0; i <5 ; i ++){
            b.getPlayers().remove(b.getPlayers().get(0));
        }

        //checks that a newton is not available when there are not other players
        assertFalse(p.isAvailable());
    }


    /**
     * Checks that a newton applies its effect.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by getHolder() or getPosition().
     */
    @Test
    public void applyEffectsNewton() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.NEWTON){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the newton
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        Square dest = b.getMap().get(6);
        assertNotEquals(dest, b.getPlayers().get(1).getPosition());

        //applies the effect with p1 as a target
        p.applyEffects(Collections.singletonList(b.getPlayers().get(1)), dest);

        assertEquals(dest, b.getPlayers().get(1).getPosition());
    }


    /**
     * Checks, for a newton, that targets are selected when there are some.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findDestination().
     */
    @Test
    public void findTargetsNewton() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.NEWTON){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the newton as [Player 2 : anonymous(Banshee)]
        p.setHolder(b.getPlayers().get(1));
        b.getPlayers().get(1).getPowerUpList().add(p);
        p = b.getPlayers().get(1).getPowerUpList().get(0);

        List<List<Player>> ap = p.findTargets();
        assertEquals("[[Player 1 : anonymous(D_struct_or)], [Player 3 : anonymous(Dozer)], [Player 4 : anonymous(Violet)], [Player 5 : anonymous(Sprog)]]",ap.toString());
    }


    /**
     * Checks, for a tagback grenade, that targets are selected when there are none.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findTargets().
     */
    @Test
    public void findTargetsNewton2() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.NEWTON){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the newton
        p.setHolder(b.getPlayers().get(4));
        b.getPlayers().get(4).getPowerUpList().add(p);
        p = b.getPlayers().get(4).getPowerUpList().get(0);

        //sets the current player
        for (int i = 0; i <5 ; i ++){
            b.getPlayers().remove(b.getPlayers().get(0));
        }
        //p0 is not damaged, then there are no targets
        assertTrue(p.findTargets().isEmpty());
    }


    /**
     * Checks, for a newton, that destinationFinder is working correctly.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findDestination().
     */
    @Test
    public void findDestinationsNewton()throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.NEWTON){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the newton
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        //checks the destinations, when the position of p1 is Sqaure 1.
        assertEquals("[Square 2, Square 3, Square 0, Square 5, Square 9]", p.findDestinations(Collections.singletonList(b.getPlayers().get(1))).toString());

    }


    /**
     * Checks that a teleporter is correctly marked as available.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by isAvailable().
     */
    @Test
    public void isAvailableTeleporter() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TELEPORTER){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the teleporter
        p.setHolder(b.getPlayers().get(1));
        b.getPlayers().get(1).getPowerUpList().add(p);
        p = b.getPlayers().get(1).getPowerUpList().get(0);

        //checks that a teleporter is always available.
        assertTrue(p.isAvailable());
    }


    /**
     * Checks that a teleporter applies its effect.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by getHolder() or getPosition().
     */
    @Test
    public void applyEffectsTeleporter() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TELEPORTER){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the teleporter
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        Square dest = b.getMap().get(6);
        assertNotEquals(dest, b.getPlayers().get(1).getPosition());

        //applies the effect with the holder as a target
        p.applyEffects(Collections.singletonList(b.getPlayers().get(0)), dest);

        assertEquals(dest, b.getPlayers().get(0).getPosition());
    }


    /**
     * Checks, for a teleporter, that targets are selected when there are some.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findDestination().
     */
    @Test
    public void findTargetsTeleporter() throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TELEPORTER){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the teleporter
        p.setHolder(b.getPlayers().get(1));
        b.getPlayers().get(1).getPowerUpList().add(p);
        p = b.getPlayers().get(1).getPowerUpList().get(0);

        List<List<Player>> ap = p.findTargets();
        assertEquals(Collections.singletonList(Collections.singletonList(b.getPlayers().get(1))),ap);
    }


    /**
     * Checks, for a teleporter, that destinationFinder is working correctly.
     *
     * @throws NoMoreCardsException                 if thrown by simulateScenario() or drawCard().
     * @throws UnacceptableItemNumberException      if thrown by simulateScenario().
     * @throws NotAvailableAttributeException       if thrown by findDestination().
     */
    @Test
    public void findDestinationsTeleporter()throws UnacceptableItemNumberException, NoMoreCardsException, NotAvailableAttributeException {
        Board b = BoardConfigurer.simulateScenario();
        PowerUp p = (PowerUp) b.getPowerUpDeck().drawCard();
        while(p.getName()!= PowerUp.PowerUpName.TELEPORTER){
            p = (PowerUp) b.getPowerUpDeck().drawCard();
        }

        //sets the holder of the teleporter
        p.setHolder(b.getPlayers().get(0));
        b.getPlayers().get(0).getPowerUpList().add(p);
        p = b.getPlayers().get(0).getPowerUpList().get(0);

        //checks the destinations, when the position of p1 is Sqaure 1.
        assertEquals(b.getMap(), p.findDestinations(Collections.singletonList(b.getPlayers().get(1))));

    }


    /**
     * Tests the method toString() of the enumeration PowerUpName.
     */
    @Test
    public void powerUpNameToString(){
        assertEquals("Targeting Scope", PowerUp.PowerUpName.TARGETING_SCOPE.toString() );
        assertEquals("Newton", PowerUp.PowerUpName.NEWTON.toString());
        assertEquals("Tagback Grenade", PowerUp.PowerUpName.TAGBACK_GRENADE.toString());
        assertEquals("Teleporter", PowerUp.PowerUpName.TELEPORTER.toString());

    }


    /**
     * Tests the method toString() of the class PowerUp.
     */
    @Test
    public void powerUpToString(){

        PowerUpFactory powerUpFactory = new PowerUpFactory(new Board());
        PowerUp powerUp = powerUpFactory.createPowerUp(PowerUp.PowerUpName.TARGETING_SCOPE, Color.YELLOW);

        //the name and the color are correct
        System.out.println("\nTesting Powerup.toString().\nYellow Targeting Scope.\nThe output is printed to console since it is the better way to check the color of a string.\n" );
        //it is shown through a println() since it is not possible to check the color of a string in another way
        System.out.println(powerUp.toString());

    }

}