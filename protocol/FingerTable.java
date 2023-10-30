package protocol;

import p2p.NodeInterface;

import java.util.HashMap;

public class FingerTable {
    HashMap<Integer, Finger> fingers = new HashMap<>();
    public FingerTable() { }

    // Find the successor node for a given key
    public NodeInterface getDestinationForKey(int key) {
        // I would implement the Chord protocol's logic here
        // For simplicity, I'll return the first entry in the finger table
        return fingers.entrySet().stream().findFirst().get().getValue().node;
    }

    public void addFinger(Finger finger) {
        fingers.put(finger.start, finger);
    }

    @Override
    public String toString() {
        return fingers.toString();
    }
}
