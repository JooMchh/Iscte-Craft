package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.collision.CollisionResults;
import jogo.engine.RenderIndex;
import jogo.gameobject.GameObject;
import jogo.gameobject.character.AIType;
import jogo.gameobject.character.Character;
import jogo.gameobject.item.Item;
import jogo.voxel.VoxelBlockType;
import jogo.voxel.VoxelPalette;
import jogo.voxel.VoxelWorld;
import jogo.voxel.blocks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class InteractionAppState extends BaseAppState {

    private final Node rootNode;
    private final Camera cam;
    private final InputAppState input;
    private final RenderIndex renderIndex;
    private final WorldAppState world;
    private final PhysicsSpace physicsSpace;
    private float reach = 5.5f;

    public InteractionAppState(Node rootNode, Camera cam, InputAppState input, RenderIndex renderIndex, WorldAppState world, PhysicsSpace physicsSpace) {
        this.rootNode = rootNode;
        this.cam = cam;
        this.input = input;
        this.renderIndex = renderIndex;
        this.world = world;
        this.physicsSpace = physicsSpace;
    }

    @Override
    protected void initialize(Application app) { }

    @Override
    public void update(float tpf) {
        if (!input.isMouseCaptured()) return;
        if (!input.consumeInteractRequested()) return;

        // get renderAppState
        RenderAppState renderAppState = getStateManager().getState(RenderAppState.class);

        // get AIAppState
        AIAppState aiAppState = getStateManager().getState(AIAppState.class);

        // get playerAppState
        PlayerAppState playerAppState = getStateManager().getState(PlayerAppState.class);

        Vector3f origin = cam.getLocation();
        Vector3f dir = cam.getDirection().normalize();

        // 1) Try to interact with a rendered GameObject (items)
        Ray ray = new Ray(origin, dir);
        ray.setLimit(reach);
        CollisionResults results = new CollisionResults();
        rootNode.collideWith(ray, results);
        if (results.size() > 0) {
            Spatial hit = results.getClosestCollision().getGeometry();
            GameObject obj = findRegistered(hit);
            // get currentPlayer through app state
            PlayerAppState player = getStateManager().getState(PlayerAppState.class);
            if (obj instanceof Item item) { // if is item
                if (player != null) {
                    item.onInteract(player);
                    item.consumeItem(renderAppState.getRegistry());
                    System.out.println("Interacted with item: " + obj.getName());
                    return; // prefer item interaction if both are hit
                } else {
                    System.out.println("Failed to interact with item: " + obj.getName() + ", PlayerAppState not found");
                }
            } else if (obj instanceof AIType aiCharacter) {  // on an AI
                if (player != null) {
                    aiCharacter.onInteract(player);
                }
            }
        }

        // 2) If no item hit, consider voxel block under crosshair (exercise for students)
        VoxelWorld vw = world != null ? world.getVoxelWorld() : null;
        if (vw != null) {
            vw.pickFirstSolid(cam, reach).ifPresent(hit -> {
                VoxelWorld.Vector3i cell = hit.cell; // The To do
                byte blockID = world.getVoxelWorld().getBlock(cell.x, cell.y, cell.z);
                VoxelBlockType type = world.getVoxelWorld().getPalette().get(blockID);

                if (type instanceof WoodPlankBlockType) {
                    List<Vector3f> destroyPositions = new ArrayList<>();

                    HashMap<String,VoxelBlockType> surroundingBlocks = world.getVoxelWorld().checkSurroundings(cell.x, cell.y, cell.z, 1, type.getName());

                    if (surroundingBlocks.size() == 4) {
                        for (String blockDirectionKey : surroundingBlocks.keySet()) { // consume wood plank blocks around

                            VoxelBlockType surroundingBlock = surroundingBlocks.get(blockDirectionKey);
                            String[] blockDirectionKeySplit = blockDirectionKey.split(" : ");
                            String blockDirection = blockDirectionKeySplit[0];

                            int x = 0;
                            int y = 0;
                            int z = 0;

                            switch (blockDirection) {
                                case "XPositive":
                                    x = 1;
                                    break;
                                case "XNegative":
                                    x = -1;
                                    break;
                                case "YPositive":
                                    y = 1;
                                    break;
                                case "YNegative":
                                    y = -1;
                                    break;
                                case "ZPositive":
                                    z = 1;
                                    break;
                                case "ZNegative":
                                    z = -1;
                                    break;
                            }

                            int bx = cell.x+x;
                            int by = cell.y+y;
                            int bz = cell.z+z;

                            world.getVoxelWorld().breakAt(bx,by,bz);
                        }

                        world.getVoxelWorld().breakAt(cell.x,cell.y,cell.z);
                        world.getVoxelWorld().rebuildDirtyChunks(physicsSpace);
                        playerAppState.refreshPhysics();
                        aiAppState.spawnWoodGolemAlly(new Vector3f(cell.x, cell.y, cell.z));
                    }



                } else if (type instanceof MetalBlockType) {
                    List<Vector3f> destroyPositions = new ArrayList<>();

                    HashMap<String,VoxelBlockType> surroundingBlocks = world.getVoxelWorld().checkSurroundings(cell.x, cell.y, cell.z, 1, type.getName());

                    if (surroundingBlocks.size() == 4) {
                        for (String blockDirectionKey : surroundingBlocks.keySet()) { // consume metal plank blocks around

                            VoxelBlockType surroundingBlock = surroundingBlocks.get(blockDirectionKey);
                            String[] blockDirectionKeySplit = blockDirectionKey.split(" : ");
                            String blockDirection = blockDirectionKeySplit[0];

                            int x = 0;
                            int y = 0;
                            int z = 0;

                            switch (blockDirection) {
                                case "XPositive":
                                    x = 1;
                                    break;
                                case "XNegative":
                                    x = -1;
                                    break;
                                case "YPositive":
                                    y = 1;
                                    break;
                                case "YNegative":
                                    y = -1;
                                    break;
                                case "ZPositive":
                                    z = 1;
                                    break;
                                case "ZNegative":
                                    z = -1;
                                    break;
                            }

                            int bx = cell.x+x;
                            int by = cell.y+y;
                            int bz = cell.z+z;

                            world.getVoxelWorld().breakAt(bx,by,bz);
                        }

                        world.getVoxelWorld().breakAt(cell.x,cell.y,cell.z);
                        world.getVoxelWorld().rebuildDirtyChunks(physicsSpace);
                        playerAppState.refreshPhysics();

                        aiAppState.spawnMetalGolemAlly(new Vector3f(cell.x, cell.y, cell.z));
                    }

                } else if (type instanceof TotemBlockType) { // winning interaction

                    int[] belowPosition = {cell.x, cell.y-1, cell.z};
                    int[] belowBelowPosition = {belowPosition[0], belowPosition[1]-1, belowPosition[2]};

                    byte belowBlockID = world.getVoxelWorld().getBlock(belowPosition[0], belowPosition[1], belowPosition[2]);
                    VoxelBlockType belowType = world.getVoxelWorld().getPalette().get(belowBlockID);
                    byte belowBelowBlockID = world.getVoxelWorld().getBlock(belowBelowPosition[0], belowBelowPosition[1], belowBelowPosition[2]);
                    VoxelBlockType belowBelow = world.getVoxelWorld().getPalette().get(belowBelowBlockID);

                    if (belowBlockID == VoxelPalette.CRYSTAL_CORE_ID && belowBelowBlockID == VoxelPalette.METAL_BLOCK_ID) {
                        System.out.println("gone through first check of blocks");
                        HashMap<String,VoxelBlockType> lowerLowerSurr = world.getVoxelWorld().checkSurroundings(belowBelowPosition[0], belowBelowPosition[1], belowBelowPosition[2], 2, "metal");
                        if (lowerLowerSurr.size() >= 4) {
                            System.out.println("second area check");
                            System.out.println("Bro just won the game congrats");
                            getStateManager().getState(HudAppState.class).stopTimer();
                            getStateManager().cleanup();
                        }
                    }

                }

                System.out.println("Interacted with voxel at " + cell.x + "," + cell.y + "," + cell.z);
            });
        }
    }

    public GameObject findRegistered(Spatial s) {
        Spatial cur = s;
        while (cur != null) {
            GameObject obj = renderIndex.lookup(cur);
            if (obj != null) return obj;
            cur = cur.getParent();
        }
        return null;
    }

    @Override
    protected void cleanup(Application app) { }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}
