package jogo.gameobject.item;

import com.jme3.app.state.BaseAppState;
import jogo.appstate.CharacterType;
import jogo.appstate.PlayerAppState;
import jogo.engine.GameRegistry;
import jogo.gameobject.GameObject;

public abstract class Item extends GameObject {
    protected int stack = 1;

    protected Item(String name) { super(name); }

    // how big the stacks of this item can get
    public byte maxStack() { return 0; }

    public void onInteract(PlayerAppState playerAppState) {
        // Hook for interaction logic (engine will route interactions)
    }

    public void consumeItem(GameRegistry gameRegistry) {
        gameRegistry.remove(this);
    }

    public void setStack(int stack) {
        if (stack >= maxStack()) { // if stacked passed is more than max then correct
            this.stack = maxStack();
        } else if (stack <= 0) { // if stacked passed is 0 then correct
            this.stack = 1;
        } else {  // else set the stack
            this.stack = stack;
        }
    }

}
