package net.pl3x.bukkit.ridables.util;

import net.pl3x.bukkit.ridables.entity.EntityDolphinSpit;
import net.pl3x.bukkit.ridables.entity.EntityRidableDolphin;
import net.pl3x.bukkit.ridables.entity.EntityRidableOcelot;
import net.pl3x.bukkit.ridables.entity.EntityRidableTurtle;
import net.pl3x.bukkit.ridables.entity.EntityRidableWolf;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Utils {
    public static ItemStack getItem(Player player, EquipmentSlot hand) {
        return hand == EquipmentSlot.OFF_HAND ?
                player.getInventory().getItemInOffHand() :
                player.getInventory().getItemInMainHand();
    }

    public static void setItem(Player player, ItemStack itemStack, EquipmentSlot hand) {
        if (hand == EquipmentSlot.OFF_HAND) {
            player.getInventory().setItemInOffHand(itemStack);
        } else {
            player.getInventory().setItemInMainHand(itemStack);
        }
    }

    public static ItemStack subtract(ItemStack stack) {
        return subtract(stack, 1);
    }

    public static ItemStack subtract(ItemStack stack, int amount) {
        stack.setAmount(Math.max(0, stack.getAmount() - amount));
        return stack;
    }

    public static Location buildLocation(Location loc, Location pLoc) {
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY() + 0.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        loc.setYaw(pLoc.getYaw());
        loc.setPitch(pLoc.getPitch());
        return loc;
    }

    public static boolean isFood(EntityType entityType, ItemStack item) {
        switch (entityType) {
            case DOLPHIN:
                return EntityRidableDolphin.isFood(item);
            case OCELOT:
                return EntityRidableOcelot.isFood(item);
            case TURTLE:
                return EntityRidableTurtle.isFood(item);
            case WOLF:
                return EntityRidableWolf.isFood(item);
            case PHANTOM:
                // cant feed a phantom
            default:
                return false;
        }
    }
}