package it.polimi.ingsw.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Weapon;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class EncoderTest {

    @Test
    public void encodeTest() {

        WeaponFactory weaponFactory = new WeaponFactory(new Board());
        Weapon first = weaponFactory.createWeapon(Weapon.WeaponName.LOCK_RIFLE);
        Weapon second = weaponFactory.createWeapon(Weapon.WeaponName.FLAMETHROWER);
        Weapon third = weaponFactory.createWeapon(Weapon.WeaponName.TRACTOR_BEAM);
        Weapon fourth = weaponFactory.createWeapon(Weapon.WeaponName.GRENADE_LAUNCHER);
        List<Weapon> weapons = new ArrayList<>(Arrays.asList(first,second,third,fourth));

        JsonObject res = Encoder.encode("Choose a weapon", weapons);

        assertEquals("{\"head\":\"Choose a weapon\",\"options\":[\"Lock rifle\",\"Flamethrower\",\"Tractor beam\",\"Grenade launcher\"]}", res.toString() );
    }

    @Test
    public void toStringLIst() {
    }
}