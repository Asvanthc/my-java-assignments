package arg.ds.list;
import java.util.NoSuchElementException;

public class SingleLinkedList<T> {  // Using generics

    protected class Node {
        T item;
        Node next;

        Node(T item, Node next) {
            this.item = item;
            this.next = next;
        }
    }
    private int size; // number of nodes in the list
    protected Node first; // head pointer
    private Node last;  // tail pointer

    public SingleLinkedList() {
        size = 0;
        first = null;
        last = null;
    }

    // Return the first element in the list
    public T getFirst() {
        if (first == null) throw new NoSuchElementException("List is empty");
        return first.item;
    }

    // Return the last element in the list
    public T getLast() {
        if (last == null) throw new NoSuchElementException("List is empty");
        return last.item;
    }

    // Add element at the beginning of the list
    public void addFirst(T e) {
        Node newNode = new Node(e, first);
        first = newNode;
        if (size == 0) {
            last = first;  // If list was empty, both first and last will point to new node
        }
        size++;
    }

    // Add element at the end of the list
    public void addLast(T e) {
        Node newNode = new Node(e, null);
        if (size == 0) {
            first = last = newNode;
        } else {
            last.next = newNode;
            last = newNode;
        }
        size++;
    }

    // Add element at the end of the list
    public boolean add(T e) {
        Node newNode = new Node(e, null);
        if (size == 0) {
            first = last = newNode;
        } else {
            last.next = newNode;
            last = newNode;
        }
        size++;
        return true;
    }

    // Remove and return the first element in the list
    public T removeFirst() {
        if (size == 0) throw new NoSuchElementException("List is empty");
        T item = first.item;
        first = first.next;
        size--;
        if (size == 0) last = null;
        return item;
    }

    // Remove and return the last element in the list
    public T removeLast() {
        if (size == 0) throw new NoSuchElementException("List is empty");
        if (size == 1) {
            return removeFirst();
        }

        Node current = first;
        while (current.next != last) {
            current = current.next;
        }

        T item = last.item;
        last = current;
        last.next = null;
        size--;
        return item;
    }

    // Return the size of the list
    public int size() {
        return size;
    }

    // Print all elements in the list
    public void printList() {
        Node current = first;
        while (current != null) {
            System.out.print(current.item + " ");
            current = current.next;
        }
        System.out.println();
    }

    // Reverse the linked list
    public void reverse() {
        Node prev = null;
        Node current = first;
        Node next = null;
        last = first;
        while (current != null) {
            next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        first = prev;
    }

    // Find and return the middle element of the list
    public T getMiddle() {
        if (size == 0) throw new NoSuchElementException("List is empty");

        Node slow = first;
        Node fast = first;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow.item;
    }

    // Check if the list contains a cycle
    public boolean hasCycle() {
        if (first == null) return false;
        Node slow = first;
        Node fast = first;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;  // Cycle detected
            }
        }
        return false;
    }

    // Merge two sorted linked lists into one sorted linked list
    // Merge two sorted linked lists into one sorted linked list
    public static <T extends Comparable<T>> SingleLinkedList<T> merge(SingleLinkedList<T> list1, SingleLinkedList<T> list2) {
        SingleLinkedList<T> result = new SingleLinkedList<>();
        SingleLinkedList<T>.Node node1 = list1.first;
        SingleLinkedList<T>.Node node2 = list2.first;

        while (node1 != null && node2 != null) {
            if (node1.item.compareTo(node2.item) <= 0) {
                result.add(node1.item);
                node1 = node1.next;
            } else {
                result.add(node2.item);
                node2 = node2.next;
            }
        }

        while (node1 != null) {
            result.add(node1.item);
            node1 = node1.next;
        }

        while (node2 != null) {
            result.add(node2.item);
            node2 = node2.next;
        }

        return result;
    }

    // Check if the list contains a specific element
    public boolean contains(T e) {
        Node current = first;
        while (current != null) {
            if (current.item.equals(e)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Clear the linked list
    public void clear() {
        first = null;
        last = null;
        size = 0;
    }

}
