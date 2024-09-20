package io.basics;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonSerialization<T extends Serializable> implements AbstractSerialization<T> {
    private static final Logger logger = LogManager.getLogger(JsonSerialization.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void serialize(T object, String filename) throws IOException {
        objectMapper.writeValue(new File(filename), object);
        logger.info("JSON Before Deserialization stored in {}", object);
    }

    @Override
    public T deserialize(String filename, Class<T> clazz) throws IOException {
        return objectMapper.readValue(new File(filename), clazz);
    }
}
