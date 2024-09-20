package io.basics;

import javax.xml.bind.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class XMLSerialization<T extends Serializable> implements AbstractSerialization<T> {

    // Serialize object to XML
    @Override
    public void serialize(T object, String filename) throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(object, new File(filename));
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    // Deserialize object from XML, requires class type
    @Override
    public T deserialize(String filename, Class<T> clazz) throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new File(filename));
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }
}
