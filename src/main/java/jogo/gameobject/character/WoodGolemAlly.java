package jogo.gameobject.character;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.texture.Texture2D;
import jogo.appstate.CharacterType;
import jogo.appstate.PlayerAppState;
import jogo.gameobject.item.*;

import java.util.ArrayList;
import java.util.List;

public class WoodGolemAlly extends Character implements AIType, HazardImmune {

    public WoodGolemAlly() {
        super("Wood Golem");
        MAX_HEALTH = 75;
        health = MAX_HEALTH;
    }

    @Override
    public void onInteract(PlayerAppState appState) {
        appState.characterDamage(-5);
    }

    @Override
    public void onAttack(CharacterType characterType) {
        characterType.characterDamage(15);
    }

    @Override
    public BetterCharacterControl getCharacterControl() {
        BetterCharacterControl characterControl = new BetterCharacterControl(0.6f, 5f, 60f);
        characterControl.setGravity(new Vector3f(0, -25f, 0));
        characterControl.setJumpForce(new Vector3f(0, this.getSetJumpForce(), 0));
        return characterControl;
    }

    @Override
    public float getSetWalkSpeed() {
        return 6.0f;
    }

    @Override
    public float getSetJumpForce() {
        return 250f;
    }

    @Override
    public List<Item> getItemsDroppedOnDeath() {
        List<Item> drops = new ArrayList<>();

        Item metal = new BlockItem("wood planks", (byte) 10);
        metal.setStack(5);
        drops.add(metal);

        return drops;
    }

    @Override
    public float getDetectionRadius() {
        return 25f;
    }

    @Override
    public float getAttackRange() {
        return 2.5f;
    }

    @Override
    public Geometry render(AssetManager assetManager) { // o 'T O D O'
        Geometry g = new Geometry(name, new Box(new Vector3f(0,2.5f,0), 1.5f, 2.5f, 1.5f));
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/WoodPlank.png");
        Material m = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors", true);
        m.setTexture("DiffuseMap", tex);
        m.setColor("Diffuse", ColorRGBA.Orange);
        m.setColor("Specular", ColorRGBA.White.mult(0.1f));
        m.setFloat("Shininess", 8f);
        g.setMaterial(m);
        g.setLocalTranslation(0,2.5f,0);
        return g;
    }
}
