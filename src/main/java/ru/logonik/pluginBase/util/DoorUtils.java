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

        BlockData data = doorBlock.getBlockData();
        if (!(data instanceof Door)) return false;
        Door door = (Door) data;

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
        BlockData bottomData = bottomBlock.getBlockData();
        if (!(bottomData instanceof Door)) return false;
        Door bottomDoor = (Door) bottomData;

        if (bottomDoor.isOpen() == open) return true;

        // Меняем состояние нижней части
        bottomDoor.setOpen(open);
        bottomBlock.setBlockData(bottomDoor, false);

        // Меняем состояние верхней части
        Block topBlock = bottomBlock.getRelative(BlockFace.UP);
        BlockData topData = topBlock.getBlockData();

        if (!(topData instanceof Door)) {
            // Откатываем изменения, если верхний блок не дверь
            bottomBlock.setBlockData(bottomDoor, false);
            return false;
        }

        Door topDoor = (Door) topData;
        topDoor.setOpen(open);
        topBlock.setBlockData(topDoor, false);

        // Обновляем физику для обоих блоков
        bottomBlock.getState().update(true, false);
        topBlock.getState().update(true, false);

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