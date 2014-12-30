package com.github.barteks2x.cubit;

import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.EntityLocation;
import com.github.barteks2x.cubit.location.Vec2D;
import com.github.barteks2x.cubit.location.Vec3D;

public class PlayerSetBlockAction implements PlayerAction {
    private final BlockLocation blockLoc;
    private final Block type;

    /**
     * Construucts new Set Block Action.
     * @param blockLoc block change location
     * @param type new block type
     */
    public PlayerSetBlockAction(BlockLocation blockLoc, Block type) {
        this.blockLoc = blockLoc;
        this.type = type;

    }

    @Override
    public boolean performAction(Player player) {
        return player.getLocation().getWorld().setBlockAt(blockLoc, type);
    }
}
