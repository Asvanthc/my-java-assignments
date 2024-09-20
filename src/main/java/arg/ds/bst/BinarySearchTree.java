package arg.ds.bst;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Stack;

public class BinarySearchTree<T extends Comparable<T>> {

    protected class Node {
        T data;
        Node left, right;

        Node(T data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;

    // Constructor
    public BinarySearchTree() {
        root = null;
    }

    // Insert a new node (iteratively)
    public void insert(T data) {
        if (root == null) {
            root = new Node(data);
            return;
        }

        Node current = root;
        Node parent = null;

        while (current != null) {
            parent = current;
            if (data.compareTo(current.data) < 0) {
                current = current.left;
            } else if (data.compareTo(current.data) > 0) {
                current = current.right;
            } else {
                return; // Value already exists, no duplicates allowed
            }
        }

        if (data.compareTo(parent.data) < 0) {
            parent.left = new Node(data);
        } else {
            parent.right = new Node(data);
        }
    }

    // In-order traversal (iteratively) with OutputStream
    public void inorder(ByteArrayOutputStream output) throws IOException {

        if (root == null) return;

        Stack<Node> stack = new Stack<>();
        Node current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            output.write((current.data.toString() + " ").getBytes());
            current = current.right;
        }
    }

    // Pre-order traversal (iteratively) with OutputStream
    public void preorder(ByteArrayOutputStream output) throws IOException {
        if (root == null) return;

        Stack<Node> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            output.write((current.data.toString() + " ").getBytes());

            if (current.right != null) {
                stack.push(current.right);
            }
            if (current.left != null) {
                stack.push(current.left);
            }
        }
    }

    // Post-order traversal (iteratively) with OutputStream
    public void postorder(ByteArrayOutputStream output) throws IOException {
        if (root == null) return;

        Stack<Node> stack1 = new Stack<>();
        Stack<Node> stack2 = new Stack<>();
        stack1.push(root);

        while (!stack1.isEmpty()) {
            Node current = stack1.pop();
            stack2.push(current);

            if (current.left != null) {
                stack1.push(current.left);
            }
            if (current.right != null) {
                stack1.push(current.right);
            }
        }

        while (!stack2.isEmpty()) {
            output.write((stack2.pop().data.toString() + " ").getBytes());
        }
    }

    // Find a node (iteratively)
    public boolean find(T data) {
        Node current = root;

        while (current != null) {
            if (data.compareTo(current.data) == 0) {
                return true;
            } else if (data.compareTo(current.data) < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return false;
    }

    // Delete a node (iteratively)
    public void delete(T data) {
        root = deleteIter(root, data);
    }

    private Node deleteIter(Node root, T data) {
        Node parent = null;
        Node current = root;

        while (current != null && !current.data.equals(data)) {
            parent = current;
            if (data.compareTo(current.data) < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        if (current == null) return root; // Node to delete not found

        // Case 1: No child
        if (current.left == null && current.right == null) {
            if (current == root) {
                root = null;
            } else if (current == parent.left) {
                parent.left = null;
            } else {
                parent.right = null;
            }
        }
        // Case 2: One child
        else if (current.left == null || current.right == null) {
            Node child = (current.left != null) ? current.left : current.right;

            if (current == root) {
                root = child;
            } else if (current == parent.left) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        }
        // Case 3: Two children
        else {
            Node successor = findMinNode(current.right);
            T successorData = successor.data;
            delete(successorData); // Recursively delete the successor
            current.data = successorData; // Replace current node's data with successor's data
        }
        return root;
    }

    // Find the minimum node in a subtree (iteratively)
    private Node findMinNode(Node root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    // Find minimum value (iteratively)
    public T findMin() {
        if (root == null) return null;
        Node current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.data;
    }

    // Find maximum value (iteratively)
    public T findMax() {
        if (root == null) return null;
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.data;
    }

    // Calculate the height of the tree (iteratively)
    public int height() {
        if (root == null) return -1;
        return heightIter(root);
    }

    private int heightIter(Node root) {
        if (root == null) return -1;
        return Math.max(heightIter(root.left), heightIter(root.right)) + 1;
    }

    // Check if the tree is balanced (iteratively)
    public boolean isBalanced() {
        return checkBalanceIter(root);
    }

    private boolean checkBalanceIter(Node root) {
        if (root == null) return true;

        Stack<Node> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            int leftHeight = heightIter(current.left);
            int rightHeight = heightIter(current.right);

            if (Math.abs(leftHeight - rightHeight) > 1) {
                return false;
            }

            if (current.left != null) stack.push(current.left);
            if (current.right != null) stack.push(current.right);
        }
        return true;
    }
}
