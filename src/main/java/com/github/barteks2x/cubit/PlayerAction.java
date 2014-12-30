package com.github.barteks2x.cubit;

/**
 * Represents single player action, like placina a block or movement.
 */
public interface PlayerAction {

    /**
     *
     * @param player Player on which the action should be performed
     * @return if the action was successful or not
     */
    public boolean performAction(Player player);
}
