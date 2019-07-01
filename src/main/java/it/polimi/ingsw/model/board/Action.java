package it.polimi.ingsw.model.board;

/**
 * Represents the actions of the players.
 * Every action is composed by some moves that can be 1 or more steps, and the possibility to collect, shoot or reload.
 * Every player has 2 or 3 actions available at the same time, depending on his status.
 * Contains an override of equals.
 *
 * @author  davidealde
 */

public final class Action {

    private int steps;
    private boolean collect;
    private boolean shoot;
    private boolean reload;
    private static final String MOVE_UP_TO = "Move up to ";
    private static final String SQUARES = " squares.";
    private static final String COLLECT_TAG = "Collect.";
    private static final String RELOAD_TAG = "Reload.";
    private static final String SHOOT_TAG = "Shoot.";




    /**
     * Constructor
     *
     * @param steps   maximum number of possible steps
     * @param collect possibility to collect
     * @param shoot   possibility to shoot
     * @param reload  possibility to reload
     */
    public Action(int steps, boolean collect, boolean shoot, boolean reload) {
        this.steps = steps;
        this.collect = collect;
        this.shoot = shoot;
        this.reload = reload;
    }


    /**
     * Getter for steps
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Getter for collect
     */
    public boolean isCollect() {
        return collect;
    }

    /**
     * Getter for shoot
     */
    public boolean isShoot() {
        return shoot;
    }

    /**
     * Getter for reload
     */
    public boolean isReload() {
        return reload;
    }


    /**
     * Override of equals(Object) to be able to confront two equal Actions.
     *
     * @param o     the object to compare the action with.
     */
    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Action)) {
            return false;
        }

        Action a = (Action) o;

        return a.getSteps() == getSteps() &&
                a.isCollect() == isCollect() &&
                a.isShoot() == isShoot() &&
                a.isReload() == isReload();

    }


    /**
     * Override of hashcode(Object).
     */
    @Override
    public int hashCode() {

        int result = 0;

        result += 3 * steps;
        if (collect) result += 249;
        if (shoot) result += 134;
        if (reload) result += 795;

        return result;

    }


    /**
     * Returns a string representing the action.
     *
     * @return      a string representing an action.
     */
    @Override
    public String toString(){

        StringBuilder builder = new StringBuilder();

        if (steps > 0){
            if (!builder.toString().isEmpty())  builder.append(" ");
            builder.append(MOVE_UP_TO);
            builder.append(steps);
            builder.append(SQUARES);
        }
        if (collect){
            if (!builder.toString().isEmpty())  builder.append(" ");
            builder.append(COLLECT_TAG);
        }
        if (reload){
            if (!builder.toString().isEmpty())  builder.append(" ");
            builder.append(RELOAD_TAG);
        }
        if (shoot){
            if (!builder.toString().isEmpty())  builder.append(" ");
            builder.append(SHOOT_TAG);
        }

        return builder.toString();

    }

}