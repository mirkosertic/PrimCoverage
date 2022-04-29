package de.mirkosertic.prim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Prim {

    static int shortestAngle(int origin, int target) {
        int diff = (target - origin + 180) % 360 - 180;
        if (diff< -180) {
            return diff + 360;
        }
        return diff;
    }

    public static void main(String[] args) {
        final Set<Cell> cells = new HashSet<>();
        final int gridSize = 10;

        Cell first = null;

        // Construct grid
        final int gsx = 8;
        final int gsy = 9;
        for (int x = 0; x < gsy; x++) {
            for (int y = 0; y < gsx; y++) {
                final Cell cell = new Cell(x * gridSize, y * gridSize);
                if (first == null) {
                    first = cell;
                }
                cells.add(cell);
            }
        }
        double maxDistance = Math.sqrt(gridSize * gridSize + gridSize * gridSize) + 0.05d;
        // Find neightbours
        for (final Cell a : cells) {
            for (final Cell b : cells) {
                if (a != b) {
                    int dx = b.x - a.x;
                    int dy = b.y - a.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < maxDistance) {
                        a.neighbours.add(b);
                    }
                }
            }
        }

        final double initialOrientation = .0;
        final Map<Cell, PathNode> cellToPathNode = new HashMap<>();
        final Consumer<Cell> prim = new Consumer<Cell>() {
            @Override
            public void accept(final Cell first) {
                cellToPathNode.clear();
                PathNode rover = new PathNode(first, initialOrientation, 0);
                cellToPathNode.put(first, rover);
                while (true) {
                    PathNode pathNodeToAdd = null;
                    Cell cellToAdd = null;
                    double costs = 0;
                    // We check for every cell
                    for (final Cell cell : cells) {
                        if (!cell.occupied && !cellToPathNode.containsKey(cell)) {
                            // Cell is not yet part of the path. We compute the next edge with the minumum cost
                            for (final PathNode node : cellToPathNode.values()) {
                                if (cell.neighbours.contains(node.cell)) {
                                    final int dx = node.cell.x - cell.x;
                                    final int dy = node.cell.y - cell.y;
                                    final double angleToTargetInRadians = Math.atan2(dy, dx);
                                    double shortestAngleInRadians = Math.toRadians(shortestAngle((int) Math.toDegrees(node.robotOrientation), (int) Math.toDegrees(angleToTargetInRadians)));

                                    final int drovx = rover.cell.x - cell.x;
                                    final int drovy = rover.cell.y - cell.y;
                                    double distanceToRover = Math.sqrt(drovx * drovx + drovy * drovy);

                                    double c = Math.sqrt(dx * dx + dy * dy) + shortestAngleInRadians * 5 + node.totalCost;

                                    if (c < costs || pathNodeToAdd == null) {
                                        pathNodeToAdd = node;
                                        cellToAdd = cell;
                                        costs = c;
                                    }
                                }
                            }

                        }
                    }

                    if (cellToAdd == null) {
                        System.out.println("No more edges found. We are done!");
                        break;
                    } else {
                        final int dx = cellToAdd.x - pathNodeToAdd.cell.x;
                        final int dy = cellToAdd.y - pathNodeToAdd.cell.y;
                        double newOrientationInRadians = Math.atan2(dy, dx);
                        final PathNode newNode = new PathNode(cellToAdd, newOrientationInRadians, costs);
                        pathNodeToAdd.children.add(newNode);
                        cellToPathNode.put(cellToAdd, newNode);
                        rover = newNode;
                    }
                }
            }
        };

        prim.accept(first);

        final int offsetx = 50;
        final int offsety = 50;
        final int scale = 3;

        final JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);
                for (final Cell c : cells) {
                    if (c.occupied) {
                        g.setColor(Color.red);
                    } else {
                        g.setColor(Color.black);
                    }
                    g.fillRect(offsetx + (c.x - 2) * scale, offsety + (c.y - 2) * scale, 4 * scale, 4 * scale);
                }

                g.setColor(Color.blue);
                for (final PathNode node : cellToPathNode.values()) {
                    for (final PathNode c : node.children) {
                        g.drawLine(offsetx + (node.cell.x * scale), offsety + (node.cell.y * scale), offsetx + (c.cell.x * scale), offsety + (c.cell.y * scale));
                    }
                }
            }
        };

        final Cell firstCell = first;
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                int gridx = (e.getX() - offsetx) / scale;
                int gridy = (e.getY() - offsetx) / scale;
                Cell found = null;
                double distance = 0;
                for (final Cell cell : cells) {
                    int dx = cell.x - gridx;
                    int dy = cell.y - gridy;
                    double d = Math.sqrt(dx * dx + dy * dy);
                    if (d < distance || found == null) {
                        found = cell;
                        distance = d;
                    }
                }
                if (found != null) {
                    found.occupied = !found.occupied;
                    panel.invalidate();
                    panel.repaint();

                    prim.accept(firstCell);
                }
            }
        });

        final JFrame render = new JFrame("PRIM Interactive Visualization");
        render.setContentPane(panel);
        render.setSize(640, 480);
        render.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        render.setVisible(true);
    }
}
