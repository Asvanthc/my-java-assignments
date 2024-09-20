package io.basics;

import java.io.*;

public class SerializationMod {

    // Generic method to serialize any object to an OutputStream
    public static <T extends Serializable> void serializeObject(T object, OutputStream outputStream) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(object);
        }
    }

    // Generic method to deserialize any object from an InputStream
    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(InputStream inputStream) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            return (T) ois.readObject();
        }
    }
}
