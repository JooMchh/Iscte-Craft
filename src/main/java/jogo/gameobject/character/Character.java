package jogo.gameobject.character;

import jogo.gameobject.GameObject;

public abstract class Character extends GameObject {

    protected Character(String name) {
        super(name);
    }

    // Example state hooks students can extend
    private int MAX_HEALTH = 100;
    private int health = MAX_HEALTH;
    protected float internalDamageTickTimer = 0f;
    protected final float iFrames = 0.25f;

    public int getHealth() { return health; }
    public int getMAX_HEALTH() { return MAX_HEALTH; }
    public void setHealth(int health) { this.health = health; }
    public void setMAX_HEALTH(int max_health) { this.MAX_HEALTH = max_health; }

    public void damage(int damage) {
        System.out.println("Character damage: damage method called");
        if (internalDamageTickTimer < iFrames) { return; }
        internalDamageTickTimer = 0;
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        } else if (this.health > MAX_HEALTH) {
            this.health = MAX_HEALTH;
        }
        System.out.println("Character damage: character took " + damage + " damage!");
    }

    public void updateInternalTimer(float tpf) {
        internalDamageTickTimer += tpf;
    }

    public boolean isDead() {
        if (this.health <= 0) {
            return true;
        } else {
            return false;
        }
    }

}
