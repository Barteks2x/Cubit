/* 
 * The MIT License
 *
 * Copyright (C) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.barteks2x.cubit;

import com.github.barteks2x.cubit.block.Block;
import com.github.barteks2x.cubit.location.BlockLocation;
import com.github.barteks2x.cubit.location.EntityLocation;
import com.github.barteks2x.cubit.location.Vec2D;
import com.github.barteks2x.cubit.location.Vec3D;
import com.github.barteks2x.cubit.world.World;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class PlayerController {

    private static final double PLAYER_SPEED = 0.4;//blocks/tick

    private double forward, side, up;
    private double rx, ry;

    private boolean setPause = false;

    private Block blockTypeSet;

    private BlockLocation blockLocationSet;
    private BlockLocation placeBlockLocation;
    private BlockLocation selectionLocation;

    /**
     *
     * @param player the player to update
     * <p>
     * @return Intterable of actions to perform
     */
    public Iterable<PlayerAction> update(Player player) {
        rx = 0;
        ry = 0;

        setPause = false;
        placeBlockLocation = null;
        selectionLocation = null;

        mouse(player);
        keyboard(player);

        List<PlayerAction> actions = new LinkedList<PlayerAction>();

        actions.add(new PlayerMoveAction(new Vec2D(rx, ry), new Vec3D(forward, side, up)));
        if(blockLocationSet != null && blockTypeSet != null) {
            actions.add(new PlayerSetBlockAction(blockLocationSet, blockTypeSet));
        }
        if(setPause) {
            actions.add(new PlayerPauseAction());
        }
        return actions;
    }

    private void keyboard(Player player) {

        while(Keyboard.next()) {
            boolean state = Keyboard.getEventKeyState();
            if(Keyboard.getEventKey() == Keyboard.KEY_W) {
                forward = state ? PLAYER_SPEED : 0;
                continue;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_S) {
                forward = state ? -PLAYER_SPEED : 0;
                continue;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_A) {
                side = state ? PLAYER_SPEED : 0;
                continue;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_D) {
                side = state ? -PLAYER_SPEED : 0;
                continue;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_LSHIFT) {
                up = state ? PLAYER_SPEED : 0;
                continue;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
                up = state ? -PLAYER_SPEED : 0;
                continue;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE && state) {
                setPause = true;
                continue;
            }
            char c = Keyboard.getEventCharacter();
            //check ASCII range
            if(c >= '0' && c <= '9') {
                int placeId = c - '0';
                this.blockTypeSet = player.getLocation().getWorld().getBlockRegistry().fromID(placeId);
                //continue;
            }
        }
    }

    private void mouse(Player player) {
        World world = player.getLocation().getWorld();
        boolean placeBlock = false, breakBlock = false;
        
        while(Mouse.next()) {
            if(!Mouse.isGrabbed()) {
                break;
            }
            double mouseSensitivity = CubitMain.getGame().mouseSensitivity;
            this.rx += Mouse.getDX() * mouseSensitivity;
            this.ry -= Mouse.getDY()*mouseSensitivity;
            if(!Mouse.getEventButtonState()) {
                continue;
            }
            if(Mouse.getEventButton() == 0) {
                breakBlock = true;
            }
            if(Mouse.getEventButton() == 1) {
                placeBlock = true;
            }
            int dWheel = Mouse.getDWheel();
            if(dWheel != 0) {
                int placeId = world.getBlockRegistry().getID(blockTypeSet);
                if(dWheel < 0) {
                    placeId--;
                } else {
                    placeId++;
                }
                blockTypeSet = world.getBlockRegistry().fromID(placeId);
            }
        }

        double sinRX = Math.sin(Math.toRadians(this.rx + player.getRx()));
        double cosRX = Math.cos(Math.toRadians(this.rx + player.getRx()));
        double cosRY = Math.cos(Math.toRadians(this.ry + player.getRy()));
        double sinRY = Math.sin(Math.toRadians(this.ry + player.getRy()));

        //TODO: FIX REYTRACING!!!
        //FIXME!!!
        double px = 0, py = 0, pz = 0;
        for(double i = 0; i <= 5; i += 0.001F) {
            int pxIntPrev = (int)px - (px < 0 ? 1 : 0);
            int pyIntPrev = (int)py - (py < 0 ? 1 : 0);
            int pzIntPrev = (int)pz - (pz < 0 ? 1 : 0);

            pz = player.getZ() + -i * cosRX * cosRY;
            py = player.getY() + -i * sinRY;
            px = player.getX() + i * sinRX * cosRY;

            int pxInt = (int)px - (px < 0 ? 1 : 0);
            int pyInt = (int)py - (py < 0 ? 1 : 0);
            int pzInt = (int)pz - (pz < 0 ? 1 : 0);

            Block b = world.getBlockAt(pxInt, pyInt, pzInt);
            if(b != Block.AIR) {
                selectionLocation = new BlockLocation(world, pxInt, pyInt, pzInt);
                placeBlockLocation = new BlockLocation(world, pxIntPrev, pyIntPrev, pzIntPrev);
                break;
            }
        }
        if(breakBlock && selectionLocation != null) {
            this.blockLocationSet = selectionLocation;
            this.blockTypeSet = Block.AIR;
        }
        if(placeBlock && placeBlockLocation != null) {
            this.blockLocationSet = placeBlockLocation;
        }

    }

    public BlockLocation getSelectionLocation() {
        return this.selectionLocation;
    }

    public BlockLocation getPlaceBlockLocation() {
        return this.placeBlockLocation;
    }

    public Block getBlockToPlace() {
        return this.blockTypeSet;
    }
}
