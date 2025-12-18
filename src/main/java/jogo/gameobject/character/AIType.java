package jogo.gameobject.character;

import com.jme3.bullet.control.BetterCharacterControl;
import jogo.appstate.CharacterType;
import jogo.appstate.PlayerAppState;
import jogo.gameobject.item.Item;

import java.util.List;

public interface AIType {
    public void onInteract(PlayerAppState appState);
    public void onAttack(CharacterType characterType);
    public BetterCharacterControl getCharacterControl();
    public float getSetWalkSpeed();
    public float getSetJumpForce();
    public List<Item> getItemsDroppedOnDeath();
    public float getDetectionRadius();
    public float getAttackRange();
}
