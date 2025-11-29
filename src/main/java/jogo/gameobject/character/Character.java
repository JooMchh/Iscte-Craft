package jogo.gameobject.character;

import jogo.gameobject.GameObject;

public abstract class Character extends GameObject {

    protected Character(String name) {
        super(name);
    }

    // Example state hooks students can extend
    private int MAX_HEALTH = 100;
    private int health = MAX_HEALTH;

    public int getHealth() { return health; }
    public int getMAX_HEALTH() { return MAX_HEALTH; }
    public void setHealth(int health) { this.health = health; }
    public void setMAX_HEALTH(int max_health) { this.MAX_HEALTH = max_health; }

    public void damage(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        } else if (this.health > MAX_HEALTH) {
            this.health = MAX_HEALTH;
        }
    }

    public boolean isDead() {
        if (this.health <= 0) {
            return true;
        } else {
            return false;
        }
    }

}
