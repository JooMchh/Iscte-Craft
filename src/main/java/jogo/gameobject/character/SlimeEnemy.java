package jogo.gameobject.character;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture2D;
import jogo.appstate.CharacterType;
import jogo.appstate.PlayerAppState;
import jogo.gameobject.item.Item;

public class SlimeEnemy extends Character implements AIType {

    public SlimeEnemy() {
        super("Slime");
    }

    @Override
    public void onInteract(PlayerAppState appState) {

    }

    @Override
    public void onAttack(CharacterType characterType) {
        characterType.characterDamage(15);
    }

    @Override
    public BetterCharacterControl getCharacterControl() {
        BetterCharacterControl characterControl = new BetterCharacterControl(0.4f, 1.2f, 60f);
        characterControl.setGravity(new Vector3f(0, -25f, 0));
        characterControl.setJumpForce(new Vector3f(0, this.getSetJumpForce(), 0));
        return characterControl;
    }

    @Override
    public float getSetWalkSpeed() {
        return 8.0f;
    }

    @Override
    public float getSetJumpForce() {
        return 250f;
    }

    @Override
    public Item[] itemsDroppedOnDeath() {
        return new Item[0];
    }

    @Override
    public float getDetectionRadius() {
        return 15f;
    }

    @Override
    public float getAttackRange() {
        return 1.5f;
    }

    public Geometry render(AssetManager assetManager) { // o 'T O D O'
        Geometry g = new Geometry(name, new Box(new Vector3f(0,.6f,0), .6f, .6f, .6f));
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/SlimeFace.png");
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors", true);
        m.setTexture("DiffuseMap", tex);
        m.setColor("Diffuse", ColorRGBA.White);
        m.setColor("Specular", ColorRGBA.White.mult(0.1f));
        m.setFloat("Shininess", 50f);
        g.setMaterial(m);
        return g;
    }
}
