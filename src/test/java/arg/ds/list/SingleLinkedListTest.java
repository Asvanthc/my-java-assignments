package arg.ds.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.NoSuchElementException;

class SingleLinkedListTest {

    private SingleLinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new SingleLinkedList<>();
    }

    @Test
    void testInitialization() {
        assertEquals(0, list.size());
        assertThrows(NoSuchElementException.class, () -> list.getFirst());
        assertThrows(NoSuchElementException.class, () -> list.getLast());
    }

    @Test
    void testRemoveFirstOnEmpty() {
        assertThrows(NoSuchElementException.class, () -> list.removeFirst());
    }

    @Test
    void testRemoveLastOnEmpty() {
        assertThrows(NoSuchElementException.class, () -> list.removeLast());
    }



    @Test
    void testAddAndGetFirst() {
        list.add("one");
        assertEquals("one", list.getFirst());
    }

    @Test
    void testAddFirst() {
        list.addFirst("one");
        list.addFirst("two");
        assertEquals("two", list.getFirst());  // "two" should be the first element
    }

    @Test
    void testRemoveFirst() {
        list.add("one");
        list.add("two");
        assertEquals("one", list.removeFirst());
        assertEquals("two", list.getFirst());  // "two" should be the new first element
    }

    @Test
    void testRemoveLast() {
        list.add("one");
        list.add("two");
        list.add("three");
        assertEquals("three", list.removeLast());  // Should remove "three"
        assertEquals("two", list.getLast());       // "two" should be the last element
    }

    @Test
    void testSize() {
        list.add("one");
        list.add("two");
        assertEquals(2, list.size());  // Size should be 2
    }

    @Test
    void testReverse() {
        list.add("one");
        list.add("two");
        list.add("three");
        list.reverse();
        assertEquals("three", list.getFirst());  // After reversal, "three" should be first
        assertEquals("one", list.getLast());     // After reversal, "one" should be last
    }

    @Test
    void testHasCycle() {
        assertFalse(list.hasCycle());

        // Creating a cycle for testing
        list.add("first");
        SingleLinkedList<String>.Node Anode = list.first;
        Anode.next = Anode; // Creating a cycle

        assertTrue(list.hasCycle());
    }

}
