package jogo.gameobject.Inventory;

import jogo.gameobject.item.Item;

public class Inventory {
    private ItemStack[] slots;
    private int selectedSlot = 0;
    private final int CAPACITY = 15;

    public Inventory() {
        slots = new ItemStack[CAPACITY];
    }

    public void setSlot(int slot, Item item) {
        if (slot < 0 || slot >= CAPACITY) {
            System.out.println("Inventory: Attempted to set invalid slot: " + slot + " inventory might be maxed.");
            return;
        };
        if (item == null) {
            slots[slot] = null;
        } else {
            ItemStack slotStack = slots[slot];
            if (slotStack == null) {
                slots[slot] = new ItemStack(item, 1);
                System.out.println("Inventory: " + slot + " is empty, filled with item stack");
            } else if (slotStack.isMaxed() || slotStack.getItem().getName() != item.getName()) {
                this.setSlot((slot+1), item);
                System.out.println("Inventory: " + slot + " is full, attempting to move on to next slot " + (slot + 1));
            } else if (slotStack.getItem().getName() == item.getName()) {
                slots[slot].addToStack(1);
                System.out.println("Inventory: " + slot + " isn't full, added to the stack");
            }
        }

    }

    public Item subtractSlot(int slot, int amount) {
        if (slot < 0 || slot >= CAPACITY) {
            System.out.println("Inventory: Attempted to subtract from invalid slot: " + slot);
            return null;
        };
        ItemStack slotStack = slots[slot];
        if (slotStack == null) {
            System.out.println("Inventory: " + slot + " attempted to subtract from empty slot");
            return null;
        } else {
            Item removedItem = slotStack.getItem();
            slotStack.removeFromStack(amount);
            if (slotStack.isEmpty()) {
                slots[slot] = null;
                System.out.println("Inventory: " + slot + " is empty, setting to null");
            }
            return removedItem;
        }

    }

    public boolean hasItem(String name, int amount) {
        int count = 0;
        for (ItemStack stack : slots) {
            if (stack != null && stack.getItem().getName() == name ) {
                count += stack.getStack();
            }
        }
        return count >= amount;
    }

    public void removeSpecificItem(Item item, int amount) {
        int toRemove = amount;

        for (int i = 0; i < CAPACITY; i++) {
            ItemStack slotStack = getItemStack(i);
            if ( slotStack != null && slotStack.getItem().getName() == item.getName() ) {
                int stack = slotStack.getStack();

                if (stack > toRemove) {
                    slotStack.removeFromStack(toRemove);
                } else {
                    toRemove -= stack;
                    slots[i] = null;
                }

                if (toRemove <= 0) { return; }
            }
        }
    }

    public void setSelectedSlot(int slot) {
        if (slot < 0 || slot >= CAPACITY) {
            selectedSlot = 0;
            System.out.println("Inventory: Attempted to select invalid slot: " + slot + ", reverted to slot 0");
            return;
        };
        selectedSlot = slot;
    }

    public ItemStack getSelectedItemStack() {
        return slots[selectedSlot];
    }

    public ItemStack getItemStack(int slot) {
        return slots[slot];
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public int getCAPACITY() { return CAPACITY; }
}
