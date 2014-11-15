/* 
 * The MIT License
 *
 * Copyright 2014 Bartosz Skrzypczak.
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
import com.github.barteks2x.cubit.location.Vec3D;
import com.github.barteks2x.cubit.world.CubitWorld;
import com.github.barteks2x.cubit.world.IWorld;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Player {

    private static final double PLAYER_SPEED = 0.1;//blocks/tick
    private static final boolean VERT_DIRECTION_MOVE = false;

    private EntityLocation location;
    private double rx, ry;
    private BlockLocation selectedBlock = null;
    private BlockLocation blockOnSelected = null;

    private double forward = 0;
    private double side = 0;
    private double up = 0;

    public int placeid;

    public <T extends IWorld> Player(T world) {
        this.location = new EntityLocation(world, 0, 0, 0);
        rx = 0;
        ry = 0;
    }

    public double getX() {
        return location.getX();
    }

    public double getY() {
        return location.getY();
    }

    public double getZ() {
        return location.getZ();
    }

    public void setPosition(EntityLocation pos) {
        this.location = new EntityLocation(pos);
    }

    public double getRx() {
        return rx;
    }

    public void setRx(double rx) {
        this.rx = rx;
    }

    public double getRy() {
        return ry;
    }

    public void setRy(double ry) {
        this.ry = ry;
    }

    public BlockLocation getSelectionLocation() {
        return selectedBlock;
    }

    public BlockLocation getPlaceBlockLocation() {
        return blockOnSelected;
    }

    public EntityLocation getLocation() {
        return this.location;
    }

    public void update() {

        while(Keyboard.next()) {
            boolean state = Keyboard.getEventKeyState();
            if(Keyboard.getEventKey() == Keyboard.KEY_W) {
                forward = state ? PLAYER_SPEED : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_S) {
                forward = state ? -PLAYER_SPEED : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_A) {
                side = state ? PLAYER_SPEED : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_D) {
                side = state ? -PLAYER_SPEED : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_LSHIFT) {
                up = state ? PLAYER_SPEED : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
                up = state ? -PLAYER_SPEED : 0;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE && Keyboard.
                    getEventKeyState()) {
                CubitMain.getGame().pauseInvert();
            }
            String c = String.valueOf(Keyboard.getEventCharacter());
            try {
                placeid = Integer.parseInt(c);
            } catch(NumberFormatException ignore) {
            }
        }

        double sinRX = Math.sin(Math.toRadians(rx));
        double cosRX = Math.cos(Math.toRadians(rx));
        double cosRY = Math.cos(Math.toRadians(ry));
        double sinRY = Math.sin(Math.toRadians(ry));

        double forwardX = forward * sinRX;
        double forwardZ = -forward * cosRX;
        double forwardY = 0;//may be non-0

        double sideX = -side * cosRX;
        double sideZ = -side * sinRX;
        double sideY = 0;//always 0

        double upX = 0;//always 0
        double upZ = 0;//always 0
        double upY = -up;

        if(VERT_DIRECTION_MOVE) {
            forwardX *= cosRY;
            forwardZ *= cosRY;

            forwardY -= forward * sinRY;
        }

        this.location = this.location.add(
                forwardX + sideX + upX,
                forwardY + sideY + upY,
                forwardZ + sideZ + upZ);

        double px = 0, py = 0, pz = 0;
        this.selectedBlock = null;
        this.blockOnSelected = null;
        //TODO: FIX REYTRACING!!!
        //FIXME!!!
        for(double i = 0; i <= 5; i += 0.001F) {
            int px_int_prev = (int)px - (px < 0 ? 1 : 0);
            int py_int_prev = (int)py - (py < 0 ? 1 : 0);
            int pz_int_prev = (int)pz - (pz < 0 ? 1 : 0);

            pz = this.getZ() + -i * cosRX * cosRY;
            py = this.getY() + -i * sinRY;
            px = this.getX() + i * sinRX * cosRY;

            int px_int = (int)px - (px < 0 ? 1 : 0);
            int py_int = (int)py - (py < 0 ? 1 : 0);
            int pz_int = (int)pz - (pz < 0 ? 1 : 0);

            Block b = this.getLocation().getWorld().getBlockAt(px_int, py_int,
                    pz_int);
            if(b != Block.AIR) {
                this.selectedBlock = new BlockLocation(this.getLocation().
                        getWorld(), px_int, py_int, pz_int);
                this.blockOnSelected = new BlockLocation(this.getLocation().
                        getWorld(), px_int_prev, py_int_prev, pz_int_prev);
                break;
            }
        }

        while(Mouse.next()) {
            if(!Mouse.isGrabbed()) {
                continue;
            }
            double mouseSensitivity = CubitMain.getGame().mouseSensitivity;
            rx += Mouse.getDX() * mouseSensitivity;
            rx %= 360;
            ry = Math.max(-90, Math.min(90, ry - Mouse.getDY() *
                    mouseSensitivity));
            if(!Mouse.getEventButtonState()) {
                continue;
            }
            BlockLocation blockPos = this.getSelectionLocation();
            if(Mouse.getEventButton() == 0 && blockPos != null) {
                this.getLocation().getWorld().setBlockAt(blockPos, Block.AIR);
                CubitMain.getGame().onBlockUpdate(blockPos);
            }
            blockPos = this.getPlaceBlockLocation();
            if(Mouse.getEventButton() == 1 && blockPos != null) {
                Mouse.setGrabbed(false);
                IWorld world = this.getLocation().getWorld();
                Block block = world.getBlockRegistry().fromID(placeid);
                if(block != null) {
                    world.setBlockAt(blockPos, block);
                    CubitMain.getGame().onBlockUpdate(blockPos);
                }
            }

        }

    }

    public Block getBlockToPlace() {
        return this.getLocation().getWorld().getBlockRegistry().fromID(this.placeid);
    }
}
