package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class InputAppState extends BaseAppState implements ActionListener, AnalogListener {

    private boolean forward, backward, left, right;
    private boolean sprint;
    private volatile boolean jumpRequested;
    private volatile boolean breakRequested;
    private volatile boolean placeRequested;
    private volatile boolean toggleShadingRequested;
    private volatile boolean respawnRequested;
    private volatile boolean interactRequested;
    private volatile boolean damageRequested;
    private volatile boolean invLeftRequested;
    private volatile boolean invRightRequested;
    private volatile boolean dropRequested;
    private volatile boolean craft1Requested;
    private volatile boolean craft2Requested;
    private volatile boolean craft3Requested;
    private volatile boolean craft4Requested;
    private volatile boolean craft5Requested;
    private float mouseDX, mouseDY;
    private boolean mouseCaptured = true;

    @Override
    protected void initialize(Application app) {
        var im = app.getInputManager();
        // Movement keys
        im.addMapping("MoveForward", new KeyTrigger(KeyInput.KEY_W));
        im.addMapping("MoveBackward", new KeyTrigger(KeyInput.KEY_S));
        im.addMapping("MoveLeft", new KeyTrigger(KeyInput.KEY_A));
        im.addMapping("MoveRight", new KeyTrigger(KeyInput.KEY_D));
        im.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        im.addMapping("Sprint", new KeyTrigger(KeyInput.KEY_LSHIFT));
        // Mouse look
        im.addMapping("MouseX+", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        im.addMapping("MouseX-", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        im.addMapping("MouseY+", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        im.addMapping("MouseY-", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        // Toggle capture (use TAB, ESC exits app by default)
        im.addMapping("ToggleMouse", new KeyTrigger(KeyInput.KEY_TAB));
        // Break voxel (left mouse)
        im.addMapping("Break", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        // Place voxel (right mouse)
        im.addMapping("Place", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        // Toggle shading (L)
        im.addMapping("ToggleShading", new KeyTrigger(KeyInput.KEY_L));
        // Respawn (R)
        im.addMapping("Respawn", new KeyTrigger(KeyInput.KEY_R));
        // Interact (E)
        im.addMapping("Interact", new KeyTrigger(KeyInput.KEY_E));
        // Self-Damage (K)
        im.addMapping("Damage", new KeyTrigger(KeyInput.KEY_K));
        // Scroll inv Left (scroll wheel up)
        im.addMapping("invLeft", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        // Scroll inv Left (scroll wheel up)
        im.addMapping("invRight", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        // Drop item
        im.addMapping("dropItem", new KeyTrigger(KeyInput.KEY_Q));
        // Craft Recipe 1
        im.addMapping("Craft1", new KeyTrigger(KeyInput.KEY_1));
        // Craft Recipe 2
        im.addMapping("Craft2", new KeyTrigger(KeyInput.KEY_2));
        // Craft Recipe 3
        im.addMapping("Craft3", new KeyTrigger(KeyInput.KEY_3));
        // Craft Recipe 4
        im.addMapping("Craft4", new KeyTrigger(KeyInput.KEY_4));
        // Craft Recipe 4
        im.addMapping("Craft5", new KeyTrigger(KeyInput.KEY_5));

        im.addListener(this, "MoveForward", "MoveBackward", "MoveLeft", "MoveRight", "Jump", "Sprint", "ToggleMouse", "Break", "Place", "ToggleShading", "Respawn", "Interact", "Damage", "Fullscreen", "invLeft", "invRight", "dropItem", "Craft1", "Craft2", "Craft3", "Craft4", "Craft5");
        im.addListener(this, "MouseX+", "MouseX-", "MouseY+", "MouseY-");
    }

    @Override
    protected void cleanup(Application app) {
        var im = app.getInputManager();
        im.deleteMapping("MoveForward");
        im.deleteMapping("MoveBackward");
        im.deleteMapping("MoveLeft");
        im.deleteMapping("MoveRight");
        im.deleteMapping("Jump");
        im.deleteMapping("Sprint");
        im.deleteMapping("MouseX+");
        im.deleteMapping("MouseX-");
        im.deleteMapping("MouseY+");
        im.deleteMapping("MouseY-");
        im.deleteMapping("ToggleMouse");
        im.deleteMapping("Break");
        im.deleteMapping("Place");
        im.deleteMapping("ToggleShading");
        im.deleteMapping("Respawn");
        im.deleteMapping("Interact");
        im.deleteMapping("Damage");
        im.deleteMapping("invLeft");
        im.deleteMapping("invRight");
        im.deleteMapping("dropItem");
        im.deleteMapping("Craft1");
        im.deleteMapping("Craft2");
        im.deleteMapping("Craft3");
        im.deleteMapping("Craft4");
        im.deleteMapping("Craft5");
        im.removeListener(this);
    }

    @Override
    protected void onEnable() {
        setMouseCaptured(true);
    }

    @Override
    protected void onDisable() { }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case "MoveForward" -> forward = isPressed;
            case "MoveBackward" -> backward = isPressed;
            case "MoveLeft" -> left = isPressed;
            case "MoveRight" -> right = isPressed;
            case "Sprint" -> sprint = isPressed;
            case "Jump" -> {
                if (isPressed) jumpRequested = true;
            }
            case "ToggleMouse" -> {
                if (isPressed) setMouseCaptured(!mouseCaptured);
            }
            case "Break" -> {
                if (isPressed && mouseCaptured) breakRequested = true;
            }
            case "Place" -> {
                if (isPressed && mouseCaptured) placeRequested = true;
            }
            case "ToggleShading" -> {
                if (isPressed) toggleShadingRequested = true;
            }
            case "Respawn" -> {
                if (isPressed) respawnRequested = true;
            }
            case "Interact" -> {
                if (isPressed && mouseCaptured) interactRequested = true;
            }
            case "Damage" -> {
                if (isPressed) damageRequested = true;
            }
            case "invLeft" -> {
                if (isPressed && mouseCaptured) invLeftRequested = true;
            }
            case "invRight" -> {
                if (isPressed && mouseCaptured) invRightRequested = true;
            }
            case "dropItem" -> {
                if (isPressed) dropRequested = true;
            }
            case "Craft1" -> {
                if (isPressed) craft1Requested = true;
            }
            case "Craft2" -> {
                if (isPressed) craft2Requested = true;
            }
            case "Craft3" -> {
                if (isPressed) craft3Requested = true;
            }
            case "Craft4" -> {
                if (isPressed) craft4Requested = true;
            }
            case "Craft5" -> {
                if (isPressed) craft5Requested = true;
            }
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (!mouseCaptured) return;
        switch (name) {
            case "MouseX+" -> mouseDX += value;
            case "MouseX-" -> mouseDX -= value;
            case "MouseY+" -> mouseDY += value;
            case "MouseY-" -> mouseDY -= value;
        }
    }

    public Vector3f getMovementXZ() {
        float fb = (forward ? 1f : 0f) + (backward ? -1f : 0f);
        float lr = (right ? 1f : 0f) + (left ? -1f : 0f);
        return new Vector3f(lr, 0f, -fb); // -fb so forward maps to -Z in JME default
    }

    public boolean isSprinting() {
        return sprint;
    }

    public boolean consumeJumpRequested() {
        boolean jr = jumpRequested;
        jumpRequested = false;
        return jr;
    }

    public boolean consumeBreakRequested() {
        boolean r = breakRequested;
        breakRequested = false;
        return r;
    }

    public boolean consumePlaceRequested() {
        boolean pr = placeRequested;
        placeRequested = false;
        return pr;
    }

    public boolean consumeToggleShadingRequested() {
        boolean r = toggleShadingRequested;
        toggleShadingRequested = false;
        return r;
    }

    public boolean consumeRespawnRequested() {
        boolean r = respawnRequested;
        respawnRequested = false;
        return r;
    }

    public boolean consumeInteractRequested() {
        boolean r = interactRequested;
        interactRequested = false;
        return r;
    }

    public boolean consumeDamageRequested() {
        boolean r = damageRequested;
        damageRequested = false;
        return r;
    }

    public boolean consumeInvLeftRequested() {
        boolean r = invLeftRequested;
        invLeftRequested = false;
        return r;
    }

    public boolean consumeInvRightRequested() {
        boolean r = invRightRequested;
        invRightRequested = false;
        return r;
    }

    public boolean consumeDropRequested() {
        boolean r = dropRequested;
        dropRequested = false;
        return r;
    }

    public boolean consumeCraft1Requested() {
        boolean r = craft1Requested;
        craft1Requested = false;
        return r;
    }

    public boolean consumeCraft2Requested() {
        boolean r = craft2Requested;
        craft2Requested = false;
        return r;
    }

    public boolean consumeCraft3Requested() {
        boolean r = craft3Requested;
        craft3Requested = false;
        return r;
    }

    public boolean consumeCraft4Requested() {
        boolean r = craft4Requested;
        craft4Requested = false;
        return r;
    }

    public boolean consumeCraft5Requested() {
        boolean r = craft5Requested;
        craft5Requested = false;
        return r;
    }

    public Vector2f consumeMouseDelta() {
        Vector2f d = new Vector2f(mouseDX, mouseDY);
        mouseDX = 0f;
        mouseDY = 0f;
        return d;
    }

    public void setMouseCaptured(boolean captured) {
        this.mouseCaptured = captured;
        var im = getApplication().getInputManager();
        im.setCursorVisible(!captured);
        // Clear accumulated deltas when switching state
        mouseDX = 0f;
        mouseDY = 0f;
    }

    public boolean isMouseCaptured() {
        return mouseCaptured;
    }
}
