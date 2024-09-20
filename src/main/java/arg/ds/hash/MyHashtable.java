package arg.ds.hash;

import java.util.LinkedList;

public class MyHashtable<K, V> {
    private class Entry {
        int hash;
        K key;
        V value;
        Entry next;

        protected Entry(int hash, K key, V value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private LinkedList<Entry>[] table; // Linked list to store chains of entries
    private int count; // number of key-value pairs in the table
    private int capacity; // current capacity of the table
    private float loadFactor = 0.75f; // threshold for resizing

    // Constructor to initialize with specified capacity
    public MyHashtable(int capacity) {
        this.capacity = capacity;
        table = new LinkedList[capacity];
        count = 0;
    }

    // Default constructor with a capacity of 10
    public MyHashtable() {
        this(10);
    }

    // Hash function to compute index for a key
    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    // Resize the hashtable when load factor is exceeded
    private void resize() {
        capacity *= 2;
        LinkedList<Entry>[] newTable = new LinkedList[capacity];
        for (LinkedList<Entry> chain : table) {
            if (chain != null) {
                for (Entry e : chain) {
                    int newIndex = hash(e.key);
                    if (newTable[newIndex] == null) {
                        newTable[newIndex] = new LinkedList<>();
                    }
                    newTable[newIndex].add(new Entry(e.hash, e.key, e.value, null));
                }
            }
        }
        table = newTable;
    }

    // Put method to add a key-value pair
    public V put(K key, V value) {
        int index = hash(key);
        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }
        for (Entry e : table[index]) {
            if (e.key.equals(key)) {
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }
        table[index].add(new Entry(index, key, value, null));
        count++;

        if ((float) count / capacity >= loadFactor) {
            resize();
        }

        return null;
    }

    // Get method to retrieve a value by key
    public V get(K key) {
        int index = hash(key);
        if (table[index] == null) return null;
        for (Entry e : table[index]) {
            if (e.key.equals(key)) {
                return e.value;
            }
        }
        return null;
    }

    // Remove method to remove a key-value pair by key
    public V remove(K key) {
        int index = hash(key);
        if (table[index] == null) return null;
        for (Entry e : table[index]) {
            if (e.key.equals(key)) {
                V value = e.value;
                table[index].remove(e);
                count--;
                return value;
            }
        }
        return null;
    }

    // Check if the hashtable contains a key
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    // Return the number of key-value pairs in the hashtable
    public int size() {
        return count;
    }

    // Check if the hashtable is empty
    public boolean isEmpty() {
        return count == 0;
    }

    // Clear all entries from the hashtable
    public void clear() {
        table = new LinkedList[capacity];
        count = 0;
    }

    // Main method to test functionality
    public static void main(String[] args) {
        MyHashtable<String, String> htable = new MyHashtable<>();

        htable.put("1", "ONE");
        htable.put("2", "TWO");

        System.out.println(htable.get("1")); // Should print "ONE"
        System.out.println(htable.size());   // Should print "2"
        System.out.println(htable.containsKey("2")); // Should print "true"
        htable.remove("1");
        System.out.println(htable.get("1")); // Should print "null"
        htable.clear();
        System.out.println(htable.size());   // Should print "0"
    }
}

