package arg.ds.bst;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BinarySearchTreeTest {

    BinarySearchTree<Integer> bst;
    ByteArrayOutputStream mockOutput;

    @BeforeEach
    void setUp() {
        bst = new BinarySearchTree<>();
        bst.insert(10);
        bst.insert(5);
        bst.insert(15);
        bst.insert(3);
        bst.insert(7);
        bst.insert(12);
        bst.insert(18);

        // Initialize the mock ByteArrayOutputStream
        mockOutput = mock(ByteArrayOutputStream.class);
    }

    @Test
    void testInorderTraversal() throws IOException {
        // Capture the output written to the mock
        ArgumentCaptor<byte[]> byteArrayCaptor = ArgumentCaptor.forClass(byte[].class);

        // Perform the traversal
        bst.inorder(mockOutput);

        // Verify that the write method was called and capture the written bytes
        verify(mockOutput, atLeastOnce()).write(byteArrayCaptor.capture());

        // Convert the captured bytes to a string and compare
        String result = Arrays.stream(byteArrayCaptor.getAllValues().toArray(new byte[0][]))
                .map(String::new)
                .reduce("", String::concat)
                .trim();
        assertEquals("3 5 7 10 12 15 18", result);
    }

    @Test
    void testPreorderTraversal() throws IOException {
        // Capture the output written to the mock
        ArgumentCaptor<byte[]> byteArrayCaptor = ArgumentCaptor.forClass(byte[].class);

        // Perform the traversal
        bst.preorder(mockOutput);

        // Verify that the write method was called and capture the written bytes
        verify(mockOutput, atLeastOnce()).write(byteArrayCaptor.capture());

        // Convert the captured bytes to a string and compare
        String result = Arrays.stream(byteArrayCaptor.getAllValues().toArray(new byte[0][]))
                .map(String::new)
                .reduce("", String::concat)
                .trim();
        assertEquals("10 5 3 7 15 12 18", result);
    }

    @Test
    void testPostorderTraversal() throws IOException {
        // Capture the output written to the mock
        ArgumentCaptor<byte[]> byteArrayCaptor = ArgumentCaptor.forClass(byte[].class);

        // Perform the traversal
        bst.postorder(mockOutput);

        // Verify that the wre method was called and capture the written bytes
        verify(mockOutput, atLeastOnce()).write(byteArrayCaptor.capture());

        // Convert the captured bytes to a string and compare
        String result = Arrays.stream(byteArrayCaptor.getAllValues().toArray(new byte[0][]))
                .map(String::new)
                .reduce("", String::concat)
                .trim();
        assertEquals("3 7 5 12 18 15 10", result);
    }
}
