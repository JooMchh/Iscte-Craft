package jogo.gameobject.character;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import jogo.gameobject.Inventory.Inventory;

public class Player extends Character {
    private Inventory inventory;

    public Player() {
        super("Player");
        this.inventory = new Inventory();
    }

    public Inventory getInventory() {
        return inventory;
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
