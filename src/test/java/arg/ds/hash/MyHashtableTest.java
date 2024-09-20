package arg.ds.hash;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyHashtableTest {

    private MyHashtable<String, String> hashtable;

    @BeforeEach
    public void setUp() {
        hashtable = new MyHashtable<>();
    }

    @Test
    public void testPutAndGet() {
        hashtable.put("1", "ONE");
        hashtable.put("2", "TWO");
        assertEquals("ONE", hashtable.get("1"));
        assertEquals("TWO", hashtable.get("2"));
    }

    @Test
    public void testRemove() {
        hashtable.put("1", "ONE");
        hashtable.remove("1");
        assertNull(hashtable.get("1"));
    }

    @Test
    public void testContainsKey() {
        hashtable.put("1", "ONE");
        assertTrue(hashtable.containsKey("1"));
        assertFalse(hashtable.containsKey("2"));
    }

    @Test
    public void testSizeAndIsEmpty() {
        assertTrue(hashtable.isEmpty());
        hashtable.put("1", "ONE");
        hashtable.put("2", "TWO");
        assertEquals(2, hashtable.size());
        assertFalse(hashtable.isEmpty());
    }

    @Test
    public void testClear() {
        hashtable.put("1", "ONE");
        hashtable.put("2", "TWO");
        hashtable.clear();
        assertEquals(0, hashtable.size());
        assertTrue(hashtable.isEmpty());
    }
}
