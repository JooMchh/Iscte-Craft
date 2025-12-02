package jogo.gameobject.item;

import com.jme3.app.state.AppStateManager;

public class BlockItem extends Item {
    protected BlockItem(String name) { super(name); }

    @Override
    public byte maxStack() { return 64; }

    public void onInteract() {

    }
}
