package io.basics;

//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;
//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlAccessType;
import java.io.Serializable;

//@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)  // Automatically binds fields
public class MyObject implements Serializable {

    private String name;
    private int value;

    // Default constructor for XML/JSON deserialization
    public MyObject() {}

    public MyObject(String name, int value) {
        this.name = name;
        this.value = value;
    }

    // Getters and setters
//    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    @XmlElement
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MyObject{name='" + name + "', value=" + value + "}";
    }
}
