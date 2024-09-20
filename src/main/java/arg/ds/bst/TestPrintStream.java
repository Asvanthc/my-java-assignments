package arg.ds.bst;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestPrintStream extends PrintStream {
    private final ByteArrayOutputStream byteArrayOutputStream;

    public TestPrintStream(ByteArrayOutputStream byteArrayOutputStream) {
        super(byteArrayOutputStream);
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public String getOutput() {
        return byteArrayOutputStream.toString();
    }
}
