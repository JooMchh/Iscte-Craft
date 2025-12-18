package jogo.gameobject;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import jogo.framework.math.Vec3;

/**
 * Engine-neutral base for all game objects used by students.
 * Stores only identity and logical position; rendering/physics is handled by engine AppStates.
 */
public abstract class GameObject {
    protected final String name;
    protected Vec3 position = new Vec3();

    protected GameObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 pos) {
        this.position.set(pos);
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public Geometry render(AssetManager assetManager) { // o 'T O D O'
        return null;
    }
}
