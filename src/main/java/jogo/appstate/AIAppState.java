package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import jogo.AI.ActiveAI;
import jogo.AI.Allies.Ally;
import jogo.AI.Enemies.Enemy;
import jogo.engine.GameRegistry;
import jogo.framework.math.Vec3;
import jogo.gameobject.character.*;
import jogo.gameobject.character.Character;
import jogo.gameobject.item.Item;
import jogo.voxel.VoxelBlockType;
import jogo.voxel.VoxelPalette;
import jogo.voxel.VoxelWorld;
import jogo.voxel.blocks.HazardType;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class AIAppState extends BaseAppState { // insane class para gerir e spawnar os npcs (enemy ally etc)
    // variáveis importantes
    private Node rootNode;
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;
    private WorldAppState world;
    private VoxelPalette palette;
    private PlayerAppState playerAppState;
    private GameRegistry registry;

    // active npc list com os spawned npcs (enemy ally etc)
    private List<ActiveAI> activeAIs = new ArrayList<>();

    public AIAppState(Node rootNode, AssetManager assetManager, PhysicsSpace physicsSpace, WorldAppState world, GameRegistry registry) {
        this.rootNode = rootNode;
        this.physicsSpace = physicsSpace;
        this.assetManager = assetManager;
        this.world = world;
        this.registry = registry;
    }

    @Override
    protected void initialize(Application app) {
        this.playerAppState = getStateManager().getState(PlayerAppState.class);
        palette = world.getVoxelWorld().getPalette();
    }

    @Override
    protected void cleanup(Application app) {
        for (ActiveAI ai : activeAIs) {
            ai.cleanup(physicsSpace);
        }
        activeAIs.clear();
    }

    @Override
    public void update(float tpf) {
        if (playerAppState != null) {

            for (int i = 0; i < activeAIs.size(); i++) {
                ActiveAI ai = activeAIs.get(i);

                if (ai.getCharacter() == null) {
                    continue;
                }

                // OnDeath
                if (ai.getCharacter().isDead()) {
                    killAi(ai);
                    activeAIs.remove(i);
                    i--;
                    continue;
                }

                // npc Position
                Vector3f aiPos = getAIPosition(ai);
                int ax = (int) Math.floor(aiPos.x);
                int ay = (int) Math.floor(aiPos.y);
                int az = (int) Math.floor(aiPos.z);

                // get currentVoxelWorld
                VoxelWorld voxelWorld = world.getVoxelWorld();

                // get surrounding blocks and stepping block
                HashMap<String, VoxelBlockType> surroundingBlocks = voxelWorld.checkSurroundings(ax, ay, az, 1, null);
                VoxelBlockType steppingBlock = palette.get(voxelWorld.getBlock(ax, ay, az));

                ai.updateAI(playerAppState, activeAIs);

                // update Hazards (if npcs isnt immune)
                if (!(ai.getCharacter() instanceof HazardImmune)) {
                    updateHazardBlocks(ai, surroundingBlocks);
                    if (steppingBlock instanceof HazardType stepHazard) {
                        System.out.println("Stepped on hazard: " + steppingBlock.getName());
                        stepHazard.onStep(ai);
                    } else if (ai.getWalkSpeed() != ai.getSetWalkSpeed() || ai.getJumpForce().y != ai.getSetJumpForce() ) {
                        ai.resetWalkSpeed();
                        ai.resetJumpForce();
                    }
                }

                // update render position?
                ai.getCharacter().setPosition(new Vec3(getAIPosition(ai).x, getAIPosition(ai).y, getAIPosition(ai).z));

                // update internal character clocks
                ai.getCharacter().updateInternalTimer(tpf);

            }

        }
    }

    private void killAi(ActiveAI ai) {
        Vector3f aiPosition = getAIPosition(ai);
        for (Item drop : ai.getDrops()) {
            drop.setPosition(new Vec3(aiPosition.x, aiPosition.y, aiPosition.z));
            registry.add(drop);
        }
        ai.cleanup(physicsSpace);
        registry.remove(ai.getCharacter());
        System.out.println("an AI has died");
    }

    private Vector3f getAIPosition(ActiveAI ai) {
        Vector3f aiPos = ai.getAiNode().getWorldTranslation();
        return aiPos;
    }

    private void updateHazardBlocks(ActiveAI ai,HashMap<String, VoxelBlockType> surroundingBlocks) {
        for (String blockDirectionKey : surroundingBlocks.keySet()) {
            VoxelBlockType surroundingBlock = surroundingBlocks.get(blockDirectionKey);
            String[] blockDirectionKeySplit = blockDirectionKey.split(" : ");
            String blockDirection = blockDirectionKeySplit[0];

            if (surroundingBlock instanceof HazardType hazardBlock) {
                //System.out.println("AI Near contact hazard: " + surroundingBlock.getName() + blockDirection);
                hazardBlock.onContact(ai);
            }
        }
    }

    // spawn functions

    public void spawnSlimeEnemy(Vector3f position) {
        Character slimeEnemy = new SlimeEnemy();
        slimeEnemy.setPosition(new Vec3(position.x, position.y, position.z));
        registry.add(slimeEnemy);
        ActiveAI ai = new Enemy(slimeEnemy, rootNode, physicsSpace, position);
        activeAIs.add(ai);
        System.out.println("AIAppState spawn: Spawned a new Slime Enemy at" + position.x + "," + position.y + "," + position.z);
    }

    public void spawnCaveSlimeEnemy(Vector3f position) {
        Character strongerSlimeEnemy = new StrongerSlimeEnemy();
        strongerSlimeEnemy.setPosition(new Vec3(position.x, position.y, position.z));
        registry.add(strongerSlimeEnemy);
        ActiveAI ai = new Enemy(strongerSlimeEnemy, rootNode, physicsSpace, position);
        activeAIs.add(ai);
        System.out.println("AIAppState spawn: Spawned a new Cave Slime Enemy at" + position.x + "," + position.y + "," + position.z);
    }

    public void spawnWoodGolemAlly(Vector3f position) {
        Character woodGolemAlly = new WoodGolemAlly();
        woodGolemAlly.setPosition(new Vec3(position.x, position.y, position.z));
        registry.add(woodGolemAlly);
        ActiveAI ai = new Ally(woodGolemAlly, rootNode, physicsSpace, position);
        activeAIs.add(ai);
        System.out.println("AIAppState spawn: Spawned a Wood Golem Ally at" + position.x + "," + position.y + "," + position.z);
    }

    //

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }

}
