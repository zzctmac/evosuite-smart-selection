package com.examples.with.different.packagename;


public class ArrayIntList  {
    public static int MAX_SIZE = 50;
    private final int[] _data = new int[MAX_SIZE];

    public int set(int index, int element) {
        checkRange(index);
        int oldVal = _data[index];
        _data[index] = element;
        return oldVal;
    }
    protected void checkRange(int index) {
        if(index >= MAX_SIZE)
            throw new IndexOutOfBoundsException();
        if(index < 0)
            throw new RuntimeException("Index less than 0");

    }
}
