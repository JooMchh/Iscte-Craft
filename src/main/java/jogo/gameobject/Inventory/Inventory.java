package jogo.gameobject.Inventory;

import jogo.gameobject.item.Item;

public class Inventory {
    private ItemStack[] slots;
    private byte selectedSlot = 0;
    private final int CAPACITY = 45;

    public Inventory() {
        slots = new ItemStack[CAPACITY];
    }

    public void setSlot(byte slot, Item item) {
        if (slot < 0 || slot >= CAPACITY) {
            System.out.println("Attempted to set invalid slot: " + slot + " inventory might be maxed.");
            return;
        };
        if (item == null) {
            slots[slot] = null;
        } else {
            ItemStack slotStack = slots[slot];
            if (slotStack == null) {
                slots[slot] = new ItemStack(item, (byte) 1);
            } else if (slotStack.isMaxed()) {
                this.setSlot( (byte) (slot+1), item);
            } else if (slotStack.getItem() == item) {
                slots[slot].addToStack((byte) 1);
            }
        }

    }

    public void setSelectedSlot(byte slot) {
        if (slot < 0 || slot >= CAPACITY) {
            System.out.println("Attempted to select invalid slot: " + slot);
        };
        selectedSlot = slot;
    }

    public ItemStack getSelectedItemStack() {
        return slots[selectedSlot];
    }

    public byte getSelectedSlot() {
        return selectedSlot;
    }

    public boolean isHotBarSlot(byte slot) {
        return slot >= 0 && slot < 9;
    }


}
