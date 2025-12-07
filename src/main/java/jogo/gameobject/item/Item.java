package jogo.gameobject.item;

import com.jme3.app.state.BaseAppState;
import jogo.appstate.CharacterType;
import jogo.gameobject.GameObject;

public abstract class Item extends GameObject {
    protected Item(String name) { super(name); }

    // how big the stacks of this item can get
    public byte maxStack() { return 0; }

    public void onInteract(CharacterType characterAppState) {
        // Hook for interaction logic (engine will route interactions)
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                "maxStack='" + maxStack() + '\'' +
                '}';
    }
}
