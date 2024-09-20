package io.basics;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class SerializationModTest {
    private static final Logger logger = LogManager.getLogger(SerializationModTest.class);
    private Person person;
    private ByteArrayOutputStream byteArrayOutputStream;

    @BeforeEach
    void setUp() {
        person = new Person("Asvanth", 22, "CBE, TN");
        byteArrayOutputStream = new ByteArrayOutputStream(); // Used for stream-based serialization
    }

    @Test
    void testSerialization() {
        try {
            // Serialize the object to a ByteArrayOutputStream
            SerializationMod.serializeObject(person, byteArrayOutputStream);
            logger.info("Serialization successful: " + byteArrayOutputStream);
        } catch (IOException e) {
            logger.error("Serialization failed: " + e.getMessage(), e);
            fail("Serialization failed: " + e.getMessage());
        }
    }

    @Test
    void testDeserialization() {
        try {
            // First serialize the object
            SerializationMod.serializeObject(person, byteArrayOutputStream);

            // Now deserialize it from the same byte stream
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            Person deserializedPerson = SerializationMod.deserializeObject(byteArrayInputStream);

            // Log and assert the deserialized object
            logger.info("Deserialization successful: " + deserializedPerson);
            assertEquals(person, deserializedPerson, "The deserialized object should match the original");
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Deserialization failed: " + e.getMessage(), e);
            fail("Deserialization failed: " + e.getMessage());
        }
    }
}
