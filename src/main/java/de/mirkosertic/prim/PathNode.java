package de.mirkosertic.prim;

import java.util.HashSet;
import java.util.Set;

public class PathNode {

    final Cell cell;
    final double robotOrientation;
    final double totalCost;
    final Set<PathNode> children;

    public PathNode(final Cell cell, final double robotOrientation, final double totalCost) {
        this.cell = cell;
        this.robotOrientation = robotOrientation;
        this.totalCost = totalCost;
        this.children = new HashSet<>();
    }
}
