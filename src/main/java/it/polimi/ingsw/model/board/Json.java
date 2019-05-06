package it.polimi.ingsw.model.board;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import it.polimi.ingsw.model.cards.FireMode;
import it.polimi.ingsw.model.cards.Weapon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Offers thr methods to read from a .json file the characteristics of a weapon and the keywords that map the weapon
 * into the algorithms that describes the mechanism of the weapon
 * the files is weaponsFile.json
 *
 * @author  davidealde
 */
public class Json {
    /**
     * Constructor of a json class
     */
    public Json() {
    }

    /**
     * Reads from the json file the color of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the color of the weapon
     */
    public String getColor(Weapon.WeaponName weaponName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray weapons = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : weapons) {

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if (name.equals(weaponName.toString())) {

                    return (String) weapon.get("color");

                }
            }


        }  catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the red full cost of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the red full cost of the weapon
     */
    public String getFullCostRed(Weapon.WeaponName weaponName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray weapons = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : weapons) {

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if (name.equals(weaponName.toString())) {

                    String cost;
                    cost= (String) weapon.get("costR");

                    return cost;
                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the blue full cost of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the blue full cost of the weapon
     */
    public String getFullCostBlue(Weapon.WeaponName weaponName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray weapons = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : weapons) {

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if (name.equals(weaponName.toString())) {

                    String cost;
                    cost= (String) weapon.get("costB");

                    return cost;
                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the yellow full cost of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the yellow full cost of the weapon
     */
    public String getFullCostYellow(Weapon.WeaponName weaponName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray weapons = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : weapons) {

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if (name.equals(weaponName.toString())) {

                    String cost;
                    cost= (String) weapon.get("costY");

                    return cost;
                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the list of fire modes of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @return                      the list of fire modes of the weapon
     */
    public String getNameList(Weapon.WeaponName weaponName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray weapons = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : weapons) {

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if (name.equals(weaponName.toString())) {

                    String type;
                    type= (String) weapon.get("type");

                    return type;
                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the number of the targets of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the number of the targets of the firemode of the weapon
     */
    public String getTargetNumber(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String number;
                            number = (String) mode.get("targetsN");
                            return number;
                        }

                    }


                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }



        return null;
    }

    /**
     * Reads from the json file the red cost of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the red cost of the firemode of the weapon
     */
    public String getFireModeCostRed(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String costR;
                            costR = (String) mode.get("costR");
                            return costR;
                        }

                    }


                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }



        return null;
    }

    /**
     * Reads from the json file the blue cost of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the blue cost of the firemode of the weapon
     */
    public String getFireModeCostBlue(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String costB;
                            costB = (String) mode.get("costB");
                            return costB;
                        }

                    }


                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }



        return null;
    }

    /**
     * Reads from the json file the yellow cost of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the yellow cost of the firemode of the weapon
     */
    public String getFireModeCostYellow(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String costY;
                            costY = (String) mode.get("costY");
                            return costY;
                        }

                    }


                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }



        return null;
    }

    /**
     * Reads from the json file the number of moves permitted of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the number of moves permitted of the firemode of the weapon
     */
    public String getMove(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String move;
                            move = (String) mode.get("move");
                            return move;
                        }

                    }


                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }



        return null;
    }

    /**
     * Reads from the json file the damages of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the damages of the firemode of the weapon
     */
    public String getDmg(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String dmg;
                            dmg = (String) mode.get("dmg");
                            return dmg;
                        }

                    }


                }
            }

        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the other amount of damages of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the other amount of damages of the firemode of the weapon
     */
    public String getDmg2(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String dmg2;
                            dmg2 = (String) mode.get("dmg2");
                            return dmg2;
                        }

                    }


                }
            }

        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the marks of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the marks of the firemode of the weapon
     */
    public String getMark(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String mark;
                            mark = (String) mode.get("mark");
                            return mark;
                        }

                    }


                }
            }

        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the steps of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the steps of the firemode of the weapon
     */
    public String getSteps(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String out;
                            out = (String) mode.get("steps");
                            return out;
                        }

                    }


                }
            }

        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the effect of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the effect of the firemode of the weapon
     */
    public String getEff(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String eff;
                            eff = (String) mode.get("effect");
                            return eff;
                        }

                    }


                }
            }


        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }



        return null;
    }

    /**
     * Reads from the json file the type of targets of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the type of targets of the firemode of the weapon
     */
    public String getWhere(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String out;
                            out = (String) mode.get("where");
                            return out;
                        }

                    }


                }
            }

        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

    /**
     * Reads from the json file the type of move of the firemode of interest of the weapon of interest
     *
     * @param weaponName            the name of the weapon
     * @param fireModeName          the firemode name
     * @return                      the type of move of the firemode of the weapon
     */
    public String getMoveType(Weapon.WeaponName weaponName, FireMode.FireModeName fireModeName) {

        JSONParser parser = new JSONParser();

        try {
            JSONArray a = (JSONArray) parser.parse(new FileReader("weaponsFile.json"));

            for (Object o : a){

                JSONObject weapon = (JSONObject) o;

                String name = (String) weapon.get("name");

                if(name.equals(weaponName.toString())){


                    JSONArray modesList = (JSONArray) weapon.get("modes");


                    for (Object k : modesList) {

                        JSONObject mode = (JSONObject) k;
                        String modeName = (String) mode.get("mode");

                        if(modeName.equals(fireModeName.toString())){

                            String out;
                            out = (String) mode.get("moveType");
                            return out;
                        }

                    }


                }
            }

        } catch (FileNotFoundException e) {
            return "FileNotFoundException";
        } catch (IOException e) {
            return "IOException";
        } catch (ParseException e) {
            return "ParseException";
        }

        return null;
    }

}
