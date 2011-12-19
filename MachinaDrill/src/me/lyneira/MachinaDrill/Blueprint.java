package me.lyneira.MachinaDrill;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.BlueprintFactory;
import me.lyneira.MachinaCore.ModuleFactory;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MovableBlueprint;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * MachinaBlueprint representing a Drill blueprint
 * 
 * @author Lyneira
 */
final class Blueprint extends MovableBlueprint {
    private static BlueprintFactory blueprint;
    final static int mainModuleId;
    final static Map<BlockRotation, BlockVector[]> drillPattern = new EnumMap<BlockRotation, BlockVector[]>(BlockRotation.class);

    final static int drillPatternSize;
    private final static Material anchorMaterial = Material.GOLD_BLOCK;
    private final static Material baseMaterial = Material.WOOD;
    final static Material headMaterial = Material.IRON_BLOCK;
    private final static Material furnaceMaterial = Material.FURNACE;
    private final static Material burningFurnaceMaterial = Material.BURNING_FURNACE;
    final static Material chestMaterial = Material.CHEST;
    final static Material rotateMaterial = Material.STICK;

    final static BlueprintBlock chest;
    final static BlueprintBlock centralBase;
    final static BlueprintBlock head;
    final static BlueprintBlock furnace;

    static {
        blueprint = new BlueprintFactory(1);
        
        ModuleFactory mainModule = blueprint.newModule();
        mainModuleId = mainModule.id;
        
        // Add key blocks to the blueprint
        // The lever is always key.
        mainModule.addKey(new BlockVector(0, 1, 0), Material.LEVER);
        // Anchor
        mainModule.addKey(new BlockVector(0, 0, 0), anchorMaterial);
        // Central base, used for ground detection
        centralBase = mainModule.addKey(new BlockVector(0, -1, 0), baseMaterial);
        // Furnace determines direction
        furnace = mainModule.addKey(new BlockVector(-1, -1, 0), burningFurnaceMaterial);

        // Add non-key blocks
        // Output chest
        chest = mainModule.add(new BlockVector(-1, 0, 0), chestMaterial);
        // Head
        head = mainModule.add(new BlockVector(1, 0, 0), headMaterial);
        mainModule.add(new BlockVector(0, -1, 1), baseMaterial);
        mainModule.add(new BlockVector(0, -1, -1), baseMaterial);
        
        // Add drill pattern data 3x3
        drillPatternSize = 9;
        BlockVector[] basePattern = new BlockVector[drillPatternSize];
        basePattern[0] = new BlockVector(2, 0, 0);
        basePattern[1] = new BlockVector(2, 1, 0);
        basePattern[2] = new BlockVector(2, 0, 1);
        basePattern[3] = new BlockVector(2, -1, 0);
        basePattern[4] = new BlockVector(2, 0, -1);
        basePattern[5] = new BlockVector(2, 1, -1);
        basePattern[6] = new BlockVector(2, 1, 1);
        basePattern[7] = new BlockVector(2, -1, 1);
        basePattern[8] = new BlockVector(2, -1, -1);
        for (BlockRotation i : BlockRotation.values()) {
            BlockVector[] rotatedPattern = new BlockVector[drillPatternSize];
            for (int j = 0; j < drillPatternSize; j++) {
                rotatedPattern[j] = basePattern[j].rotated(i);
            }
            drillPattern.put(i, rotatedPattern);
        }
    }

    public final static Blueprint instance = new Blueprint();

    private Blueprint() {
        super(blueprint);
        blueprint = null;
    }

    /**
     * Detects whether a drill is present at the given BlockLocation. Key blocks
     * defined above must be detected manually.
     */
    public Machina detect(Player player, final BlockLocation anchor, final BlockFace leverFace, ItemStack itemInHand) {
        if (leverFace != BlockFace.UP)
            return null;

        if (!anchor.checkType(anchorMaterial))
            return null;

        BlockLocation centralBase = anchor.getRelative(BlockFace.DOWN);
        if (!centralBase.checkType(baseMaterial))
            return null;

        // Check if the drill is on solid ground.
        if (!BlockData.isSolid(centralBase.getRelative(BlockFace.DOWN).getTypeId()))
            return null;

        // Search for a furnace around the central base.
        for (BlockRotation i : BlockRotation.values()) {
            if (!centralBase.getRelative(i.getYawFace()).checkType(furnaceMaterial))
                continue;

            BlockRotation yaw = i.getOpposite();
            if (!detectOther(anchor, yaw, mainModuleId))
                continue;

             if (!player.hasPermission("machinadrill.activate")) {
                player.sendMessage("You do not have permission to activate a drill.");
                return null;
            }

            // Detection was a success, now make the new drill.
            List<Integer> detectedModules = new ArrayList<Integer>(1);
            detectedModules.add(mainModuleId);

            Drill drill = new Drill(this, detectedModules, yaw, player, anchor);
            if (itemInHand != null && itemInHand.getType() == rotateMaterial) {
                // Support for rotation on a non-activated drill
                drill.doRotate(anchor, BlockRotation.yawFromLocation(player.getLocation()));
                drill.onDeActivate(anchor);
                drill = null;
            }
            return drill;
        }

        return null;
    }
}
