package jogo.voxel.blocks;

import jogo.appstate.CharacterType;
import jogo.appstate.WorldAppState;

public interface InteractableType {
    public boolean onInteract(WorldAppState world);
}
