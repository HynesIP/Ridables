package net.pl3x.bukkit.ridables.entity;

import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.ControllerLook;
import net.minecraft.server.v1_13_R2.ControllerMove;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityEvoker;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.EnumDirection;
import net.minecraft.server.v1_13_R2.EnumHand;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.MathHelper;
import net.minecraft.server.v1_13_R2.VoxelShape;
import net.minecraft.server.v1_13_R2.World;
import net.pl3x.bukkit.ridables.configuration.Config;
import net.pl3x.bukkit.ridables.configuration.Lang;
import net.pl3x.bukkit.ridables.entity.controller.BlankLookController;
import net.pl3x.bukkit.ridables.entity.controller.ControllerWASD;
import net.pl3x.bukkit.ridables.entity.projectile.EntityCustomEvokerFangs;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.util.Vector;

public class EntityRidableEvoker extends EntityEvoker implements RidableEntity {
    private ControllerMove aiController;
    private ControllerWASD wasdController;
    private ControllerLook defaultLookController;
    private BlankLookController blankLookController;
    private EntityPlayer rider;
    private int spellCooldown = 0;

    public EntityRidableEvoker(World world) {
        super(world);
        aiController = moveController;
        wasdController = new ControllerWASD(this);
        defaultLookController = lookController;
        blankLookController = new BlankLookController(this);
    }

    public RidableType getType() {
        return RidableType.EVOKER;
    }

    // canBeRiddenInWater
    public boolean aY() {
        return true;
    }

    protected void mobTick() {
        if (spellCooldown > 0) {
            spellCooldown--;
        }

        EntityPlayer rider = updateRider();
        if (rider != null) {
            setGoalTarget(null, null, false);
            setRotation(rider.yaw, rider.pitch);
            useWASDController();
        }
        super.mobTick();
    }

    // getJumpUpwardsMotion
    protected float cG() {
        return super.cG() * getJumpPower() * 2.2F;
    }

    public void setRotation(float newYaw, float newPitch) {
        setYawPitch(lastYaw = yaw = newYaw, pitch = newPitch * 0.5F);
        aS = aQ = yaw;
    }

    public float getJumpPower() {
        return Config.EVOKER_JUMP_POWER;
    }

    public float getSpeed() {
        return (float) getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() * Config.EVOKER_SPEED * 0.5F;
    }

    public EntityPlayer getRider() {
        return rider;
    }

    public EntityPlayer updateRider() {
        if (passengers == null || passengers.isEmpty()) {
            rider = null;
        } else {
            Entity entity = passengers.get(0);
            rider = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
        }
        return rider;
    }

    public void useAIController() {
        if (moveController != aiController) {
            moveController = aiController;
            lookController = defaultLookController;
        }
    }

    public void useWASDController() {
        if (moveController != wasdController) {
            moveController = wasdController;
            lookController = blankLookController;
        }
    }

    public boolean onClick(org.bukkit.entity.Entity entity, EnumHand hand) {
        return handleClick(hand);
    }

    public boolean onClick(Block block, BlockFace blockFace, EnumHand hand) {
        return handleClick(hand);
    }

    public boolean onClick(EnumHand hand) {
        return handleClick(hand);
    }

    private boolean handleClick(EnumHand hand) {
        if (spellCooldown == 0) {
            EntityPlayer rider = getRider();
            if (rider != null) {
                return castSpell(rider, hand == EnumHand.OFF_HAND);
            }
        }
        return false;
    }

    /**
     * Cast fangs spell at target location
     *
     * @param rider  Rider of this evoker
     * @param circle True if fangs spawn in circle, false for straight line
     * @return True if spell was successfully cast
     */
    public boolean castSpell(EntityPlayer rider, boolean circle) {
        spellCooldown = Config.EVOKER_SPELL_COOLDOWN;

        if (rider == null) {
            return false;
        }

        CraftPlayer player = (CraftPlayer) ((Entity) rider).getBukkitEntity();
        if (!player.hasPermission("allow.special.evoker")) {
            Lang.send(player, Lang.SHOOT_NO_PERMISSION);
            return false;
        }

        Vector direction = player.getEyeLocation().getDirection().normalize().multiply(25);

        double y = locY + direction.getY();
        double minY = Math.min(y, locY);
        double maxY = Math.max(y, locY) + 1.0D;

        float distance = (float) MathHelper.c((locZ + direction.getZ()) - locZ, (locX + direction.getX()) - locX);
        if (circle) {
            for (int i = 0; i < 5; ++i) {
                float rotationYaw = distance + (float) i * (float) Math.PI * 0.4F;
                spawnFangs(rider, locX + (double) MathHelper.cos(rotationYaw) * 1.5D, locZ + (double) MathHelper.sin(rotationYaw) * 1.5D, minY, maxY, rotationYaw, 0);
            }
            for (int i = 0; i < 8; ++i) {
                float rotationYaw = distance + (float) i * (float) Math.PI * 2.0F / 8.0F + ((float) Math.PI * 2F / 5F);
                spawnFangs(rider, locX + (double) MathHelper.cos(rotationYaw) * 2.5D, locZ + (double) MathHelper.sin(rotationYaw) * 2.5D, minY, maxY, rotationYaw, 3);
            }
        } else {
            for (int i = 0; i < 16; ++i) {
                double d2 = 1.25D * (double) (i + 1);
                spawnFangs(rider, locX + (double) MathHelper.cos(distance) * d2, locZ + (double) MathHelper.sin(distance) * d2, minY, maxY, distance, i);
            }
        }

        return true;
    }

    private void spawnFangs(EntityPlayer rider, double x, double z, double minY, double maxY, float rotationYaw, int warmupDelayTicks) {
        BlockPosition pos = new BlockPosition(x, maxY, z);
        do {
            if (!world.q(pos) && world.q(pos.down())) { // !isTopSolid(pos) && isTopSolid(pos.down())
                double yOffset = 0.0D;
                if (!world.isEmpty(pos)) { // !world.isAirBlock
                    VoxelShape shape = world.getType(pos).h(world, pos); // blockState.getCollisionShape
                    if (!shape.b()) { // !isEmpty
                        yOffset = shape.c(EnumDirection.EnumAxis.Y); // shape.getEnd
                    }
                }
                EntityCustomEvokerFangs fangs = new EntityCustomEvokerFangs(world, x, (double) pos.getY() + yOffset, z, rotationYaw, warmupDelayTicks, this, rider);
                world.addEntity(fangs);
                break;
            }
            pos = pos.down();
        } while (pos.getY() >= MathHelper.floor(minY) - 1);
    }
}
