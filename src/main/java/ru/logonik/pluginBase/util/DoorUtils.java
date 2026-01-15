package ru.logonik.pluginBase.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;

public final class DoorUtils {

    private DoorUtils() {
    }

    public static boolean openDoor(Block block) {
        return setDoorOpen(block, true);
    }

    public static boolean closeDoor(Block block) {
        return setDoorOpen(block, false);
    }

    public static boolean toggleDoor(Block block) {
        Block doorBlock = getBottomDoorBlock(block);
        if (doorBlock == null) return false;

        Door door = (Door) doorBlock.getBlockData();
        return setDoorState(doorBlock, !door.isOpen());
    }

    public static boolean openDoor(Location location) {
        return openDoor(location.getBlock());
    }

    public static boolean closeDoor(Location location) {
        return closeDoor(location.getBlock());
    }

    public static boolean toggleDoor(Location location) {
        return toggleDoor(location.getBlock());
    }

    private static boolean setDoorOpen(Block block, boolean open) {
        Block doorBlock = getBottomDoorBlock(block);
        if (doorBlock == null) return false;

        return setDoorState(doorBlock, open);
    }

    private static boolean setDoorState(Block bottomBlock, boolean open) {
        BlockData data = bottomBlock.getBlockData();
        if (!(data instanceof Door)) return false;
        Door door = (Door) data;

        if (door.isOpen() == open) return true;

        door.setOpen(open);
        bottomBlock.setBlockData(door, true);

        Block topBlock = bottomBlock.getRelative(BlockFace.UP);
        topBlock.setBlockData(door, true);

        return true;
    }

    private static Block getBottomDoorBlock(Block block) {
        BlockData data = block.getBlockData();
        if (!(data instanceof Door)) return null;
        Door door = (Door) data;

        return door.getHalf() == Bisected.Half.TOP
                ? block.getRelative(BlockFace.DOWN)
                : block;
    }
}
