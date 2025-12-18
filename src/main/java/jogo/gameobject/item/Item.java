package jogo.gameobject.item;

import com.jme3.app.state.BaseAppState;
import jogo.appstate.CharacterType;
import jogo.appstate.PlayerAppState;
import jogo.engine.GameRegistry;
import jogo.gameobject.GameObject;

public abstract class Item extends GameObject {
    protected Item(String name) { super(name); }

    // how big the stacks of this item can get
    public byte maxStack() { return 0; }

    public void onInteract(PlayerAppState playerAppState) {
        // Hook for interaction logic (engine will route interactions)
    }

    public void consumeItem(GameRegistry gameRegistry) {
        gameRegistry.remove(this);
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                "maxStack='" + maxStack() + '\'' +
                '}';
    }
}
