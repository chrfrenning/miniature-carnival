package crypto;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The consisten hash function assigns m-bit hash value to data.
 *
 * A hash ring is constructed with the value from 0 to 2^m. Whenever a data item needs a hash value
 * the hash function assigns the data to one point in the hash ring (eg:- each data item would be assigned to the
 * value of 0 to 2^m. It uses sha,md5 hash functions to ensure that the assignment is random.
 */
public class ConsistentHashing {

    // length of the identifier
    public int m;

    /**
     *
     * @param m the length of the hash value in bits
     */
    public ConsistentHashing(int m){
        this.m=m;
    }

    /*


    @param data
    @return returns
     */

    /**
     * Calculates hash value for the data
     * @param data the data can be any String object that needs to get hash value
     * @return hash value (the integer value from 0 to 2^m that indicates the placement of data in the ring).
     */
    public int hash(String data){


        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // digest() method is called to calculate message digest
        // of an input digest() return array of byte
        byte[] messageDigest = md.digest(data.getBytes());

        // Convert byte array into signum representation
        BigInteger res = new BigInteger(1, messageDigest);


        // consistent hashing function - hash mod 2^m
        BigInteger length = BigInteger.valueOf((long) Math.pow(2, m));
        res = res.mod(length);

        return res.intValue();

    }



    public static void test(){
        ConsistentHashing ch = new ConsistentHashing(5);
        String nodes[] ={"node 1", "node 2", "node 3", "node 4", "node 5", "node 6"};
        for(int i=0; i< nodes.length; i++)
            System.out.println( ch.hash(nodes[i]));

    }


}
