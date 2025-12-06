package jogo.gameobject.character;

import jogo.gameobject.Inventory;

public class Player extends Character {
    private Inventory inventory;

    public Player() {
        super("Player");
        this.inventory = new Inventory();
    }

    public Inventory getInventory() {
        return inventory;
    }
}
