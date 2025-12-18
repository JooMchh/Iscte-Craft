package jogo.AI.Allies;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import jogo.AI.ActiveAI;
import jogo.AI.Enemies.Enemy;
import jogo.appstate.CharacterType;
import jogo.appstate.PlayerAppState;
import jogo.gameobject.character.Character;
import jogo.util.VectorLengthCompare;

import java.util.*;

public class Ally extends ActiveAI {
    public Ally(Character character, Node rootNode, PhysicsSpace physicsSpace, Vector3f spawnPosition) {
        super(character, rootNode, physicsSpace, spawnPosition);
    }

    @Override
    public void updateAI(PlayerAppState playerAppState, List<ActiveAI> activeAIS) {

        //System.out.println("Updating Slime, at: " + aiNode.getWorldTranslation().x + "," + aiNode.getWorldTranslation().y + "," + aiNode.getWorldTranslation().z);
        Vector3f playerPos = new Vector3f((int) playerAppState.getPlayerPosition().x,(int) playerAppState.getPlayerPosition().y,(int) playerAppState.getPlayerPosition().z) ;

        // simple AI following logic

        if (playerPos != null) {
            HashMap<Vector3f, ActiveAI> targets = new HashMap<>();

            for (ActiveAI ai : activeAIS) { // search for closest enemy
                if (ai instanceof Enemy) {
                    targets.put(ai.getAiNode().getWorldTranslation().subtract(aiNode.getWorldTranslation()), ai);
                }
            }

            List<Vector3f> directions = new ArrayList<>(targets.keySet());

            Vector3f direction = null;
            CharacterType target = null;

            if (directions.size() > 0) {
                Collections.sort(directions, new VectorLengthCompare());

                direction = directions.get(0);
                target = targets.get(direction);
            }

            if (direction.length() <= getAIType().getDetectionRadius() && direction != null && target != null) {
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
                    getAIType().onAttack(target);
                    characterControl.setWalkDirection(direction.mult(-moveSpeed/2));
                }
            } else {
                // stop walking if too far :(
                characterControl.setWalkDirection(Vector3f.ZERO);
            }

        }

    }

}
