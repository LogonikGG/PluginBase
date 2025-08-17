package ru.logonik.pluginBase;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class BukkitUtil {

    public static void giveItemOrDrop(Player player, ItemStack... itemStacks) {
        HashMap<Integer, ItemStack> notFit = player.getInventory().addItem(itemStacks);
        Location location = player.getLocation();
        for (ItemStack notFitted : notFit.values()) {
            if(notFitted == null) continue;
            location.getWorld().dropItem(location, notFitted);
        }
    }

    public static ItemStack[] deepCopyItemStackArray(ItemStack[] original) {
        if (original == null) {
            return null;
        }
        ItemStack[] copy = new ItemStack[original.length];
        for (int i = 0; i < original.length; i++) {
            if (original[i] != null) {
                copy[i] = original[i].clone();
            }
        }
        return copy;
    }

    public static Collection<PotionEffect> deepCopyPotionEffects(Collection<PotionEffect> original) {
        if (original == null) {
            return null;
        }
        Collection<PotionEffect> copy = new ArrayList<>(original.size());
        for (PotionEffect effect : original) {
            if (effect != null) {
                copy.add(new PotionEffect(
                        effect.getType(),
                        effect.getDuration(),
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.hasParticles(),
                        effect.hasIcon()
                ));
            }
        }

        return copy;
    }
}
