package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.light.PointLight;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import jogo.gameobject.Inventory.Inventory;
import jogo.gameobject.character.Player;
import jogo.voxel.VoxelBlockType;
import jogo.voxel.VoxelPalette;
import jogo.voxel.VoxelWorld;
import jogo.voxel.blocks.HazardType;

import java.util.HashMap;

public class PlayerAppState extends BaseAppState implements CharacterType {

    private final Node rootNode;
    private final AssetManager assetManager;
    private final Camera cam;
    private final InputAppState input;
    private final PhysicsSpace physicsSpace;
    private final WorldAppState world;

    private Node playerNode;
    private BetterCharacterControl characterControl;
    private Player player;
    private Inventory inventory;
    private VoxelPalette palette;

    // view angles
    private float yaw = 0f;
    private float pitch = 0f;

    // tuning
    private final float SET_MOVE_SPEED = 8.0f;
    private final float SET_JUMP_FORCE = 400f;
    private float moveSpeed = SET_MOVE_SPEED; // m/s
    private float sprintMultiplier = 1.7f;
    private float mouseSensitivity = 30f; // degrees per mouse analog unit
    private float eyeHeight = 1.7f;

    private Vector3f spawnPosition = new Vector3f(25.5f, 12f, 25.5f);
    private PointLight playerLight;
    private boolean previousInLiquid = false;
    private byte previousBlockInPlayer = 0;
    private int previousPlayerHealth = 100;

    public PlayerAppState(Node rootNode, AssetManager assetManager, Camera cam, InputAppState input, PhysicsSpace physicsSpace, WorldAppState world) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.cam = cam;
        this.input = input;
        this.physicsSpace = physicsSpace;
        this.world = world;
        world.registerPlayerAppState(this);
    }

    @Override
    protected void initialize(Application app) {
        // query world for recommended spawn now that it should be initialized
        if (world != null) {
            spawnPosition = world.getRecommendedSpawnPosition();
        }

        playerNode = new Node("Player");
        rootNode.attachChild(playerNode);

        // Engine-neutral player entity (no engine visuals here)
        player = new Player();
        inventory = player.getInventory();

        // BetterCharacterControl(radius, height, mass)
        characterControl = new BetterCharacterControl(0.42f, 1.8f, 80f);
        characterControl.setGravity(new Vector3f(0, -24f, 0));
        characterControl.setJumpForce(new Vector3f(0, SET_JUMP_FORCE, 0));
        playerNode.addControl(characterControl);
        physicsSpace.add(characterControl);

        // Local light source that follows the player's head
        playerLight = new PointLight();
        playerLight.setColor(new com.jme3.math.ColorRGBA(0.6f, 0.55f, 0.5f, 1f));
        playerLight.setRadius(12f);
        rootNode.addLight(playerLight);

        // Spawn at recommended location
        respawn();

        // initialize camera
        cam.setFrustumPerspective(60f, (float) cam.getWidth() / cam.getHeight(), 0.05f, 500f);
        // Look slightly downward so ground is visible immediately
        this.pitch = 0.35f;
        applyViewToCamera();

        // setup palette
        palette = world.getVoxelWorld().getPalette();
    }

    @Override
    public void update(float tpf) {
        // player eye Position
        Vector3f eyePos = playerNode.getWorldTranslation().add(0, eyeHeight, 0);
        Vector3f plrPos = playerNode.getWorldTranslation().add(0, 0, 0);
        int ex = (int) Math.floor(eyePos.x);
        int ey = (int) Math.floor(eyePos.y);
        int ez = (int) Math.floor(eyePos.z);
        int px = (int) Math.floor(plrPos.x);
        int py = (int) Math.floor(plrPos.y);
        int pz = (int) Math.floor(plrPos.z);

        // get currentVoxelWorld
        VoxelWorld voxelWorld = world.getVoxelWorld();

        // get surrounding blocks and stepping block
        HashMap<String, VoxelBlockType> surroundingBlocks = voxelWorld.checkSurroundings(px, py, pz, 1, null);
        VoxelBlockType steppingBlock = palette.get(voxelWorld.getBlock(px, py, pz));

        // get currentHud
        HudAppState hud = getStateManager().getState(HudAppState.class);

        // respawn on request
        if (input.consumeRespawnRequested()) {
            // refresh spawn from world in case terrain changed
            if (world != null) spawnPosition = world.getRecommendedSpawnPosition();
            respawn();
        }

        // inventory control requests
        if (input.consumeInvLeftRequested()) {
            inventory.setSelectedSlot(inventory.getSelectedSlot()-1);
        }
        if (input.consumeInvRightRequested()) {
            inventory.setSelectedSlot(inventory.getSelectedSlot()+1);
        }

        // self damage on request
        if (input.consumeDamageRequested()) {
            if (player != null) player.damage(10);
        }

        // pause controls if mouse not captured
        if (!input.isMouseCaptured()) {
            characterControl.setWalkDirection(Vector3f.ZERO);
            // keep light with player even when paused
            if (playerLight != null) playerLight.setPosition(playerNode.getWorldTranslation().add(0, eyeHeight, 0));
            applyViewToCamera();
            return;
        }

        // handle mouse look
        Vector2f md = input.consumeMouseDelta();
        if (md.lengthSquared() != 0f) {
            float degX = md.x * mouseSensitivity;
            float degY = md.y * mouseSensitivity;
            yaw -= degX * FastMath.DEG_TO_RAD;
            pitch -= degY * FastMath.DEG_TO_RAD;
            pitch = FastMath.clamp(pitch, -FastMath.HALF_PI * 0.99f, FastMath.HALF_PI * 0.99f);
        }

        // movement input in XZ plane based on camera yaw
        Vector3f wish = input.getMovementXZ();
        Vector3f dir = Vector3f.ZERO;
        if (wish.lengthSquared() > 0f) {
            dir = computeWorldMove(wish).normalizeLocal();
        }
        float speed = moveSpeed * (input.isSprinting() ? sprintMultiplier : 1f);
        characterControl.setWalkDirection(dir.mult(speed));

        // jump
        if (input.consumeJumpRequested() && characterControl.isOnGround()) {
            characterControl.jump();
        }

        // place camera at eye height above physics location
        applyViewToCamera();

        // update light to follow head
        if (playerLight != null) playerLight.setPosition(eyePos);

        // Handle Player Iframes

        // update Liquid
        boolean inLiquid = false;

        byte blockInPlayerEyes = voxelWorld.getBlock(ex, ey, ez);
        if (blockInPlayerEyes == palette.WATER_ID || blockInPlayerEyes == palette.POISON_ID || blockInPlayerEyes == palette.LAVA_ID) {
            inLiquid = true;
        }

        if (inLiquid != previousInLiquid || blockInPlayerEyes != previousBlockInPlayer) {
            previousInLiquid = inLiquid;
            previousBlockInPlayer = blockInPlayerEyes;
            hud.updateLiquidEffect(blockInPlayerEyes, inLiquid);
        }

        // update Health + if 0 then call Death
        int currentHealth = player.getHealth();

        if (currentHealth != previousPlayerHealth && player != null) {
            previousPlayerHealth = currentHealth;

            if (player.isDead()) {
                respawn();
            }

            if (hud != null) {
                hud.updateHealthBar(currentHealth, player.getMAX_HEALTH());
            };
        }

        // update Hazard blocks
        updateHazardBlocks(surroundingBlocks);

        // update step Hazard block
        if (steppingBlock instanceof HazardType stepHazard) {
            System.out.println("Stepped on hazard: " + steppingBlock.getName());
            stepHazard.onStep(this);
        } else if (moveSpeed != SET_MOVE_SPEED || characterControl.getJumpForce(null).y != SET_JUMP_FORCE ) {
            resetWalkSpeed();
            resetJumpForce();
        }

        // update internal character clocks
        if (player != null) {
            player.updateInternalTimer(tpf);
        }

    }

    // method to simplify hazard block updating
    private void updateHazardBlocks(HashMap<String, VoxelBlockType> surroundingBlocks) {
        for (String blockDirectionKey : surroundingBlocks.keySet()) {
            VoxelBlockType surroundingBlock = surroundingBlocks.get(blockDirectionKey);
            String[] blockDirectionKeySplit = blockDirectionKey.split(" : ");
            String blockDirection = blockDirectionKeySplit[0];

            if (surroundingBlock instanceof HazardType hazardBlock) {
                System.out.println("Near contact hazard: " + surroundingBlock.getName() + blockDirection);
                hazardBlock.onContact(this);
            }
        }
    }

    // characterAppState implements

    @Override
    public void characterDamage(int damage) {
        if (player != null) {
            player.damage(damage);
        }
    }

    @Override
    public void resetJumpForce() {
        System.out.println("reset speed");
        if (characterControl != null) {
            characterControl.setJumpForce(new Vector3f(0, SET_JUMP_FORCE,0));
        }
    }

    @Override
    public void resetWalkSpeed() {
        moveSpeed = SET_MOVE_SPEED;
    }

    @Override
    public void setWalkSpeed(float walkSpeed) {
        moveSpeed = walkSpeed;
        System.out.println("set walkspeed");
    }

    @Override
    public void setJumpForce(float jumpForce) {
        if (characterControl != null) {
            this.characterControl.setJumpForce(new Vector3f(0, jumpForce, 0));
        }
    }

    //


    public Inventory getInventory() {
        return inventory;
    }

    private void onDeath() {
        player.setHealth(100);
        player.setMAX_HEALTH(100);
        System.out.println("PlayerAppState onDeath: Player has died!");
    }

    private void respawn() {
        characterControl.setWalkDirection(Vector3f.ZERO);
        characterControl.warp(spawnPosition);
        onDeath();
        // Reset look
        this.pitch = 0.35f;
        applyViewToCamera();
    }

    public Player getPlayer() {
        return player;
    }

    private Vector3f computeWorldMove(Vector3f inputXZ) {
        // Build forward and left unit vectors from yaw
        float sinY = FastMath.sin(yaw);
        float cosY = FastMath.cos(yaw);
        Vector3f forward = new Vector3f(-sinY, 0, -cosY); // -Z when yaw=0
        Vector3f left = new Vector3f(-cosY, 0, sinY);     // -X when yaw=0
        return left.mult(inputXZ.x).addLocal(forward.mult(inputXZ.z));
    }

    private void applyViewToCamera() {
        // Character world location (spatial is synced by control)
        Vector3f loc = playerNode.getWorldTranslation().add(0, eyeHeight, 0);
        cam.setLocation(loc);
        cam.setRotation(new com.jme3.math.Quaternion().fromAngles(pitch, yaw, 0f));
    }

    @Override
    protected void cleanup(Application app) {
        if (playerNode != null) {
            if (characterControl != null) {
                physicsSpace.remove(characterControl);
                playerNode.removeControl(characterControl);
                characterControl = null;
            }
            playerNode.removeFromParent();
            playerNode = null;
        }
        if (playerLight != null) {
            rootNode.removeLight(playerLight);
            playerLight = null;
        }
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }

    public void refreshPhysics() {
        if (characterControl != null) {
            physicsSpace.remove(characterControl);
            physicsSpace.add(characterControl);
        }
    }
}
