package jogo.AI.Enemies;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import jogo.AI.ActiveAI;
import jogo.appstate.PlayerAppState;
import jogo.gameobject.character.Character;

import java.util.List;

public class Enemy extends ActiveAI {
    public Enemy(Character character, Node rootNode, PhysicsSpace physicsSpace, Vector3f spawnPosition) {
        super(character, rootNode, physicsSpace, spawnPosition);
    }

    @Override
    public void updateAI(PlayerAppState playerAppState, List<ActiveAI> activeAIS) {

        //System.out.println("Updating Slime, at: " + aiNode.getWorldTranslation().x + "," + aiNode.getWorldTranslation().y + "," + aiNode.getWorldTranslation().z);
        Vector3f playerPos = new Vector3f((int) playerAppState.getPlayerPosition().x,(int) playerAppState.getPlayerPosition().y,(int) playerAppState.getPlayerPosition().z) ;

        // simple AI following logic

        if (playerPos != null) {
            Vector3f direction = playerPos.subtract(aiNode.getWorldTranslation());

            if (direction.length() <= getAIType().getDetectionRadius()) {
                if (direction.length() > getAIType().getAttackRange() && !playerAppState.getPlayer().cantHit()) {
                    direction.normalizeLocal();
                    direction.y = 0; // set y a height do ai para n voar
                    characterControl.setWalkDirection(direction.mult(moveSpeed));
                    if (playerPos.y > (int) aiNode.getWorldTranslation().y) {
                        characterControl.jump();
                    }
                } else {
                    // damage and then back away when close for difficulty >:)
                    direction.y = 0; // same reason
                    characterControl.jump();
                    getAIType().onAttack(playerAppState);
                    characterControl.setWalkDirection(direction.mult(-moveSpeed/2));
                }
            } else {
                // stop walking if :(
                characterControl.setWalkDirection(Vector3f.ZERO);
            }

        }

    }

}
