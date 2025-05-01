package other;

import Node.WorkerNode;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * Implements a consistent hashing mechanism to distribute keys (e.g., store names) among worker nodes.
 * Uses virtual nodes to improve load balancing and fault tolerance.
 */
public class HashRing {
    private final TreeMap<Long, WorkerNode> ring = new TreeMap<>();         //long = hash value
    private final int virtualNodes; //Used to evenly spread the keys among the key ring, helping in fault tolerance, load balancing and reduced impact when nodes are added/removed.

    //Ξεχωριστή μεταβλητή για κάθε νήμα
    private static final ThreadLocal<MessageDigest> SHA1_DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });

    /**
     * Initializes the hash ring with the given list of worker nodes and number of virtual nodes per physical node.
     *
     * @param workers the list of worker nodes to add
     * @param virtualNodes the number of virtual nodes per worker for better distribution
     */
    public HashRing(List<WorkerNode> workers, int virtualNodes){
        this.virtualNodes = virtualNodes;
        if(workers != null && !workers.isEmpty()){
            for (WorkerNode node : workers) {
                addNode(node);
            }
        }
    }

    /**
     * Returns all worker nodes currently present in the hash ring.
     *
     * @return a collection of unique worker nodes
     */
    public Collection<WorkerNode> getAllNodes() {
        return new HashSet<>(ring.values());
    }

    /**
     * Adds a worker node to the hash ring by inserting its virtual nodes.
     *
     * @param node the worker node to add
     */
    public void addNode(WorkerNode node) {
        for (int i = 0; i < virtualNodes; i++) {
            String virtualId = node.toString() + "#" + i; //We add + "#" + i to create x virtual Nodes per node.
                                                            //Το toString() του WorkerNode μας δίνει κάτι σαν "localhost:55001".
                                                            //Προσθέτουμε #i για να φτιάξουμε διαφορετικά IDs:
                                                            //localhost:55001#0
                                                            //localhost:55001#1
            long hash = sha1Hash(virtualId);
            ring.put(hash, node);                           //Στο TreeMap ring, με κλειδί το hash και τιμή τον WorkerNode
            //System.out.println("Added virtual node: " + virtualId + " → hash: " + hash);
        }
    }

    /**
     * Removes a worker node and all its associated virtual nodes from the hash ring.
     *
     * @param node the worker node to remove
     */
    public void removeNode(WorkerNode node) {
        for (int i = 0; i < virtualNodes; i++) {
            String virtualId = node.toString() + "#" + i;
            long hash = sha1Hash(virtualId);
            ring.remove(hash);
            System.out.println("Removed virtual node: " + virtualId + " → hash: " + hash);
        }
    }

    /**
     * Finds the worker node responsible for the given key using consistent hashing.
     *
     * @param key the key to hash (e.g., a store name)
     * @return the worker node mapped to handle the key
     */
    public WorkerNode getNodeForKey(String key) {
        long hash = sha1Hash(key);
        Map.Entry<Long, WorkerNode> entry = ring.ceilingEntry(hash); //ceilingEntry Returns a key-value mapping associated with the least key greater than or equal to the given key, or null if there is no such key.
        if (entry == null) {
            return ring.firstEntry().getValue(); // Wrap around the ring
        }
        return entry.getValue();
    }

    /**
     * Computes a SHA-1 hash of the input string and returns a 64-bit positive long value.
     *
     * @param input the string to hash
     * @return the long representation of the SHA-1 hash
     */
    public long sha1Hash(String input) {
        try {
            MessageDigest md = SHA1_DIGEST.get();
            md.reset();
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            //ByteBuffer.wrap(digest).getLong() return the 8 bytes == 64 bit that a long holds out of the 160 bit of SHA1
            return ByteBuffer.wrap(digest).getLong() & 0x7FFFFFFFFFFFFFFFL; //& 0x7FFFFFFFFFFFFFFFL for positive only numbers.
        }
        catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}
