package jogo.voxel.blocks;

import jogo.voxel.VoxelBlockType;

public abstract class HazardBlockType extends VoxelBlockType {
    protected HazardBlockType(String name) {
        super(name);
    }

    @Override
    public boolean isHazard() { return true; }

}
