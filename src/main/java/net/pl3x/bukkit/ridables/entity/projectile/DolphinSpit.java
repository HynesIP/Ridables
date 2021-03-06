package net.pl3x.bukkit.ridables.entity.projectile;

import net.minecraft.server.v1_13_R2.AxisAlignedBB;
import net.minecraft.server.v1_13_R2.DamageSource;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.EntityLlamaSpit;
import net.minecraft.server.v1_13_R2.IProjectile;
import net.minecraft.server.v1_13_R2.Material;
import net.minecraft.server.v1_13_R2.MathHelper;
import net.minecraft.server.v1_13_R2.MovingObjectPosition;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.Particles;
import net.minecraft.server.v1_13_R2.Vec3D;
import net.minecraft.server.v1_13_R2.World;
import net.minecraft.server.v1_13_R2.WorldServer;
import net.pl3x.bukkit.ridables.configuration.Config;
import net.pl3x.bukkit.ridables.entity.RidableDolphin;

import java.util.UUID;

public class DolphinSpit extends EntityLlamaSpit implements IProjectile {
    private RidableDolphin dolphin;
    private EntityHuman rider;
    private NBTTagCompound nbt;
    private int life;

    public DolphinSpit(World world) {
        super(world);
        setSize(0.25F, 0.25F);
    }

    public DolphinSpit(World world, RidableDolphin dolphin, EntityHuman rider) {
        this(world);
        this.dolphin = dolphin;
        this.rider = rider;
        setPosition(dolphin.locX - (double) (dolphin.width + 1.0F) * 0.5D * (double) MathHelper.sin(dolphin.aQ * 0.017453292F),
                dolphin.locY + (double) dolphin.getHeadHeight() - 0.5000000149011612D,
                dolphin.locZ + (double) (dolphin.width + 1.0F) * 0.5D * (double) MathHelper.cos(dolphin.aQ * 0.017453292F));
    }

    public void tick() {
        if (nbt != null) {
            restoreOwnerFromSave();
        }

        detectCollisions();

        updatePosition();

        for (int i = 0; i < 5; i++) {
            ((WorldServer) world).sendParticles(null, Particles.e,
                    locX + random.nextFloat() / 2 - 0.25F,
                    locY + random.nextFloat() / 2 - 0.25F,
                    locZ + random.nextFloat() / 2 - 0.25F,
                    1, 0, 0, 0, 0, true);
        }

        if (!(world.a(getBoundingBox(), Material.AIR) || world.a(getBoundingBox(), Material.WATER))) {
            die(); // die if not in air or water
        }
        if (++life > 15) {
            die();
        }
    }

    private void updatePosition() {
        locX += motX;
        locY += motY;
        locZ += motZ;
        yaw = (float) (MathHelper.c(motX, motZ) * 57.2957763671875D);
        pitch = (float) (MathHelper.c(motY, (double) MathHelper.sqrt(motX * motX + motZ * motZ)) * 57.2957763671875D);
        while (pitch - lastPitch < -180.0F)
            lastPitch -= 360.0F;
        while (pitch - lastPitch >= 180.0F)
            lastPitch += 360.0F;
        while (yaw - lastYaw < -180.0F)
            lastYaw -= 360.0F;
        while (yaw - lastYaw >= 180.0F)
            lastYaw += 360.0F;
        pitch = lastPitch + (pitch - lastPitch) * 0.2F;
        yaw = lastYaw + (yaw - lastYaw) * 0.2F;
        if (!world.a(getBoundingBox(), Material.WATER)) {
            motX *= 0.9900000095367432D;
            motY *= 0.9900000095367432D;
            motZ *= 0.9900000095367432D;
        }
        if (!isNoGravity()) {
            motY -= 0.02999999865889549D;
        }
        setPosition(locX, locY, locZ);
    }

    private void detectCollisions() {
        double reach = 10.0;

        Vec3D minVec = new Vec3D(locX, locY, locZ);
        Vec3D maxVec = new Vec3D(locX + motX * reach, locY + motY * reach, locZ + motZ * reach);

        MovingObjectPosition rayTraceResult = world.rayTrace(minVec, maxVec);

        minVec = new Vec3D(locX, locY, locZ);
        maxVec = new Vec3D(locX + motX * reach, locY + motY * reach, locZ + motZ * reach);

        if (rayTraceResult != null) {
            maxVec = new Vec3D(rayTraceResult.pos.x, rayTraceResult.pos.y, rayTraceResult.pos.z);
        }

        EntityLiving hitEntity = getHitEntity(minVec, maxVec);
        if (hitEntity != null && rider != null) {
            if (Config.DOLPHIN_SHOOT_DAMAGE > 0) {
                hitEntity.damageEntity(DamageSource.a(this, rider).c(), Config.DOLPHIN_SHOOT_DAMAGE);
            }
            die();
        }
    }

    private EntityLiving getHitEntity(Vec3D vec3d, Vec3D vec3d1) {
        EntityLiving entity = null;
        double d0 = 0.0D;
        for (Entity entity1 : world.getEntities(this, getBoundingBox().b(motX, motY, motZ).g(1.0D))) {
            if (entity1 != dolphin && entity1 != rider && entity1 instanceof EntityLiving) {
                AxisAlignedBB axisalignedbb = entity1.getBoundingBox().g(0.5D);
                MovingObjectPosition movingobjectposition = axisalignedbb.b(vec3d, vec3d1);
                if (movingobjectposition != null) {
                    double d1 = vec3d.distanceSquared(movingobjectposition.pos);
                    if (d1 < d0 || d0 == 0.0D) {
                        entity = (EntityLiving) entity1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity;
    }

    public void shoot(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        motX = d0 = (d0 / (double) f2) * f;
        motY = d1 = (d1 / (double) f2) * f;
        motZ = d2 = (d2 / (double) f2) * f;
        lastYaw = yaw = (float) (MathHelper.c(d0, d2) * 57.2957763671875D);
        lastPitch = pitch = (float) (MathHelper.c(d1, (double) MathHelper.sqrt(d0 * d0 + d2 * d2)) * 57.2957763671875D);
    }

    // entityInit
    protected void x_() {
    }

    // readEntityFromNBT
    protected void a(NBTTagCompound nbt) {
        if (nbt.hasKeyOfType("Owner", 10)) {
            this.nbt = nbt.getCompound("Owner");
        }

    }

    // writeEntityToNBT
    protected void b(NBTTagCompound nbttagcompound) {
        if (dolphin != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.a("OwnerUUID", dolphin.getUniqueID());
            nbttagcompound.set("Owner", nbt);
        }
    }

    // restoreOwnerFromSave
    private void restoreOwnerFromSave() {
        if (nbt != null && nbt.b("OwnerUUID")) {
            UUID uuid = nbt.a("OwnerUUID");
            for (RidableDolphin entity : world.a(RidableDolphin.class, getBoundingBox().g(15.0D))) {
                if (dolphin.getUniqueID().equals(uuid)) {
                    this.dolphin = entity;
                    break;
                }
            }
        }
        nbt = null;
    }
}
