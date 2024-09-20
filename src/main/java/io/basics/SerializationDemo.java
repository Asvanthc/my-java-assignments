package io.basics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerializationDemo {
    private static final Logger logger = LogManager.getLogger(SerializationDemo.class);

    public static void main(String[] args) {
        Person person = new Person("Asvanth", 22, "CBE");

        // Serialize the person object and log the stream data
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            SerializationMod.serializeObject(person, byteArrayOutputStream);
            // Log the byte array
            logger.info("Serialized data: " + byteArrayOutputStream.toString());  // Logging instead of sout
        } catch (IOException e) {
            logger.error("Serialization failed: " + e.getMessage(), e);
        }

        // Deserialize the person object from the byte stream and log it
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        try {
            Person deserializedPerson = SerializationMod.deserializeObject(byteArrayInputStream);
            logger.info("Deserialized Person: " + deserializedPerson);  // Logging instead of sout
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Deserialization failed: " + e.getMessage(), e);
        }
    }
}
