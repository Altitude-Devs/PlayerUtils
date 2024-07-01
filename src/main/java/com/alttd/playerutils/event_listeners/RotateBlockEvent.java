package com.alttd.playerutils.event_listeners;

import com.alttd.playerutils.util.Logger;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class RotateBlockEvent implements Listener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RotateBlockEvent.class);
    private final HashSet<UUID> rotateEnabled = new HashSet<>();
    private final Logger logger;
    private static final List<BlockFace> VALID_FOUR_STATES = List.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

    public RotateBlockEvent(Logger logger) {
        this.logger = logger;
    }


    public synchronized boolean toggleRotate(UUID uuid) {
        if (rotateEnabled.contains(uuid)) {
            rotateEnabled.remove(uuid);
            return false;
        } else {
            rotateEnabled.add(uuid);
            return true;
        }
    }

    private synchronized boolean hasRotateEnabled(UUID uuid) {
        return rotateEnabled.contains(uuid);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!hasRotateEnabled(player.getUniqueId()))
            return;

        ItemStack item = event.getItem();
        if (item == null || !item.getType().equals(Material.WOODEN_HOE))
            return;

        Block block = event.getClickedBlock();
        if (block == null)
            return;

        Material type = block.getType();
        logger.debug(String.format("Material %s with action %s", type, event.getAction().isLeftClick() ? "left click" : "right click"));
        if (type.equals(Material.IRON_TRAPDOOR) && event.getAction().isLeftClick()) {
            event.setCancelled(true);
            logger.debug("Toggling trap door");
            toggleTrapDoor(block, player);
        } else if (Tag.BIG_DRIPLEAF_PLACEABLE.isTagged(type) && event.getAction().isLeftClick()) {
            event.setCancelled(true);
            logger.debug("Toggling drip leaf");
            toggleDripLeaf(block, player);
        } else if (Tag.STAIRS.isTagged(type)) {
            event.setCancelled(true);
            rotateStairs(block, player);
        } else if (Tag.WALLS.isTagged(type)) {
            event.setCancelled(true);
            toggleWall(block, event.getBlockFace(), player, event.getAction().isLeftClick());
        } else if (Tag.FENCES.isTagged(type)) {
            event.setCancelled(true);
            toggleFence(block, event.getBlockFace(), player);
        } else if(Tag.RAILS.isTagged(type)) {
            event.setCancelled(true);
            toggleRails(block, player);
        } else if (block.getBlockData() instanceof Directional directional) {
            event.setCancelled(true);
            rotateDirectionalBlock(block, directional, player);
        } else if (block.getBlockData() instanceof Orientable orientable && !block.getType().equals(Material.NETHER_PORTAL)) {
            event.setCancelled(true);
            rotateOrientableBlock(block, orientable, player);
        }
    }

    private void toggleDripLeaf(Block block, Player player) {
        if (!(block instanceof BigDripleaf bigDripleaf)) {
            return;
        }

        if (cannotBuild(block, player)) {
            return;
        }

        BigDripleaf.Tilt[] values = BigDripleaf.Tilt.values();

        int ordinal = bigDripleaf.getTilt().ordinal();
        logger.debug("drip leaf is on tilt %d".formatted(ordinal));
        if (++ordinal == values.length) {
            ordinal = 0;
        }

        bigDripleaf.setTilt(values[ordinal]);
        logger.debug("drip leaf set to tilt %d".formatted(ordinal));
        block.setBlockAndForget(bigDripleaf);
        logger.debug("drip leaf set");
    }

    private void toggleTrapDoor(Block block, Player player) {
        if (!(block instanceof TrapDoor trapDoor)) {
            logger.debug("Trap door early return 1");
            return;
        }

        if (cannotBuild(block, player)) {
            logger.debug("Trap door early return 2");
            return;
        }

        logger.debug("Trap door is %s".formatted(trapDoor.isOpen() ? "open" : "closed"));
        trapDoor.setOpen(!trapDoor.isOpen());
        logger.debug(String.format("Trap door set to %s", trapDoor.isOpen() ? "open" : "close"));
    }

    private void toggleRails(Block block, Player player) {
        if (!(block instanceof Rail rail)) {
            return;
        }

        if (cannotBuild(block, player)) {
            return;
        }

        rail.setShape(getNextRailShape(rail, player.isSneaking()));
        block.setBlockAndForget(rail);
    }

    private Rail.Shape getNextRailShape(Rail rail, boolean reverse) {
        Rail.Shape shape = rail.getShape();
        List<Rail.Shape> collect = rail.getShapes().stream().sorted().toList();
        Iterator<Rail.Shape> iterator = collect.iterator();

        if (reverse) {
            Rail.Shape last = iterator.next();
            if (last.equals(shape))
                return collect.get(collect.size() - 1);
            while (iterator.hasNext()) {
                Rail.Shape next = iterator.next();
                if (next.equals(shape))
                    return last;
                last = next;
            }
            return collect.get(0);
        } else {
            while (iterator.hasNext() && !iterator.next().equals(shape))
                ;
            return iterator.hasNext() ? iterator.next() : collect.get(0);
        }
    }

    private void rotateOrientableBlock(Block block, Orientable orientable, Player player) {
        if (cannotBuild(block, player)) {
            return;
        }

        Axis axis = orientable.getAxis();
        LinkedList<Axis> collect = orientable.getAxes().stream()
                .sorted()
                .collect(Collectors.toCollection(LinkedList::new));

        int index = getNextRotation(player, collect.indexOf(axis), collect.size());

        orientable.setAxis(collect.get(index));
        block.setBlockAndForget(orientable);
    }

    private void rotateDirectionalBlock(Block block, Directional directional, Player player) {
        if (cannotBuild(block, player)) {
            return;
        }

        BlockFace facing = directional.getFacing();
        LinkedList<BlockFace> collect = directional.getFaces().stream()
                .sorted()
                .collect(Collectors.toCollection(LinkedList::new));

        int index = getNextRotation(player, collect.indexOf(facing), collect.size());

        directional.setFacing(collect.get(index));
        block.setBlockAndForget(directional);
    }

    private int getNextRotation(Player player, int i, int size) {
        int index = i;
        if (player.isSneaking()) {
            index--;
            if (index < 0)
                index = size - 1;
        } else {
            index++;
            if (index >= size)
                index = 0;
        }
        return index;
    }

    private void toggleFence(Block block, BlockFace blockFace, Player player) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Fence fence)) {
            return;
        }

        if (cannotBuild(block, player)) {
            return;
        }

        if (player.isSneaking()) {
            fence.getFaces().forEach(face -> fence.setFace(face, false));
            block.setBlockAndForget(fence);
            return;
        }

        if (!fence.getAllowedFaces().contains(blockFace)) {
            return;
        }

        Set<BlockFace> faces = fence.getFaces();
        fence.setFace(blockFace, !faces.contains(blockFace));
        block.setBlockAndForget(fence);
    }

    private void toggleWall(Block block, BlockFace blockFace, Player player, boolean leftClick) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Wall wall)) {
            return;
        }

        if (cannotBuild(block, player)) {
            return;
        }

        if (player.isSneaking()) {
            VALID_FOUR_STATES.forEach(face -> wall.setHeight(face, Wall.Height.NONE));
            wall.setUp(true);
            block.setBlockAndForget(wall);
            return;
        }

        if (!leftClick) {
            if (wallHasNoFaces(wall))
                return;
            wall.setUp(!wall.isUp());
            block.setBlockAndForget(wall);
            return;
        }

        if (!VALID_FOUR_STATES.contains(blockFace))
            return;

        int ordinal = wall.getHeight(blockFace).ordinal();
        if (ordinal == Wall.Height.values().length - 1)
            ordinal = 0;
        else
            ordinal++;
        wall.setHeight(blockFace, Wall.Height.values()[ordinal]);
        if (!wall.isUp() && wallHasNoFaces(wall)) {
            wall.setUp(true);
        }
        block.setBlockAndForget(wall);
    }

    private boolean wallHasNoFaces(Wall wall) {
        for (BlockFace validFourState : VALID_FOUR_STATES) {
            if (wall.getHeight(validFourState).equals(Wall.Height.NONE))
                continue;
            return false;
        }
        return true;
    }

    private void rotateStairs(Block block, Player player) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Stairs stairs))
            return;

        if (cannotBuild(block, player)) {
            return;
        }

        Stairs nextRotation;
        if (player.isSneaking())
            nextRotation = getPreviousRotation(stairs);
        else
            nextRotation = getNextRotation(stairs);
        block.setBlockAndForget(nextRotation);
    }

    private Stairs getNextRotation(Stairs stairs) {
        Stairs.Shape shape = stairs.getShape();
        if (shape.ordinal() != Stairs.Shape.values().length - 1) {
            stairs.setShape(Stairs.Shape.values()[shape.ordinal() + 1]);
            return stairs;
        }

        Bisected.Half half = stairs.getHalf();
        if (half.equals(Bisected.Half.BOTTOM)) {
            stairs.setShape(Stairs.Shape.values()[0]);
            stairs.setHalf(Bisected.Half.TOP);
            return stairs;
        }

        BlockFace facing = stairs.getFacing();
        int index = VALID_FOUR_STATES.indexOf(facing);
        if (index == 3) {
            index = 0;
        } else {
            index += 1;
        }

        stairs.setFacing(VALID_FOUR_STATES.get(index));
        stairs.setHalf(Bisected.Half.BOTTOM);
        stairs.setShape(Stairs.Shape.values()[0]);
        return stairs;
    }

    private Stairs getPreviousRotation(Stairs stairs) {
        Stairs.Shape shape = stairs.getShape();
        if (shape.ordinal() != 0) {
            stairs.setShape(Stairs.Shape.values()[shape.ordinal() - 1]);
            return stairs;
        }
        Bisected.Half half = stairs.getHalf();
        if (half.equals(Bisected.Half.TOP)) {
            stairs.setShape(Stairs.Shape.values()[Stairs.Shape.values().length - 1]);
            stairs.setHalf(Bisected.Half.BOTTOM);
            return stairs;
        }
        BlockFace facing = stairs.getFacing();
        int index = VALID_FOUR_STATES.indexOf(facing);
        if (index == 0) {
            index = 3;
        } else {
            index -= 1;
        }
        stairs.setFacing(VALID_FOUR_STATES.get(index));
        stairs.setHalf(Bisected.Half.TOP);
        stairs.setShape(Stairs.Shape.values()[Stairs.Shape.values().length - 1]);
        return stairs;
    }

    private boolean cannotBuild(Block block, Player player) {
        BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, block.getState(), block, new ItemStack(block.getType()), player, true, EquipmentSlot.HAND);
        Bukkit.getPluginManager().callEvent(placeEvent);
        return placeEvent.isCancelled();
    }
}
