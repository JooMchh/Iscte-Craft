package jogo.gameobject.Crafting;

import jogo.gameobject.Inventory.Inventory;
import jogo.gameobject.Inventory.ItemStack;
import jogo.gameobject.item.BlockItem;
import jogo.gameobject.item.Item;
import jogo.gameobject.item.MetalScrap;
import jogo.voxel.VoxelPalette;

import java.util.ArrayList;
import java.util.List;

public class Crafting {
    private List<Recipe> recipes = new ArrayList<>();
    private VoxelPalette palette = VoxelPalette.defaultPalette();

    public Crafting() {
        List<ItemStack> neededForMetalBlock = new ArrayList<>(); // recipe para fazer metal block
        neededForMetalBlock.add(new ItemStack(new MetalScrap(), 4));
        recipes.add(new Recipe("1 Metal Block",
                neededForMetalBlock,
                new ItemStack(
                        new BlockItem(palette.get(VoxelPalette.METAL_BLOCK_ID).getName(), VoxelPalette.METAL_BLOCK_ID),
                        1
                    )
                )
            );

        List<ItemStack> neededForPlank = new ArrayList<>(); // recipe para fazer metal block
        neededForPlank.add(new ItemStack(new BlockItem(palette.get(VoxelPalette.WOOD_ID).getName(), VoxelPalette.WOOD_ID), 1));
        recipes.add(new Recipe("2 Wooden Planks",
                        neededForPlank,
                        new ItemStack(
                                new BlockItem(palette.get(VoxelPalette.WOOD_PLANK_ID).getName(), VoxelPalette.WOOD_PLANK_ID),
                                2
                        )
                )
        );
    }

    public boolean craft(Recipe recipe, Inventory inventory) {

        for (ItemStack stack : recipe.getNeeded()) { // check loop para verificar se existe oq é preciso
            if (!inventory.hasItem(stack.getItem().getName(), stack.getStack())) {
                System.out.println("Crafting: Not enough resources to craft" + recipe.getName());
                return false;
            }
        }

        for (ItemStack stack : recipe.getNeeded()) { // set loop para proceder
            inventory.removeSpecificItem(stack.getItem(), stack.getStack());
        }

        for (int i = 0; i < recipe.getReward().getStack(); i++) { // loop para passar os rewards
            inventory.setSlot(0, recipe.getReward().getItem());
        }

        System.out.println("Crafting: successfully crafted " + recipe.getName());
        return true;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }
}
