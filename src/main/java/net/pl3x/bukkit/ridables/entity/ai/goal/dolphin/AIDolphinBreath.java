package net.pl3x.bukkit.ridables.entity.ai.goal.dolphin;

import net.minecraft.server.v1_13_R2.EntityCreature;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.PathfinderGoalBreath;
import net.pl3x.bukkit.ridables.entity.RidableEntity;
import net.pl3x.bukkit.ridables.entity.ai.controller.ControllerWASD;

public class AIDolphinBreath extends PathfinderGoalBreath {
    private final RidableEntity ridable;
    private final ControllerWASD controller;

    public AIDolphinBreath(RidableEntity ridable) {
        super((EntityCreature) ridable);
        this.ridable = ridable;
        this.controller = (ControllerWASD) ((EntityInsentient) ridable).getControllerMove();
    }

    // shouldExecute
    @Override
    public boolean a() {
        if (ridable.getRider() != null) {
            return controller.override = super.a();
        }
        return super.a();
    }

    // shouldContinueExecuting
    @Override
    public boolean b() {
        return a();
    }
}
