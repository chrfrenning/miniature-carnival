package protocol;


import crypto.ConsistentHashing;
import p2p.NetworkInterface;
import p2p.NodeInterface;


import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * This class implements the chord protocol. The protocol is tested using the custom-built simulator.
 */
public class ChordProtocol implements Protocol{

    // length of the identifier that is used for consistent hashing
    public int m;

    // network object
    public NetworkInterface network;

    // consistent hashing object
    public ConsistentHashing ch;

    // key indexes. tuples of (<key name>, <key index>)
    public HashMap<String, Integer> keyIndexes;

    // node indexes. tuples of (<node index>, <node>) sorted by index (Peer.getId())
    public LinkedHashMap<Integer, NodeInterface> sorted;

    public ChordProtocol(int m){
        this.m = m;
        setHashFunction();
        this.keyIndexes = new HashMap<String, Integer>();
    }



    /**
     * sets the hash function
     */
    public void setHashFunction(){
        this.ch = new ConsistentHashing(this.m);
    }



    /**
     * sets the network
     * @param network the network object
     */
    public void setNetwork(NetworkInterface network){
        this.network = network;
    }


    /**
     * sets the key indexes. Those key indexes can be used to  test the lookup operation.
     * @param keyIndexes - indexes of keys
     */
    public void setKeys(HashMap<String, Integer> keyIndexes){
        this.keyIndexes = keyIndexes;
    }



    /**
     *
     * @return the network object
     */
    public NetworkInterface getNetwork(){
        return this.network;
    }






    /**
     * This method builds the overlay network. It assumes the network object has already been set. It generates indexes
     *     for all the nodes in the network. Based on the indexes it constructs the ring and places nodes on the ring.
     *         algorithm:
     *           1) for each node:
     *           2)     find neighbor based on consistent hash (neighbor should be next to the current node in the ring)
     *           3)     add neighbor to the peer (uses Peer.addNeighbor() method)
     */
    public void buildOverlayNetwork(){

        /*
        implement this logic
         */

        for (Map.Entry<String, NodeInterface> mapElement :
             getNetwork().getTopology().entrySet()) {

            String key = mapElement.getKey();

            // Finding the value
            // using getValue() method
            NodeInterface value = mapElement.getValue();

            getNetwork().getNode(key).setId(ch.hash(key));
        }



        String[] resultingNeighbour = new String[2];
        for (Map.Entry<String, NodeInterface> mapElement :
             getNetwork().getTopology().entrySet()) {

            String key = mapElement.getKey();

            // Finding the value
            // using getValue() method
            NodeInterface value = mapElement.getValue();

           int findNearest = getNetwork().getNode(key).getId();
           resultingNeighbour = getNearestNodetoAddNeighbour(findNearest);
            System.out.println(key+ " is with index " + findNearest + " have neighbour values with node name "+ resultingNeighbour[0]+" node value"+ resultingNeighbour[1]);
            getNetwork().getNode(key).addNeighbor(resultingNeighbour[0],getNetwork().getNode(resultingNeighbour[0]));
        }

    }



    public String[] getNearestNodetoAddNeighbour(int findnearest) {

        int[] arrayOfNetworkIndexes = new int[network.getSize()];
        int i = 0;
         Map<String, String> dictionary = new HashMap<>();

        for (Map.Entry<String, NodeInterface> mapElement :
             getNetwork().getTopology().entrySet()) {
             String key = mapElement.getKey();

            // Finding the value
            // using getValue() method
            NodeInterface value = mapElement.getValue();


            arrayOfNetworkIndexes[i]= getNetwork().getNode(key).getId();
            dictionary.put(String.valueOf(arrayOfNetworkIndexes[i]), key);
            i++;
        }
        String nearestInt = String.valueOf(findNextGreaterOrEqualInteger(arrayOfNetworkIndexes, findnearest));

        System.out.println("value "+ findnearest + "have nearest int of "+ nearestInt);

        String nearestNodeName = dictionary.get(String.valueOf(nearestInt));


        int maxIndex = Arrays.stream(arrayOfNetworkIndexes).max().getAsInt();
        int minIndex = Arrays.stream(arrayOfNetworkIndexes).min().getAsInt();

        String[] result = new String[2];

        if(findnearest != maxIndex ){
            result[0] = nearestNodeName;
            result[1] = nearestInt;
        return result;
        }else  {
            nearestNodeName = dictionary.get(String.valueOf(minIndex));
            result[0] = nearestNodeName;
            result[1] = String.valueOf(minIndex);
        return result;
        }


    }

    public static int findNextGreaterOrEqualInteger(int[] array, int target) {
        int nextInteger = Integer.MAX_VALUE; // Initialize with a large value

        for (int value : array) {
            if (value > target && value < nextInteger) {
                nextInteger = value;
            }
        }

        if (nextInteger == Integer.MAX_VALUE) {
            // No greater or equal integer found in the array
            return -1; // You can choose an appropriate value or throw an exception here
        }

        return nextInteger;
    }



    /**
     * This method builds the finger table. The finger table is the routing table used in the chord protocol to perform
     * lookup operations. The finger table stores m-entries. Each ith entry points to the ith finger of the node.
     * Each ith entry stores the information of it's neighbor that is responsible for indexes ((n+2^i-1) mod 2^m).
     * i = 1,...,m.
     *
     *Each finger table entry should consists of
     *     1) start value - (n+2^i-1) mod 2^m. i = 1,...,m
     *     2) interval - [finger[i].start, finger[i+1].start)
     *     3) node - first node in the ring that is responsible for indexes in the interval
     */
    public void buildFingerTable() {
        /*
        TODO implement this logic
         */

        sorted = getNetwork().getTopology().entrySet().stream()
                .sorted(Comparator.comparing(nodeEntry -> nodeEntry.getValue().getId()))
                .collect(Collectors.toMap(nodeEntry -> nodeEntry.getValue().getId(), Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        int max = (int) Math.pow(2, m);
        System.out.println("--------------------------------------------");
        System.out.println("Calculate " + m + " fingers to each node in a ring of " + max);
        System.out.println("--------------------------------------------");

        for(Map.Entry<Integer, NodeInterface> node : sorted.entrySet()) {
            System.out.println("Calculate " + m + " fingers to "+ node.getValue().getId() +"_(" + node.getValue().getName() + ")");
            FingerTable fingerTable = new FingerTable();
            for(int finger = 1; finger <= m; finger++) {
                int start = (node.getValue().getId() + (int) Math.pow(2, finger -1)) % max;
                int stop = ((node.getValue().getId() + (int) Math.pow(2, finger)) -1) % max;
                NodeInterface contact = findEntryEqualOrBigger(start);
                if (contact != null) {
                    System.out.println("\tFINGER #" + finger
                            + " range " + Integer.toBinaryString(start) + "-" + Integer.toBinaryString(stop)
                            + " points to "+contact.getId() +"_(" + contact.getName() + ")");
                } else {
                    System.out.println(" ERROR: Not found");
                }
                fingerTable.addFinger(new Finger(start, stop, contact));
            }
            node.getValue().setRoutingTable(fingerTable);
        }
        printRoutingTables();
    }

    private NodeInterface findEntryEqualOrBigger(int start) {

        if (sorted.containsKey(start)) return sorted.get(start);

        System.out.print("\t\t...Searching closest to " + start + ". Trying... ");
        for (Map.Entry<Integer, NodeInterface> entry : sorted.entrySet()) {
            if (entry.getKey() >= start) {
                System.out.println(" Found: " + entry.getKey());
                return entry.getValue();
            }
            System.out.print(" " + entry.getKey());
        }
        Map.Entry<Integer, NodeInterface> first = sorted.entrySet().stream().findFirst().get();
        System.out.println(" wrapped and found " + first.getKey());
        return first.getValue();
    }

    public void printRoutingTables() {
        System.out.println("\n.................Printing routing tables...............");
        System.out.println("\tINTERVAL \tNODE ");

        for (Map.Entry<Integer, NodeInterface> node : sorted.entrySet()) {
            System.out.println("Node: " + node.getValue());
            FingerTable fingers = (FingerTable) node.getValue().getRoutingTable();
            for (Map.Entry<Integer, Finger> finger : fingers.fingers.entrySet()) {
                System.out.println("\t" + finger.getValue().start
                        + "-" + finger.getValue().stop
                        + "\t\t" + finger.getValue().node);
            };
        }

        System.out.println(".......................................................");


    }

    /**
     * This method performs the lookup operation.
     *  Given the key index, it starts with one of the node in the network and follows through the finger table.
     *  The correct successors would be identified and the request would be checked in their finger tables successively.
     *  Finally, the request will reach the node that contains the data item.
     *
     * @param keyIndex index of the key
     * @return names of nodes that have been searched and the final node that contains the key
     */
    public LookUpResponse lookUp(int keyIndex){
        /*
        TODO implement this logic
         */

        for(Map.Entry<String, Integer> entry: keyIndexes.entrySet()) {
            keyIndex = Integer.parseInt(String.valueOf(entry.getValue()));
            System.out.println("\nkeyIndex " + keyIndex);
            // PLACEHOLDER CODE choose any node to start searching for the key
            // ask for some node -- the closest successor
            NodeInterface departure = getNetwork().getTopology().get("Node 1");
            FingerTable routing = (FingerTable) departure.getRoutingTable();
            LinkedHashSet<Integer> data = (LinkedHashSet<Integer>) departure.getData();
            NodeInterface destination = data.contains(keyIndex) ? departure : routing.getDestinationForKey(keyIndex);
            System.out.println("\nFirst destination for the key " + keyIndex + " from " + departure.getName() + " is " + destination);
        }
        return null;
    }


}
