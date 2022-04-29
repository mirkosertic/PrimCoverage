package de.mirkosertic.prim;

import java.util.HashSet;
import java.util.Set;

public class Cell {

    int x;
    int y;
    Set<Cell> neighbours;
    boolean occupied;

    public Cell(final int x, final int y) {
        this.x = x;
        this.y = y;
        this.neighbours = new HashSet<>();
        this.occupied = false;
    }
}
