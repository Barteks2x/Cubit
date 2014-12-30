package com.github.barteks2x.cubit;

public class PlayerPauseAction implements PlayerAction {

    @Override
    public boolean performAction(Player player) {
        CubitMain.getGame().pauseInvert();
        return true;
    }
}
