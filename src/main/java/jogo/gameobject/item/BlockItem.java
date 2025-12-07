package jogo.gameobject.item;

import jogo.appstate.CharacterType;

public class BlockItem extends Item implements BlockType {
    private byte blockId;

    protected BlockItem(String name, byte blockId) { super(name); this.blockId = blockId; }

    @Override
    public byte maxStack() { return 64; }

    public void onInteract(CharacterType characterAppState) {

    }
}
