package jogo.gameobject;

import jogo.gameobject.item.Item;
import jogo.voxel.VoxelPalette;

public class ItemStack {
    private VoxelPalette palette;
    private Item item;
    private byte stack;
    private boolean isBlock;

    public ItemStack(Item item, byte stack) {
        this.item = item;
        this.stack = stack;
    }
}
