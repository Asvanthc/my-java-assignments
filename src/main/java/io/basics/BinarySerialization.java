package io.basics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class BinarySerialization<T extends Serializable> implements AbstractSerialization<T> {

    private static final Logger logger = LogManager.getLogger(BinarySerialization.class);
    @Override
    public void serialize(T object, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            logger.info("Binary Before Deserialization stored in {}", oos.toString());
            oos.writeObject(object);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(String filename, Class<T> clazz) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (T) ois.readObject();
        }
    }
}
