package io.basics;

import java.io.IOException;
import java.io.Serializable;

public interface AbstractSerialization<T extends Serializable> {

    void serialize(T object, String filename) throws IOException;

    T deserialize(String filename, Class<T> clazz) throws IOException, ClassNotFoundException;
}
