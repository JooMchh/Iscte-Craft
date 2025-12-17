package jogo.gameobject.Inventory;

import jogo.gameobject.item.BlockType;
import jogo.gameobject.item.Item;
import jogo.voxel.VoxelPalette;

public class ItemStack {
    private VoxelPalette palette;
    private Item item;
    private int stack;
    private boolean isBlock;

    public ItemStack(Item item, int stack) {
        this.item = item;
        this.stack = stack;
        if (item instanceof BlockType) {
            this.isBlock = true;
        }
    }

    public void setItem(Item item) {
        if (item == this.item) {
            System.out.println("Item is already set to the given item");
            return;
        }
        this.item = item;
    }

    public void addToStack(int amount) {
        if (amount <= 0) {
            System.out.println("Amount to add must be positive");
            return;
        } else if (this.stack + amount > this.item.maxStack()) {
            this.stack = this.item.maxStack();
            System.out.println("Stack Maxed");
        }
        this.stack += amount;
    }

    public void removeFromStack(int amount) {
        if (amount <= 0) {
            System.out.println("Amount to take must be positive");
            return;
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

    public int getStack() {
        return stack;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public boolean isMaxed() {
        return stack >= item.maxStack();
    }

    public boolean isEmpty() { return stack == 0; }

}
