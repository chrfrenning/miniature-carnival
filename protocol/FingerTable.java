package protocol;

import p2p.NodeInterface;

public class FingerTable {
    int m;
    NodeInterface closest;
    public FingerTable(int size, NodeInterface closest) {
        m = size;
        this.closest = closest;
    }

    // Find the successor node for a given key
    public NodeInterface getSuccessorForKey(int key) {
        // I would implement the Chord protocol's logic here
        // For simplicity, I'll return the first entry in the finger table
        return closest;
    }

    @Override
    public String toString() {
        return "FingerTable{" +
                "m=" + m +
                ", finger 1 is " + closest.getName() + " [" +closest.getId() + "], keys: " + closest.getData() +
                '}';
    }
}
