package jogo.gameobject;

import jogo.gameobject.item.Item;

import java.util.ArrayList;

public class Inventory {
    private ItemStack[] slots;
    private byte selectedSlot = 0;
    private final int CAPACITY = 45;

    public Inventory() {
        slots = new ItemStack[CAPACITY];
    }

    public void setSlot(byte slot, Item item) {
        return;
    }

    public void setSelectedSlot(byte slot) {
        if (slot < 0 || slot >= CAPACITY) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        };
        selectedSlot = slot;
    }

    public ItemStack getSelectedItemStack() {
        return slots[selectedSlot];
    }

    public byte getSelectedSlot() {
        return selectedSlot;
    }
}
