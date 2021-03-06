package net.pl3x.bukkit.ridables.entity;

import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityHorseZombie;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.EnumHand;
import net.minecraft.server.v1_13_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_13_R2.World;
import net.pl3x.bukkit.ridables.configuration.Config;
import net.pl3x.bukkit.ridables.util.ItemUtil;

public class RidableZombieHorse extends EntityHorseZombie implements RidableEntity {
    private EntityPlayer rider;

    public RidableZombieHorse(World world) {
        super(world);
    }

    public RidableType getType() {
        return RidableType.ZOMBIE_HORSE;
    }

    // canBeRiddenInWater
    public boolean aY() {
        return Config.ZOMBIE_HORSE_RIDABLE_IN_WATER;
    }

    public void mobTick() {
        Q = Config.ZOMBIE_HORSE_STEP_HEIGHT;
        updateRider();
        super.mobTick();
    }

    // travel
    public void a(float strafe, float vertical, float forward) {
        super.a(strafe, vertical, forward);
        if (getRider() != null) {
            checkMove();
        }
    }

    public void setRotation(float newYaw, float newPitch) {
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
    }

    public void useWASDController() {
    }

    // processInteract
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        if (passengers.isEmpty() && !entityhuman.isPassenger() && !entityhuman.isSneaking() && ItemUtil.isEmptyOrSaddle(entityhuman)) {
            return enumhand == EnumHand.MAIN_HAND && tryRide(entityhuman);
        }
        return passengers.isEmpty() && super.a(entityhuman, enumhand);
    }

    public boolean isTamed() {
        return true;
    }

    // addSwimmingPathfinder
    protected void dI() {
        goalSelector.a(0, new PathfinderGoalFloat(this));
    }
}
