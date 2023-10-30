package protocol;

import p2p.NodeInterface;

public final class Finger {
    int start;
    int stop;
    NodeInterface node;

    public Finger(int start, int stop, NodeInterface node) {
        this.start = start;
        this.stop = stop;
        this.node = node;
    }

    @Override
    public String toString() {
        return stop + " -->" + node;
    }
}
