package jogo.voxel.blocks;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture2D;
import jogo.voxel.VoxelBlockType;

public class WaterBlockType extends VoxelBlockType {
    public WaterBlockType() {
        super("water");
    }
    // isSolid() inherits true from base

    @Override
    public boolean canCollide() { return false; }

    @Override
    public Material getMaterial(AssetManager assetManager) {
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/Waterblock.png");
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setTexture("DiffuseMap", tex);
        m.setBoolean("UseMaterialColors", true);
        m.setColor("Diffuse", ColorRGBA.White);
        m.setColor("Specular", ColorRGBA.White.mult(0.02f)); // reduced specular
        m.setFloat("Shininess", 32f); // tighter, less intense highlight
        return m;
    }
}
