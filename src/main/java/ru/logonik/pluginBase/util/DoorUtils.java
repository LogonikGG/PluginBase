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

    public static boolean openDoor(Location location) {
        return setDoorOpen(location, true);
    }

    public static boolean closeDoor(Location location) {
        return setDoorOpen(location, false);
    }

    public static boolean toggleDoor(Location location) {
        Block block = location.getBlock();
        Block doorBlock = getDoorBlock(block);
        if (doorBlock == null) return false;

        Door door = (Door) doorBlock.getBlockData();
        return setDoorState(doorBlock, !door.isOpen());
    }

    private static boolean setDoorOpen(Location location, boolean open) {
        Block block = location.getBlock();
        Block doorBlock = getDoorBlock(block);
        if (doorBlock == null) return false;

        return setDoorState(doorBlock, open);
    }

    private static boolean setDoorState(Block doorBlock, boolean open) {
        BlockData data = doorBlock.getBlockData();
        if (!(data instanceof Door)) return false;
        Door door = (Door) data;

        if (door.isOpen() == open) return true;

        door.setOpen(open);
        doorBlock.setBlockData(door, true);

        Block otherHalf = getOtherHalf(doorBlock, door);
        if (otherHalf != null) {
            otherHalf.setBlockData(door, true);
        }

        return true;
    }

    private static Block getDoorBlock(Block block) {
        if (!(block.getBlockData() instanceof Door)) return null;
        Door door = (Door) block.getBlockData();

        return door.getHalf() == Bisected.Half.TOP
                ? block.getRelative(BlockFace.DOWN)
                : block;
    }

    private static Block getOtherHalf(Block block, Door door) {
        return door.getHalf() == Bisected.Half.BOTTOM
                ? block.getRelative(BlockFace.UP)
                : block.getRelative(BlockFace.DOWN);
    }
}
