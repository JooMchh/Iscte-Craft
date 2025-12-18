package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture2D;
import jogo.appstate.PlayerAppState;
import jogo.gameobject.Inventory.Inventory;

public class MetalScrap extends Item {
    public MetalScrap() {
        super("Metal Scrap");
    }

    @Override
    public byte maxStack() { return 64; }

    @Override
    public void onInteract(PlayerAppState playerAppState) {
        Inventory plrInv =  playerAppState.getInventory();
        if (stack > 1) {
            for (int i = 0; i < stack; i++) {
                plrInv.setSlot(0, this);
            }
        } else {
            plrInv.setSlot(0, this);
        }

    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Geometry g = new Geometry(name, new Box(new Vector3f(0,.4f,0), .4f, .15f, .4f));
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/MetalBlock.png");
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors", true);
        m.setTexture("DiffuseMap", tex);
        m.setColor("Diffuse", ColorRGBA.White);
        m.setColor("Specular", ColorRGBA.White.mult(0.1f));
        m.setFloat("Shininess", 50f);
        g.setMaterial(m);
        return g;
    }
}
