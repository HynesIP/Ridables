package net.pl3x.bukkit.ridables.entity.ai.slime;

import com.destroystokyo.paper.event.entity.SlimeTargetLivingEntityEvent;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.PathfinderGoal;
import net.pl3x.bukkit.ridables.entity.RidableSlime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

public class AISlimeAttack extends PathfinderGoal {
    private final RidableSlime slime;
    private int timer;

    public AISlimeAttack(RidableSlime slime) {
        this.slime = slime;
        a(2); // setMutexBits
    }

    // shouldExecute
    public boolean a() {
        if (slime.getRider() != null) {
            return false;
        }
        EntityLiving target = slime.getGoalTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (target instanceof EntityHuman && ((EntityHuman) target).abilities.isInvulnerable) {
            return false;
        }
        return slime.canWander() && new SlimeTargetLivingEntityEvent((Slime) slime.getBukkitEntity(), (LivingEntity) target.getBukkitEntity()).callEvent();
    }

    // shouldContinueExecuting
    public boolean b() {
        if (slime.getRider() != null) {
            return false;
        }
        EntityLiving target = slime.getGoalTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (target instanceof EntityHuman && ((EntityHuman) target).abilities.isInvulnerable) {
            return false;
        }
        if (--timer <= 0) {
            return false;
        }
        return slime.canWander() && new SlimeTargetLivingEntityEvent((Slime) slime.getBukkitEntity(), (LivingEntity) target.getBukkitEntity()).callEvent();
    }

    // startExecuting
    public void c() {
        timer = 300;
        super.c();
    }

    // resetTask
    public void d() {
        timer = 0;
        slime.setGoalTarget(null);
    }

    // tick
    public void e() {
        slime.a(slime.getGoalTarget(), 10.0F, 10.0F);
        ((RidableSlime.SlimeWASDController) slime.getControllerMove()).setDirection(slime.yaw, slime.canDamagePlayer());
    }
}
