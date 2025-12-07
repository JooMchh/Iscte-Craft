package jogo.voxel.blocks;

import jogo.appstate.CharacterType;

public interface HazardType {
    public void onContact(CharacterType appState);
    public void onStep(CharacterType appState);
}
