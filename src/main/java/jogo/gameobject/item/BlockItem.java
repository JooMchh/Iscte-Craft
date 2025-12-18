package jogo.gameobject.item;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import jogo.appstate.CharacterType;
import jogo.appstate.PlayerAppState;
import jogo.gameobject.Inventory.Inventory;
import jogo.voxel.VoxelPalette;

public class BlockItem extends Item implements BlockType {
    private byte blockId;
    private final VoxelPalette voxelPalette = new VoxelPalette().defaultPalette();

    public BlockItem(String name, byte blockId) {
        super(name);
        this.blockId = blockId;
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
    public byte getBlockId() {
        return blockId;
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Geometry g = new Geometry(name + "" + Math.random(), new Box(new Vector3f(0,.3f,0), .3f, .3f, .3f));
        Material m = voxelPalette.get(blockId).getMaterial(assetManager);
        g.setMaterial(m);
        return g;
    }
}
