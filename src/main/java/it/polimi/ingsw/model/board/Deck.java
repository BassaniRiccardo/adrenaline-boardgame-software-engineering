package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.WrongTimeException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.shuffle;

/**
 * Represents a generic deck of cards, made of drawable and discarded cards.
 * Allows to addList cards to the drawable cards and to the discarded cards.
 * Allows to draw a card, shuffle the deck or regenerate it by shuffling
 * the discarded cards and adding them to the drawable cards.
 *
 * @author  BassaniRiccardo
 */

public class Deck {

    private List<Card> drawable;
    private List<Card> discarded;

    private static final int DRAWN_CARD_INDEX = 0;


    /**
     * Constructs an empty deck.
     *
     */
    public Deck() {

        drawable = new ArrayList<>();
        discarded = new ArrayList<>();

    }


    /**
     * Getter for drawable.
     *
     * @return      the drawable cards of the deck.
     */
    public List<Card> getDrawable() {
        return drawable;
    }

    /**
     * Getter for discarded.
     *
     * @return      the discarded cards of the deck.
     */
    public List<Card> getDiscarded() {
        return discarded;
    }


    /**
     * Adds a card to the deck as a drawable card
     *
     * @param card      the card to addList.
     */
    public void addCard(Card card){
        drawable.add(card);
    }


    /**
     * Adds a card to the deck as a discarded card
     *
     * @param card      the card to addList.
     */
    public void addDiscardedCard(Card card){
        discarded.add(card);
    }


    /**
     * Draws a card form the deck.
     *
     * @return          the drawn card.
     * @throws  NoMoreCardsException    if there are no drawable cards.
     */
    public Card drawCard() throws NoMoreCardsException {

        try{
            Card drawn = drawable.get(DRAWN_CARD_INDEX);
            drawable.remove(DRAWN_CARD_INDEX);
            return drawn;
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new NoMoreCardsException("The deck is empty");
        }

    }


    /**
     * Shuffles the deck.
     */
    public void shuffleDeck(){
        shuffle(drawable);
    }


    /**
     * Regenerates the deck, by shuffling the discarded cards and adding them to the drawable cards.
     *
     * @throws WrongTimeException if there are still drawable cards.
     */
    public void regenerate() throws WrongTimeException {

        if (!drawable.isEmpty()) throw new WrongTimeException("The deck can be regenerated only if empty.");
        shuffle(discarded);
        drawable =  new ArrayList<>(discarded);
        discarded.clear();

    }

}