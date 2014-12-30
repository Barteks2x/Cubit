package com.github.barteks2x.cubit;

import com.github.barteks2x.cubit.location.EntityLocation;
import com.github.barteks2x.cubit.location.Vec2D;
import com.github.barteks2x.cubit.location.Vec3D;
import org.lwjgl.input.Mouse;

public class PlayerMoveAction implements PlayerAction {

    private static final boolean VERT_DIRECTION_MOVE = false;

    private final double forward;
    private final double right;
    private final double up;

    private final double rotX;
    private final double rotY;

    /**
     * Contructs new Player Move Action.
     * <p>
     * X coordinate of dMove is forward movement.
     * <p>
     * Y coordinate of dMove is righe movement.
     * <p>
     * Z coordinate of dMove is up movement.
     * <p>
     * @param dRot  player rotation change.
     * @param dMove Player position change.
     */
    public PlayerMoveAction(Vec2D dRot, Vec3D dMove) {
        this.forward = dMove.getX();
        this.right = dMove.getY();
        this.up = dMove.getZ();

        this.rotX = dRot.getX();
        this.rotY = dRot.getY();
    }

    @Override
    public boolean performAction(Player player) {
        double rx = player.getRx() + this.rotX;
        double ry = player.getRy() + this.rotY;
        rx %= 360;
        ry = Math.max(-90, Math.min(90, ry));

        double sinRX = Math.sin(Math.toRadians(rx));
        double cosRX = Math.cos(Math.toRadians(rx));
        double cosRY = Math.cos(Math.toRadians(ry));
        double sinRY = Math.sin(Math.toRadians(ry));

        double forwardX = forward * sinRX;
        double forwardZ = -forward * cosRX;
        double forwardY = 0;//may be non-0 (VERT_DIRECTION_MOVE)

        double sideX = -right * cosRX;
        double sideZ = -right * sinRX;
        double sideY = 0;//always 0 unless nonzero roll

        double upX = 0;//always 0
        double upZ = 0;//always 0
        double upY = -up;

        if(VERT_DIRECTION_MOVE) {
            forwardX *= cosRY;
            forwardZ *= cosRY;

            forwardY -= forward * sinRY;
        }

        EntityLocation location = player.getLocation().add(
                forwardX + sideX + upX,
                forwardY + sideY + upY,
                forwardZ + sideZ + upZ);

        player.setLocation(location);
        player.setRx(rx);
        player.setRy(ry);
        return true;
    }
}
