package me.lyneira.MachinaBuilder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.BlueprintFactory;
import me.lyneira.MachinaCore.EventSimulator;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MovableBlueprint;

/**
 * MachinaBlueprint representing a Builder blueprint
 * 
 * @author Lyneira
 */
public class Blueprint extends MovableBlueprint {
    final static Material headMaterial = Material.IRON_BLOCK;
    private final static Material baseMaterial = Material.WOOD;
    private final static Material furnaceMaterial = Material.FURNACE;
    private final static Material burningFurnaceMaterial = Material.BURNING_FURNACE;
    private final static Material supplyContainerMaterial = Material.CHEST;
    private final static Material lowerableHeadSupportMaterial = Material.FENCE;
    final static Material rotateMaterial = Material.STICK;

    // ***********************
    // **** Basic Builder ****
    // ***********************
    static int moduleBasicMain;
    static int moduleBasicLeft;
    static int moduleBasicRight;
    static int moduleBasicBackend;
    static int moduleBasicBackendRoad;

    static BlueprintBlock basicCentralBase;
    static BlueprintBlock basicChest;
    static BlueprintBlock basicFurnace;
    static BlueprintBlock basicFurnaceRoad;
    static BlueprintBlock basicChestRoad;
    static BlueprintBlock basicHeadPrimary;
    static BlueprintBlock basicHeadLeft;
    static BlueprintBlock basicHeadRight;

    // ************************
    // **** Bridge Builder ****
    // ************************

    static int moduleBridgeMain;
    static int moduleBridgeLeft;
    static int moduleBridgeRight;
    static int moduleBridgeHeadPrimary;
    static int moduleBridgeHeadLeft;
    static int moduleBridgeHeadRight;

    static BlueprintBlock bridgeCentralBase;
    static BlueprintBlock bridgePatternChest;
    static BlueprintBlock bridgeSupplyChest;
    static BlueprintBlock bridgeFurnace;

    static BlueprintBlock bridgeHeadPrimary;
    static BlueprintBlock bridgeHeadLeft;
    static BlueprintBlock bridgeHeadRight;

    /**
     * Static blueprint constructor so that we have a {@link BlueprintFactory}
     * to give to super()
     * 
     * @return A new Blueprint
     */
    static Blueprint blueprint() {
        BlueprintFactory blueprint = new BlueprintFactory(11);

        // ***********************
        // **** Basic Builder ****
        // ***********************
        moduleBasicMain = blueprint.newModule();
        moduleBasicLeft = blueprint.newModule();
        moduleBasicRight = blueprint.newModule();
        moduleBasicBackend = blueprint.newModule();
        moduleBasicBackendRoad = blueprint.newModule();

        // **** Main module ****
        // The lever is always key.
        blueprint.addKey(new BlockVector(0, 1, 0), Material.LEVER, moduleBasicMain);
        // Central base, used for ground detection
        basicCentralBase = blueprint.addKey(new BlockVector(0, 0, 0), baseMaterial, moduleBasicMain);
        basicChest = blueprint.add(new BlockVector(1, 1, 0), supplyContainerMaterial, moduleBasicMain);
        basicHeadPrimary = blueprint.add(new BlockVector(1, 0, 0), headMaterial, moduleBasicMain);

        // **** Basic backend module ****
        // Furnace has to be key because it isn't burning at detection
        basicFurnace = blueprint.addKey(new BlockVector(-1, 0, 0), burningFurnaceMaterial, moduleBasicBackend);

        // **** Road builder backend module ****
        // Furnace has to be key because it isn't burning at detection
        basicFurnaceRoad = blueprint.addKey(new BlockVector(-2, 0, 0), burningFurnaceMaterial, moduleBasicBackendRoad);
        blueprint.add(new BlockVector(-1, 0, 0), baseMaterial, moduleBasicBackendRoad);
        basicChestRoad = blueprint.add(new BlockVector(-1, 1, 0), supplyContainerMaterial, moduleBasicBackendRoad);

        // **** Left module ****
        basicHeadLeft = blueprint.add(new BlockVector(1, 0, -1), headMaterial, moduleBasicLeft);
        blueprint.add(new BlockVector(0, 0, -1), baseMaterial, moduleBasicLeft);

        // **** Right module ****
        basicHeadRight = blueprint.add(new BlockVector(1, 0, 1), headMaterial, moduleBasicRight);
        blueprint.add(new BlockVector(0, 0, 1), baseMaterial, moduleBasicRight);

        // ************************
        // **** Bridge Builder ****
        // ************************
        moduleBridgeMain = blueprint.newModule();
        moduleBridgeLeft = blueprint.newModule();
        moduleBridgeRight = blueprint.newModule();
        moduleBridgeHeadPrimary = blueprint.newModule();
        moduleBridgeHeadLeft = blueprint.newModule();
        moduleBridgeHeadRight = blueprint.newModule();

        // **** Main module ****
        // The lever is always key.
        blueprint.addKey(new BlockVector(0, 1, 0), Material.LEVER, moduleBridgeMain);
        // Central base, used for ground detection
        bridgeCentralBase = blueprint.addKey(new BlockVector(0, 0, 0), baseMaterial, moduleBridgeMain);
        bridgeSupplyChest = blueprint.add(new BlockVector(1, 1, 0), supplyContainerMaterial, moduleBridgeMain);
        blueprint.add(new BlockVector(1, 0, 0), baseMaterial, moduleBridgeMain);
        blueprint.add(new BlockVector(-1, 0, 0), baseMaterial, moduleBridgeMain);
        bridgePatternChest = blueprint.add(new BlockVector(-1, 1, 0), supplyContainerMaterial, moduleBridgeMain);
        bridgeFurnace = blueprint.addKey(new BlockVector(-2, 0, 0), burningFurnaceMaterial, moduleBridgeMain);

        // **** Left module ****
        blueprint.add(new BlockVector(0, 0, -1), baseMaterial, moduleBridgeLeft);
        blueprint.add(new BlockVector(1, 0, -1), baseMaterial, moduleBridgeLeft);

        // **** Right module ****
        blueprint.add(new BlockVector(0, 0, 1), baseMaterial, moduleBridgeRight);
        blueprint.add(new BlockVector(1, 0, 1), baseMaterial, moduleBridgeRight);

        // **** Movable head sub-machina ****
        // Vectors in these modules are relative to its primary head, not the
        // bridge builder.
        bridgeHeadPrimary = blueprint.add(new BlockVector(0, 0, 0), headMaterial, moduleBridgeHeadPrimary);
        blueprint.add(new BlockVector(0, 1, 0), lowerableHeadSupportMaterial, moduleBridgeHeadPrimary);
        blueprint.add(new BlockVector(0, 2, 0), lowerableHeadSupportMaterial, moduleBridgeHeadPrimary);

        bridgeHeadLeft = blueprint.add(new BlockVector(0, 0, -1), headMaterial, moduleBridgeHeadLeft);
        blueprint.add(new BlockVector(0, 1, -1), lowerableHeadSupportMaterial, moduleBridgeHeadLeft);
        blueprint.add(new BlockVector(0, 2, -1), lowerableHeadSupportMaterial, moduleBridgeHeadLeft);

        bridgeHeadRight = blueprint.add(new BlockVector(0, 0, 1), headMaterial, moduleBridgeHeadRight);
        blueprint.add(new BlockVector(0, 1, 1), lowerableHeadSupportMaterial, moduleBridgeHeadRight);
        blueprint.add(new BlockVector(0, 2, 1), lowerableHeadSupportMaterial, moduleBridgeHeadRight);

        return new Blueprint(blueprint);
    }

    private Blueprint(BlueprintFactory blueprint) {
        super(blueprint);
    }

    /**
     * Detects whether a builder is present at the given BlockLocation. Key
     * blocks defined above must be detected manually.
     */
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        if (leverFace != BlockFace.UP)
            return null;

        if (!anchor.checkType(baseMaterial))
            return null;

        // Check if the Builder is on solid ground.
        if (!BlockData.isSolid(anchor.getRelative(BlockFace.DOWN).getTypeId()))
            return null;

        // Search for a furnace around the anchor.
        for (BlockRotation i : BlockRotation.values()) {
            BuilderDetector detector = findDetector(player, anchor, i);

            if (detector == null)
                continue;

            if (!player.hasPermission("machinabuilder.activate")) {
                player.sendMessage("You do not have permission to activate a builder.");
                return null;
            }
            if (!Builder.canActivate(player)) {
                player.sendMessage("You cannot activate any more builders.");
                return null;
            }

            if (detector.inventoryProtected())
                return null;

            detector.detectOptionalModules();

            Builder builder = detector.create();

            if (itemInHand != null && itemInHand.getType() == rotateMaterial) {
                builder.doRotate(anchor, BlockRotation.yawFromLocation(player.getLocation()));
                builder.onDeActivate(anchor);
                builder = null;
            }
            if (builder != null)
                builder.increment();
            return builder;
        }

        return null;
    }

    private final BuilderDetector findDetector(final Player player, final BlockLocation anchor, final BlockRotation yaw) {
        BlockFace furnaceFace = yaw.getOpposite().getYawFace();
        if (anchor.getRelative(furnaceFace).checkType(furnaceMaterial)) {
            // Basic builder
            if (detectOther(anchor, yaw, moduleBasicMain)) {
                // The furnace we detected is moduleBasicBackend
                return new BasicDetector(player, anchor, yaw);
            }
        } else if (anchor.getRelative(furnaceFace, 2).checkType(furnaceMaterial)) {
            // Road builder or bridge builder
            if (detectOther(anchor, yaw, moduleBasicMain)) {
                // Road builder
                if (detectOther(anchor, yaw, moduleBasicBackendRoad)) {
                    return new RoadDetector(player, anchor, yaw);
                }
            } else if (detectOther(anchor, yaw, moduleBridgeMain)) {
                int offset;
                BlockLocation head = anchor.getRelative(yaw.getYawFace(), 2);
                for (offset = 0; offset < 3; offset++) {
                    if (detectOther(head, yaw, moduleBridgeHeadPrimary)) {
                        return new BridgeDetector(player, anchor, yaw, offset);
                    }
                    head = head.getRelative(BlockFace.DOWN);
                }
            }
        }
        return null;
    }

    private abstract class BuilderDetector {
        protected final Player player;
        protected final BlockLocation anchor;
        protected final BlockRotation yaw;
        protected final List<Integer> detectedModules = new ArrayList<Integer>(4);

        BuilderDetector(Player player, BlockLocation anchor, BlockRotation yaw) {
            this.player = player;
            this.anchor = anchor;
            this.yaw = yaw;
        }

        /**
         * Returns true if an inventory in this type of builder is protected.
         * 
         * @param yaw
         * @return
         */
        abstract boolean inventoryProtected();

        /**
         * Detects any optional modules.
         * 
         * @param yaw
         */
        abstract void detectOptionalModules();

        /**
         * Creates an instance of the new builder.
         * 
         * @param yaw
         * @return
         */
        abstract Builder create();
    }

    private class BasicDetector extends BuilderDetector {
        BasicDetector(Player player, BlockLocation anchor, BlockRotation yaw) {
            super(player, anchor, yaw);
            detectedModules.add(moduleBasicMain);
            detectedModules.add(moduleBasicBackend);
        }

        @Override
        public boolean inventoryProtected() {
            return EventSimulator.inventoryProtected(yaw, player, anchor, basicChest, basicFurnace);
        }

        @Override
        public void detectOptionalModules() {
            if (detectOther(anchor, yaw, moduleBasicLeft)) {
                detectedModules.add(moduleBasicLeft);
            }
            if (detectOther(anchor, yaw, moduleBasicRight)) {
                detectedModules.add(moduleBasicRight);
            }
        }

        @Override
        public Builder create() {
            return new BasicBuilder(Blueprint.this, detectedModules, yaw, player, anchor);
        }
    }

    private class RoadDetector extends BuilderDetector {
        RoadDetector(Player player, BlockLocation anchor, BlockRotation yaw) {
            super(player, anchor, yaw);
            detectedModules.add(moduleBasicMain);
            detectedModules.add(moduleBasicBackendRoad);
        }

        @Override
        boolean inventoryProtected() {
            return EventSimulator.inventoryProtected(yaw, player, anchor, basicChest, basicChestRoad, basicFurnaceRoad);
        }

        @Override
        void detectOptionalModules() {
            if (detectOther(anchor, yaw, moduleBasicLeft)) {
                detectedModules.add(moduleBasicLeft);
            }
            if (detectOther(anchor, yaw, moduleBasicRight)) {
                detectedModules.add(moduleBasicRight);
            }
        }

        @Override
        Builder create() {
            return new RoadBuilder(Blueprint.this, detectedModules, yaw, player, anchor);
        }
    }

    private class BridgeDetector extends BuilderDetector {
        private final int offset;
        protected final List<Integer> detectedHeadModules = new ArrayList<Integer>(3);
        
        BridgeDetector(Player player, BlockLocation anchor, BlockRotation yaw, int offset) {
            super(player, anchor, yaw);
            this.offset = offset;
            detectedModules.add(moduleBridgeMain);
            detectedHeadModules.add(moduleBridgeHeadPrimary);
        }

        @Override
        boolean inventoryProtected() {
            return EventSimulator.inventoryProtected(yaw, player, anchor, bridgeSupplyChest, bridgePatternChest, bridgeFurnace);
        }

        @Override
        void detectOptionalModules() {
            // TODO Auto-generated method stub

        }

        @Override
        Builder create() {
            return new BridgeBuilder(Blueprint.this, detectedModules, yaw, player, anchor);
        }
    }
}
