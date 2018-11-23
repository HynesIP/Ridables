package net.pl3x.bukkit.ridables.entity.projectile;

import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.Blocks;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityFallingBlock;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.EnumMoveType;
import net.minecraft.server.v1_13_R2.IBlockData;
import net.minecraft.server.v1_13_R2.MathHelper;
import net.minecraft.server.v1_13_R2.World;
import net.pl3x.bukkit.ridables.entity.RidableEntity;
import org.bukkit.craftbukkit.v1_13_R2.event.CraftEventFactory;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class SpiderWeb extends EntityFallingBlock implements CustomProjectile {
    public static final IBlockData WEB = Blocks.COBWEB.getBlockData();

    private RidableEntity ridable;
    private EntityPlayer rider;

    public SpiderWeb(World world) {
        super(world);
    }

    public SpiderWeb(World world, RidableEntity ridable, EntityPlayer rider, double x, double y, double z) {
        super(world, x, y, z, WEB);
        this.ridable = ridable;
        this.rider = rider;
    }

    @Override
    public RidableEntity getRidable() {
        return ridable;
    }

    @Override
    public Mob getMob() {
        return ((EntityInsentient) ridable).getBukkitMob();
    }

    @Override
    public Player getRider() {
        return rider == null ? null : rider.getBukkitEntity();
    }

    @Override
    public void tick() {
        if (dead) {
            return;
        }

        if (ticksLived++ > 100) {
            die();
            return;
        }

        lastX = locX;
        lastY = locY;
        lastZ = locZ;

        if (!isNoGravity()) {
            motY -= (double) 0.04F;
        }

        move(EnumMoveType.SELF, motX, motY, motZ);

        if (!onGround) {
            if (D) { // collided
                trySetAsBlock(new BlockPosition(this).up());
            } else {
                for (Entity entity : world.getEntities(this, getBoundingBox())) {
                    if (entity != ridable && entity != rider) {
                        motX = motY = motZ = 0;
                        trySetAsBlock(entity);
                        return;
                    }
                }
            }
        } else {
            trySetAsBlock(new BlockPosition(this));
        }

        motX *= (double) 0.98F;
        motY *= (double) 0.98F;
        motZ *= (double) 0.98F;
    }

    // fall
    @Override
    public void c(float distance, float damageMultiplier) {
        // do nothing
    }

    private void trySetAsBlock(BlockPosition pos) {
        IBlockData state = world.getType(pos);
        if (state.getBlock() != Blocks.MOVING_PISTON) {
            die();
            if (state.getMaterial().isReplaceable() && !CraftEventFactory.callEntityChangeBlockEvent(this, pos, WEB).isCancelled()) {
                world.setTypeAndData(pos, WEB, 3);
            }
        }
    }

    private void trySetAsBlock(Entity entity) {
        BlockPosition pos = new BlockPosition(entity);
        trySetAsBlock(pos);
        trySetAsBlock(pos.up());
        entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public void shoot(double x, double y, double z, double speed) {
        double distance = (double) MathHelper.sqrt(x * x + y * y + z * z);
        motX = x / distance * speed;
        motY = y / distance * speed;
        motZ = z / distance * speed;
    }
}
