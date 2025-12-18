package jogo.appstate;

import jogo.voxel.VoxelBlockType;

import java.util.HashMap;

public interface CharacterType {
    public void characterDamage(int damage);
    public void resetJumpForce();
    public void resetWalkSpeed();
    public void setWalkSpeed(float walkSpeed);
    public void setJumpForce(float jumpForce);
};
