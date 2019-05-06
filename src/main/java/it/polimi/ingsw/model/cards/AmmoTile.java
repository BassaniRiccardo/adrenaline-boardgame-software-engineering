package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Player;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;

/**
 * Represents an ammo tile, containing an ammo pack, and
 * shows if a power up can be drawn since displayed on the ammo tile.
 *
 * @author  davidealde
 */
public class AmmoTile implements Card{

    private final boolean powerUp;
    private final AmmoPack ammoPack;

    /**
     * Constructs an ammo tile, with an ammo pack and the information about the possibility to draw a power up.
     *
     * @param powerUp       whether a power up can be drawn.
     * @param ammoPack      the ammo pack.
     */
    public AmmoTile(boolean powerUp, AmmoPack ammoPack) {

        this.powerUp = powerUp;
        this.ammoPack = ammoPack;

    }


    /**
     * Getters
     */

    public boolean hasPowerUp() {
        return powerUp;
    }

    public AmmoPack getAmmoPack() {
        return ammoPack;
    }

    public Player getHolder() throws NotAvailableAttributeException { throw new NotAvailableAttributeException("An ammo tile does not have an holder"); }

    public Color getColor() throws NotAvailableAttributeException { throw new NotAvailableAttributeException("An ammo tile does not have a color"); }

    public void setHolder(Player holder) throws NotAvailableAttributeException { throw new NotAvailableAttributeException("An ammo tile does not have an holder"); }

}
