package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.controller.PowerUpFactory;
import it.polimi.ingsw.controller.WeaponFactory;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.cards.AmmoPack;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PowerUp;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.model.cards.Color.*;
import static it.polimi.ingsw.model.cards.PowerUp.PowerUpName.*;
import static it.polimi.ingsw.model.cards.Weapon.WeaponName.*;
import static org.junit.Assert.*;

/**
 * Tests all methods of the class Deck.
 * The methods are tested interchangeably with decks of weapon, ammoTile, powerUps.
 *
 * @author BassaniRiccardo
 */

public class DeckTest {


    /**
     * Tests the method addAllCards() for a deck of weapons.
     */
    @Test
    public void addCardWeapon() {

        Board board1 = BoardConfigurer.getInstance().configureMap(1);
        WeaponFactory weaponFactory = new WeaponFactory(board1);

        //creates an new empty deck of weapons
        Deck weaponsDeck = new Deck();

        //creates a new weapon
        Card lockRifle = weaponFactory.createWeapon(LOCK_RIFLE);

        //checks that the deck does not contain the created weapon
        assertFalse((weaponsDeck.getDrawable()).contains(lockRifle));
        assertEquals(0,weaponsDeck.getDrawable().size());

        //adds the created weapon as a drawable card
        weaponsDeck.addCard(lockRifle);

        //checks that the deck contains the added weapon as a drawable card
        assertTrue((weaponsDeck.getDrawable()).contains(lockRifle));
        assertEquals(1,weaponsDeck.getDrawable().size());

    }


    /**
     * Tests the method addDiscardedCard() for a deck of ammoTiles.
     */
    @Test
    public void addDiscardedCardAmmoTile() {

        //creates an new empty deck of ammo tiles
        Deck ammoDeck = new Deck();

        //creates a new ammo tile
        AmmoTile ammo = new AmmoTile(false, new AmmoPack(2,1,0));

        //checks that the deck does not contain the created ammo tile
        assertFalse(ammoDeck.getDiscarded().contains(ammo));

        //adds the created ammo tile as a discarded card
        ammoDeck.addDiscardedCard(ammo);

        //checks that the deck contains the added ammo tile as a discarded card
        assertTrue(ammoDeck.getDiscarded().contains(ammo));
    }


    /**
     * Tests the method drawCard() for a deck of powerUps.
     */
    @Test
    public void drawCardPowerUp() throws NoMoreCardsException {

        Board board1 = BoardConfigurer.getInstance().configureMap(1);
        PowerUpFactory powerUpFactory = new PowerUpFactory(board1);

        //creates an new empty deck of ammo tiles
        Deck powerUpDeck = new Deck();

        //creates two power ups and adds them to the deck
        PowerUp powerUp1 = powerUpFactory.createPowerUp(TELEPORTER, BLUE);
        PowerUp powerUp2 = powerUpFactory.createPowerUp(TAGBACK_GRENADE, YELLOW);
        powerUpDeck.getDrawable().add(powerUp1);
        powerUpDeck.getDrawable().add(powerUp2);

        //creates a copy of the deck
        Deck oldPowerUpDeck = new Deck();
        oldPowerUpDeck.getDrawable().addAll(powerUpDeck.getDrawable());

        //memorizes the values of the cards before a card has been drawn
        Card firstBeforeDrawn = powerUpDeck.getDrawable().get(0);
        Card secondBeforeDrawn = powerUpDeck.getDrawable().get(1);

        //draws a card
        Card drawn = powerUpDeck.drawCard();

        //checks that the first card has been drawn
        assertEquals(firstBeforeDrawn, drawn);

        //checks that the card that was in second position is now the first one of the deck
        assertEquals(secondBeforeDrawn, powerUpDeck.getDrawable().get(0));

        //checks that the size of the deck decreased by 1.
        assertEquals(oldPowerUpDeck.getDrawable().size()-1, powerUpDeck.getDrawable().size());

    }


    /**
     * Tests the method drawCard() for a deck of powerUps, when an exception should be thrown since the deck is empty.
     */
    @Test(expected = NoMoreCardsException.class)

    public void drawCardPowerUpEmptyDeck() throws NoMoreCardsException {

        //creates an new empty deck of power ups
        Deck powerUpDeck = new Deck();

        //draws a card
        Card drawn = powerUpDeck.drawCard();

        //checks that the size of the deck is still 0
        assertEquals(0, powerUpDeck.getDrawable().size());

    }


    /**
     * Tests the method drawCard() for a deck of power ups, drawing multiple cards.
     * Draws 4 cards.
     * The tested deck is configured as a real deck, containing all the power up cards of the game.
     */
    @Test
    public void drawMultiplePowerUpsFromRealDeck() throws NoMoreCardsException {

        //creates the board with the decks.
        Board board1 = BoardConfigurer.getInstance().configureMap(1);
        BoardConfigurer.getInstance().configureDecks(board1);

        //create a copy of the deck of power ups
        Deck powerUpDeck = board1.getPowerUpDeck();
        Deck oldPowerUpDeck = new Deck();
        oldPowerUpDeck.getDrawable().addAll(powerUpDeck.getDrawable());

        //checks that the copied deck has the same drawable cards of the original
        assertEquals(powerUpDeck.getDrawable(), oldPowerUpDeck.getDrawable());

        //creates a list of drawn cards
        List<Card> drawn = new ArrayList<>();

        //draws 4 cards
        for (int i = 0; i < 4; i++){
            drawn.add(powerUpDeck.drawCard());
        }

        //checks that the cards have been drawn in the correct order
        assertEquals(oldPowerUpDeck.getDrawable().subList(0,4), drawn);

        //checks that the size of the deck decreased by 4.
        assertEquals(oldPowerUpDeck.getDrawable().size()-4, powerUpDeck.getDrawable().size());

    }


    /**
     * Tests the method shuffleDeck() for a deck of powerUps.
     * The tested deck is configured as a real deck, containing all the powerUp cards of the game.
     */
    @Test
    public void shuffleRealDeckPowerUp() {

        //creates the board with the decks.
        Board board1 = BoardConfigurer.getInstance().configureMap(1);
        BoardConfigurer.getInstance().configureDecks(board1);
        Deck powerUpDeck = board1.getPowerUpDeck();

        //creates a deck with the same drawable cards of the deck of power ups
        Deck oldPowerUpDeck = new Deck();
        oldPowerUpDeck.getDrawable().addAll(powerUpDeck.getDrawable());

        //checks that the copied deck has the same drawable cards of the original
        assertEquals(powerUpDeck.getDrawable(), oldPowerUpDeck.getDrawable());

        //shuffles the original deck
        powerUpDeck.shuffleDeck();

        //checks that the list of drawable cards of the shuffled deck is different from the one of the not-shuffled one
        assertNotEquals(powerUpDeck.getDrawable(), oldPowerUpDeck.getDrawable());

    }


    /**
     * Tests the method regenerate() for a deck of ammo tiles.
     */
    @Test
    public void regenerateAmmoTile() throws WrongTimeException {

        //creates a new deck of ammo tiles and adds two cards to the deck as discarded cards
        Deck ammoTileDeck = new Deck();
        AmmoTile ammoTile1 = new AmmoTile(true, new AmmoPack(1,2,0));
        AmmoTile ammoTile2 = new AmmoTile(true, new AmmoPack(1,0,2));
        ammoTileDeck.getDiscarded().add(ammoTile1);
        ammoTileDeck.getDiscarded().add(ammoTile2);

        //checks that there are not drawable cards
        assertTrue(ammoTileDeck.getDrawable().isEmpty());

        //regenerates the deck
        ammoTileDeck.regenerate();

        //checks that there are not discarded cards
        assertTrue(ammoTileDeck.getDiscarded().isEmpty());

        //checks that the cards which were in the discarded pile are now drawable
        assertTrue(ammoTileDeck.getDrawable().contains(ammoTile1) && ammoTileDeck.getDrawable().contains(ammoTile2));
    }


    /**
     * Tests the method regenerate() for a deck of power ups.
     */
    @Test
    public void regeneratePowerUpRealDeck() throws WrongTimeException{

        //The deck contains 2 copies of all the combinations of color(3 possible colors) and type of power up
        int numberOfColor = 3;
        int numberOfCopy = 2;

        //creates the board with the decks.
        Board board1 = BoardConfigurer.getInstance().configureMap(1);
        BoardConfigurer.getInstance().configureDecks(board1);
        Deck powerUpDeck = board1.getPowerUpDeck();

        //checks that the deck contains all the power ups as drawable cards
        assertEquals(PowerUp.PowerUpName.values().length*numberOfColor*numberOfCopy, powerUpDeck.getDrawable().size());

        //discards all the cards of the deck
        powerUpDeck.getDiscarded().addAll(powerUpDeck.getDrawable());
        powerUpDeck.getDrawable().clear();

        //checks that there are not drawable cards
        assertTrue(powerUpDeck.getDrawable().isEmpty());

        //regenerates the deck
        powerUpDeck.regenerate();

        //checks that there are not discarded cards
        assertTrue(powerUpDeck.getDiscarded().isEmpty());

        //checks that all the cards are drawable
        assertEquals(PowerUp.PowerUpName.values().length*numberOfColor*numberOfCopy, powerUpDeck.getDrawable().size());
    }


    /**
     * Tests the method regenerate() for a deck of power up, when an exception should be thrown since the deck is not empty.
     */
    @Test(expected = WrongTimeException.class)
    public void regeneratePowerUpRealDeckNotEmpty() throws WrongTimeException{

        //The deck contains 2 copies of all the combinations of color(3 possible colors) and type of power up
        int numberOfColor = 3;
        int numberOfCopy = 2;

        //creates the board with the decks.
        Board board1 = BoardConfigurer.getInstance().configureMap(1);
        BoardConfigurer.getInstance().configureDecks(board1);
        Deck powerUpDeck = board1.getPowerUpDeck();

        //checks that the deck contains all the power ups as drawable cards
        assertEquals(PowerUp.PowerUpName.values().length*numberOfColor*numberOfCopy, powerUpDeck.getDrawable().size());

        //discards one card
        Card drawn = powerUpDeck.getDrawable().remove(0);
        powerUpDeck.getDiscarded().add(drawn);

        //checks that there still are 23 drawable cards
        assertEquals(23, powerUpDeck.getDrawable().size());

        //tries to regenerate the deck
        powerUpDeck.regenerate();

        //checks that there are still 23 discarded cards, no cards have been added
        assertEquals(23, powerUpDeck.getDrawable().size());

    }
}