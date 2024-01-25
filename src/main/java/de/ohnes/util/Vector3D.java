package de.ohnes.util;

public class Vector3D {

    private int[] values;

    public Vector3D(int x1, int x2, int x3) {
        this.values = new int[]{x1, x2, x3};
    }

    public int get(int i) {
        return this.values[i];
    }

    public void set(int i, int value) {
        this.values[i] = value;
    }

    public boolean isSmallerElementWise(int x1, int x2, int x3) {
        return x1 <= this.values[0] && x2 <= this.values[1] && x3 <= this.values[2];
    }

    public Vector3D subtract(int x1, int x2, int x3) {
        return new Vector3D(values[0] - x1, values[1] - x2, values[2] - x3);
    }

}
