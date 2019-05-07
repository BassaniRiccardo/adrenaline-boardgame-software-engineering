package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.BoardConfigurer;
import it.polimi.ingsw.controller.WeaponFactory;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.FireMode;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.exceptions.NoMoreCardsException;
import it.polimi.ingsw.model.exceptions.NotAvailableAttributeException;
import it.polimi.ingsw.model.exceptions.UnacceptableItemNumberException;
import org.junit.Test;
import static org.junit.Assert.*;

//This class needs more in-depth testing once it has been fully implemented: only the first weapon has been tested so far
public class WeaponFactoryTest {

    /**
     * Creates the first weapon and checks that it is initialized correctly
     */
    @Test
    public void createWeapon() throws NoMoreCardsException, UnacceptableItemNumberException {
        Board b = BoardConfigurer.getInstance().simulateScenario();
        WeaponFactory weaponFactory = new WeaponFactory(b);
        Weapon w = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        assertTrue(w.getWeaponName() == Weapon.WeaponName.LOCK_RIFLE);

        assertTrue(w.getFullCost().getRedAmmo()==0);
        assertTrue(w.getFullCost().getBlueAmmo()==2);
        assertTrue(w.getFullCost().getYellowAmmo()==0);
        assertFalse(w.getFireModeList().isEmpty());
        assertTrue(w.getFireModeList().size()==2);
        assertTrue(w.getReducedCost().getRedAmmo()==0);
        assertTrue(w.getReducedCost().getBlueAmmo()==1);
        assertTrue(w.getReducedCost().getYellowAmmo()==0);
        try {
            w.getHolder();
        }catch (NotAvailableAttributeException e){}
        assertFalse(w.isLoaded());
        assertTrue(w.getMainTargets().isEmpty());
        assertTrue(w.getOptionalTargets().isEmpty());
        try{
            w.getHolder();
        }catch (NotAvailableAttributeException e){}
        FireMode f = w.getFireModeList().get(0);
        assertTrue(f.getName() == FireMode.FireModeName.MAIN);
        assertTrue(f.getMaxTargets()==1);
        assertTrue(f.getCost().getRedAmmo() == 0);
        assertTrue(f.getCost().getBlueAmmo() == 0);
        assertTrue(f.getCost().getYellowAmmo() == 0);
        assertTrue(f.getDestinationFinder()!=null&&f.getTargetFinder()!=null&&f.getEffect()!=null);

        f = w.getFireModeList().get(1);
        assertTrue(f.getName() == FireMode.FireModeName.OPTION1);
        assertTrue(f.getMaxTargets()== 1);
        assertTrue(f.getCost().getRedAmmo() == 1);
        assertTrue(f.getCost().getBlueAmmo() == 0);
        assertTrue(f.getCost().getYellowAmmo() == 0);
        assertTrue(f.getDestinationFinder()!=null&&f.getTargetFinder()!=null&&f.getEffect()!=null);
    }
}