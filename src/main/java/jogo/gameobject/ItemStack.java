package jogo.gameobject;

import jogo.gameobject.item.BlockType;
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
        if (item instanceof BlockType) {
            this.isBlock = true;
        }
    }

    public void setItem(Item item) {
        if (item == this.item) {
            throw new IllegalArgumentException("Item is already set to the given item");
        }
        this.item = item;
    }

    public void addToStack(byte amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive");
        } else if (this.stack + amount > this.item.maxStack()) {
            this.stack = this.item.maxStack();
            System.out.println("Stack Maxed");
            throw new IllegalArgumentException("Stack Maxed");
        }
        this.stack += amount;
    }

    public void removeFromStack(byte amount) {
        if (amount >= 0) {
            throw new IllegalArgumentException("Amount to take must be positive");
        } else if (this.stack - amount < 0) {
            this.stack = 0;
            System.out.println("Stack Empty");
            return;
        }
        this.stack -= amount;
    }

    public Item getItem() {
        return item;
    }

    public byte getStack() {
        return stack;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public boolean isMaxed() {
        return stack >= item.maxStack();
    }

}
