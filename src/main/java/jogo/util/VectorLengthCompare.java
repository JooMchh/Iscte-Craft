package jogo.util;

import com.jme3.math.Vector3f;

import java.util.Comparator;
import java.util.Vector;

public class VectorLengthCompare implements Comparator<Vector3f> {
    public int compare(Vector3f v1, Vector3f v2) {
        if (v1.length() > v2.length()) {
            return 1;
        } else if (v1.length() < v2.length()) {
            return -1;
        } else {
            return 0;
        }
    }
}
