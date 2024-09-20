package io.basics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GenericMain {

    // Create a Logger instance
    private static final Logger logger = LogManager.getLogger(GenericMain.class);

    public static void main(String[] args) {
        try {
            // Binary serialization person
            AbstractSerialization<Person> binarySerializerPerson = new BinarySerialization<>();
            Person objPerson = new Person("asvanth", 22, "cbe");
            binarySerializerPerson.serialize(objPerson, "myObjectBinary.ser");
            Person deserializedBinaryObjPerson = binarySerializerPerson.deserialize("myObjectBinary.ser", Person.class);
            logger.info("Binary: " + deserializedBinaryObjPerson);

            // JSON serialization person
            AbstractSerialization<Person> jsonSerializerPerson = new JsonSerialization<>();
            jsonSerializerPerson.serialize(objPerson, "Person.json");
            Person deserializedJsonObjPerson = jsonSerializerPerson.deserialize("Person.json", Person.class);
            logger.info("JSON: " + deserializedJsonObjPerson);

            // Binary serialization MyObj
            AbstractSerialization<MyObject> binarySerializer = new BinarySerialization<>();
            MyObject obj = new MyObject("BinaryExample", 100);
            binarySerializer.serialize(obj, "myObjectBinary.ser");
            MyObject deserializedBinaryObj = binarySerializer.deserialize("myObjectBinary.ser", MyObject.class);
            logger.info("Binary: " + deserializedBinaryObj);

            // JSON serialization MyObj
            AbstractSerialization<MyObject> jsonSerializer = new JsonSerialization<>();
            jsonSerializer.serialize(obj, "myObject.json");
            MyObject deserializedJsonObj = jsonSerializer.deserialize("myObject.json", MyObject.class);
            logger.info("JSON: " + deserializedJsonObj);

            // XML serialization example
           /* AbstractSerialization<MyObject> xmlSerializer = new XMLSerialization<>();
            xmlSerializer.serialize(obj, "myObject.xml");
            MyObject deserializedXmlObj = xmlSerializer.deserialize("myObject.xml", MyObject.class);
            logger.info("XML: " + deserializedXmlObj); */

        } catch (IOException | ClassNotFoundException e) {
            logger.error("An error occurred", e);
        }
    }
}
