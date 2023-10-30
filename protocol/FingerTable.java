package protocol;

import p2p.NodeInterface;

import java.util.HashMap;
import java.util.Map;

public class FingerTable {
    HashMap<Integer, Finger> fingers = new HashMap<>();
    public FingerTable() { }

    // Find the successor node for a given key
    public NodeInterface getDestinationForKey(int key) {
        System.out.print("...Matching key " + key + " to interval: ");
        for (Map.Entry<Integer, Finger> finger : fingers.entrySet()) {
            int start = finger.getValue().start;
            int stop = finger.getValue().stop;

            if ((start - stop <= 0 && key >= start && key <= stop) ||
                (start - stop > 0 && (key >= start && key > stop  || key < start && key <= stop)))
            {
                System.out.println("Found: " + finger.getValue().start + "-" + finger.getValue().stop);
                return finger.getValue().node;
            }
            System.out.print(" " + finger.getValue().start + "-" + finger.getValue().stop + " ");
        }
        return null;
    }

    public void addFinger(Finger finger) {
        fingers.put(finger.start, finger);
    }

    @Override
    public String toString() {
        return fingers.toString();
    }
}
