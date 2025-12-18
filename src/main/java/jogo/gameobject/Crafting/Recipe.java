package jogo.gameobject.Crafting;

import jogo.gameobject.Inventory.ItemStack;
import jogo.gameobject.item.Item;

import java.util.HashMap;
import java.util.List;

public class Recipe {
    private List<ItemStack> needed;
    private String name;
    private ItemStack reward;

    public Recipe(String name, List<ItemStack> needed, ItemStack reward) {
        this.name = name;
        this.needed = needed;
        this.reward = reward;
    }

    public List<ItemStack> getNeeded() {
        return needed;
    }

    public String getName() {
        return name;
    }

    public ItemStack getReward() {
        return reward;
    }

    @Override
    public String toString() {
        String toString = "Recipe: " + name + "; needs: \n";
        for (ItemStack itemStack : needed) {
            toString += "   " + itemStack.getStack() + "-" + itemStack.getItem().getName() + "; \n";
        }
        return toString;
    }


}
