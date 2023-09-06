package de.ohnes.util;

public class Vector3D {

    private int[] values;

    public Vector3D(int x1, int x2, int x3) {
        this.values = new int[]{x1, x2, x3};
    }

    public int get(int i) {
        return this.values[i];
    }

    public boolean isSmallerElementWise(int x1, int x2, int x3) {
        return x1 <= this.values[0] && x2 <= this.values[1] && x3 <= this.values[2];
    }

    public Vector3D subtract(Vector3D other) {
        return new Vector3D(values[0] - other.get(0), values[1] - other.get(1), values[2] - other.get(2));
    }

}
