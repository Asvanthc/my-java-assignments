package mis;
import java.util.BitSet;

public class BloomFilter {
    private BitSet bitSet;
    private int size;
    private int numHashFunctions;

    public BloomFilter(int size, int numHashFunctions) {
        this.size = size;
        this.numHashFunctions = numHashFunctions;
        bitSet = new BitSet(size);
    }

    private int hash(String word, int seed) {
        // Example hash function to avoid negative values
        int hash = (word.hashCode() + seed) & 0x7FFFFFFF; // Ensure hash is positive
        return hash % size;
    }

    public void add(String word) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = hash(word, i);
            bitSet.set(hash);
        }
    }

    public boolean contains(String word) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = hash(word, i);
            if (!bitSet.get(hash)) {
                return false;
            }
        }
        return true;
    }
}
