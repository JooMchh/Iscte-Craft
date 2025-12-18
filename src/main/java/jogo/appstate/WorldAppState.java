package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import jogo.gameobject.Inventory.Inventory;
import jogo.gameobject.Inventory.ItemStack;
import jogo.gameobject.item.BlockItem;
import jogo.gameobject.item.BlockType;
import jogo.voxel.VoxelBlockType;
import jogo.voxel.VoxelPalette;
import jogo.voxel.VoxelWorld;

import java.util.ArrayList;
import java.util.Vector;

public class WorldAppState extends BaseAppState {

    private final Node rootNode;
    private final AssetManager assetManager;
    private final PhysicsSpace physicsSpace;
    private final Camera cam;
    private final InputAppState input;
    private PlayerAppState playerAppState;

    // world root for easy cleanup
    private Node worldNode;
    private VoxelWorld voxelWorld;
    private VoxelPalette palette;
    private com.jme3.math.Vector3f spawnPosition;

    public WorldAppState(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace, Camera cam, InputAppState input) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.physicsSpace = physicsSpace;
        this.cam = cam;
        this.input = input;
    }

    public void registerPlayerAppState(PlayerAppState playerAppState) { this.playerAppState = playerAppState; }

    @Override
    protected void initialize(Application app) {
        worldNode = new Node("World");
        rootNode.attachChild(worldNode);

        // Lighting
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.20f)); // slightly increased ambient
        worldNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.35f, -1.3f, -0.25f).normalizeLocal()); // more top-down to reduce harsh contrast
        sun.setColor(ColorRGBA.White.mult(0.85f)); // slightly dimmer sun
        worldNode.addLight(sun);

        // Voxel world 16x16x16 (reduced size for simplicity)
        voxelWorld = new VoxelWorld(assetManager, 320, 32, 320, getStateManager().getState(AIAppState.class));
        voxelWorld.generateLayers();
        voxelWorld.buildMeshes();
        voxelWorld.clearAllDirtyFlags();
        worldNode.attachChild(voxelWorld.getNode());
        voxelWorld.buildPhysics(physicsSpace);

        // compute recommended spawn
        spawnPosition = voxelWorld.getRecommendedSpawn();

        this.palette = VoxelPalette.defaultPalette();
    }

    public com.jme3.math.Vector3f getRecommendedSpawnPosition() {
        return spawnPosition != null ? spawnPosition.clone() : new com.jme3.math.Vector3f(25.5f, 12f, 25.5f);
    }

    public VoxelWorld getVoxelWorld() {
        return voxelWorld;
    }

    @Override
    public void update(float tpf) {
        Inventory playerInventory = playerAppState.getInventory();

        if (input != null && input.isMouseCaptured() && input.consumeBreakRequested()) {
            var pick = voxelWorld.pickFirstSolid(cam, 6f);
            pick.ifPresent(hit -> {
                VoxelWorld.Vector3i cell = hit.cell;
                byte blockID = voxelWorld.getBlock(cell.x, cell.y, cell.z);
                VoxelBlockType type = palette.get(voxelWorld.getBlock(cell.x, cell.y, cell.z));
                if (!type.isBreakable()) {
                    System.out.println("WorldAppState update: Player cannot break " + type.getName());
                    return;
                }
                if (voxelWorld.breakAt(cell.x, cell.y, cell.z)) {
                    playerInventory.setSlot(0, new BlockItem(type.getName(), blockID));
                    voxelWorld.rebuildDirtyChunks(physicsSpace);
                    playerAppState.refreshPhysics();
                }
            });
        }
        if (input != null && input.isMouseCaptured() && input.consumePlaceRequested()) { // Le place block
            ItemStack selectedItemStack = playerInventory.getSelectedItemStack();
            if (selectedItemStack != null && selectedItemStack.getItem() instanceof BlockType blockItem) {
                var pick = voxelWorld.pickFirstSolid(cam, 6f);
                pick.ifPresent(hit -> {
                    VoxelWorld.Vector3i cell = hit.cell;
                    VoxelWorld.Vector3i placePos = new VoxelWorld.Vector3i(
                            cell.x + (int) hit.normal.x,
                            cell.y + (int) hit.normal.y,
                            cell.z + (int) hit.normal.z
                    );
                    if (!canPlaceBlock(placePos)) {
                        System.out.println("WorldAppState update: can't place block in player position");
                        return;
                    }
                    voxelWorld.setBlock(placePos.x, placePos.y, placePos.z, blockItem.getBlockId());
                    playerInventory.subtractSlot(playerInventory.getSelectedSlot(), 1);
                    System.out.println("WorldAppState update: Block placed by Player at (" + placePos.x + "," + placePos.y + "," + placePos.z + ").");
                    voxelWorld.rebuildDirtyChunks(physicsSpace);
                    playerAppState.refreshPhysics();
                });
            }
        }
        if (input != null && input.consumeToggleShadingRequested()) {
            voxelWorld.toggleRenderDebug();
        }
    }

    private boolean canPlaceBlock(VoxelWorld.Vector3i placePos) {
        Vector3f playerPos = playerAppState.getPlayerPosition();
        Vector3f playerEyePos = playerAppState.getPlayerEyePosition();
        int px = (int) playerPos.x;
        int py = (int) playerPos.y;
        int pz = (int) playerPos.z;
        int ex = (int) playerEyePos.x;
        int ey = (int) playerEyePos.y;
        int ez = (int) playerEyePos.z;

        if ((px == placePos.x || ex == placePos.x) &&
                (py == placePos.y || ey == placePos.y) &&
                (pz == placePos.z || ez == placePos.z) || py < voxelWorld.getSizeY()
        ) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void cleanup(Application app) {
        if (worldNode != null) {
            // Remove all physics controls under worldNode
            worldNode.depthFirstTraversal(spatial -> {
                RigidBodyControl rbc = spatial.getControl(RigidBodyControl.class);
                if (rbc != null) {
                    physicsSpace.remove(rbc);
                    spatial.removeControl(rbc);
                }
            });
            worldNode.removeFromParent();
            worldNode = null;
        }
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}
