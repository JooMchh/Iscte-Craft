package jogo.util;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

public class GeometryEx extends Geometry {
    private final byte block_id;

    public GeometryEx(String name, Mesh mesh, byte block_id) {
        super(name, mesh);
        this.block_id = block_id;
    }

    public byte getBlock_id() {
        return block_id;
    }
}
