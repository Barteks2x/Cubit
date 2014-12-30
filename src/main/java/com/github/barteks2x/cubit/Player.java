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
import com.github.barteks2x.cubit.world.CubitWorld;
import com.github.barteks2x.cubit.world.World;
import java.util.Collection;
import java.util.Collections;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import toxi.geom.AABB;
import toxi.geom.Vec3D;

public class Player {

    private EntityLocation location;
    private double rx, ry;

    private final AABB aabb = AABB.fromMinMax(new Vec3D(-0.4F, 0.0F, -0.4F), new Vec3D(0.4F, 1.8F, 0.4F));

    private final PlayerController controller;

    public <T extends World> Player(T world) {
        this.location = new EntityLocation(world, 0, 0, 0);
        rx = 0;
        ry = 0;
        this.controller = new PlayerController();
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

    public void setLocation(EntityLocation pos) {
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

    public EntityLocation getLocation() {
        return this.location;
    }

    public void update() {
        Iterable<PlayerAction> actions = this.controller.update(this);
        
        for(PlayerAction action : actions) {
            action.performAction(this);
        }
    }

    public Collection<AABB> getCollisionBoundingBoxes() {
        return Collections.singleton(aabb);
    }

    public PlayerController getController() {
        return this.controller;
    }
}
