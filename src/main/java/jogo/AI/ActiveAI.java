package jogo.AI;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.light.PointLight;
import com.jme3.math.Vector3f;
import jogo.gameobject.character.AIType;
import jogo.gameobject.character.Character;
import jogo.appstate.CharacterType;
import jogo.appstate.PlayerAppState;
import com.jme3.scene.Node;
import jogo.gameobject.item.Item;

import java.util.List;

public abstract class ActiveAI implements CharacterType {
    protected Character character;
    protected Node aiNode;
    protected PointLight aiLight;
    protected BetterCharacterControl characterControl;
    protected float moveSpeed;

    protected ActiveAI(Character character, Node rootNode, PhysicsSpace physicsSpace, Vector3f spawnPosition) {
        if (character instanceof AIType aiCharacter) {
            this.character = character;
            moveSpeed = aiCharacter.getSetWalkSpeed();
            aiNode = new Node(character.getName());

            characterControl = aiCharacter.getCharacterControl();
            aiNode.addControl(characterControl);

            rootNode.attachChild(aiNode);
            physicsSpace.add(characterControl);

            characterControl.warp(spawnPosition);
        } else {
            System.out.println("To create an active AI, a character of the AIType must be passed");
        }
    }

    public void updateAI(PlayerAppState playerAppState, List<ActiveAI> activeAIs) {
        // hook for setting specific AI behaviours ,':)
    }

    public Character getCharacter() {
        return character;
    }

    public AIType getAIType() {
        return (AIType) character;
    }

    public Node getAiNode() {
        return aiNode;
    }

    public Vector3f getJumpForce() {
        return characterControl.getJumpForce(null);
    }

    public float getWalkSpeed() {
        return moveSpeed;
    }

    public float getSetWalkSpeed() {
        AIType aiCharacter = (AIType) character;
        return aiCharacter.getSetWalkSpeed();
    }

    public float getSetJumpForce() {
        AIType aiCharacter = (AIType) character;
        return aiCharacter.getSetJumpForce();
    }

    public List<Item> getDrops() {
        return getAIType().getItemsDroppedOnDeath();
    }

    @Override
    public void characterDamage(int damage) {
        character.damage(damage);
    }

    @Override
    public void resetJumpForce() {
        //System.out.println("Active AI: reset jumpforce");
        if (characterControl != null) {
            characterControl.setJumpForce(new Vector3f(0, this.getSetJumpForce(),0));
        }
    }

    @Override
    public void resetWalkSpeed() {
        //System.out.println("Active AI: reset walkspeed");
        moveSpeed = this.getSetWalkSpeed();
    }

    @Override
    public void setWalkSpeed(float walkSpeed) {
        moveSpeed = walkSpeed;
        //System.out.println("Active AI: set walkspeed to " + walkSpeed);
    }

    @Override
    public void setJumpForce(float jumpForce) {
        if (characterControl != null) {
            //wSystem.out.println("Active AI: set vertical jumpforce to " + jumpForce);
            this.characterControl.setJumpForce(new Vector3f(0, jumpForce, 0));
        }
    }

    public void cleanup(PhysicsSpace physicsSpace) {
        if (aiNode != null) {
            if (characterControl != null) {
                physicsSpace.remove(characterControl);
                aiNode.removeControl(characterControl);
                characterControl = null;
            }
            aiNode.removeFromParent();
            aiNode = null;
        }
    }

}
