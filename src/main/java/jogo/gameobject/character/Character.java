package jogo.gameobject.character;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import jogo.gameobject.GameObject;

public abstract class Character extends GameObject {

    protected Character(String name) {
        super(name);
    }

    // Example state hooks students can extend
    protected int MAX_HEALTH = 100;
    protected int health = MAX_HEALTH;
    protected float internalDamageTickTimer = 0f;
    protected final float iFrames = 0.25f;

    public int getHealth() { return health; }
    public int getMAX_HEALTH() { return MAX_HEALTH; }
    public void setHealth(int health) { this.health = health; }
    public void setMAX_HEALTH(int max_health) { this.MAX_HEALTH = max_health; }

    public void damage(int damage) {
        System.out.println("Character damage: damage method called");
        if (internalDamageTickTimer < iFrames) { return; }
        internalDamageTickTimer = 0;
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        } else if (this.health > MAX_HEALTH) {
            this.health = MAX_HEALTH;
        }
        System.out.println("Character damage: character " + getName() + " took " + damage + " damage!");
    }

    public void updateInternalTimer(float tpf) {
        internalDamageTickTimer += tpf;
    }

    public boolean cantHit() {
        return  (internalDamageTickTimer < iFrames);
    }

    public boolean isDead() {
        if (this.health <= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Geometry g = new Geometry(name, new Cylinder(16, 16, 0.35f, 1.4f, true));
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors", true);
        m.setColor("Diffuse", ColorRGBA.Green);
        m.setColor("Specular", ColorRGBA.White.mult(0.1f));
        m.setFloat("Shininess", 8f);
        g.setMaterial(m);
        return g;
    }

}
