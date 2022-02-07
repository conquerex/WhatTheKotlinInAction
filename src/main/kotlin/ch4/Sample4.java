package ch4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Sample4 {
    public static void main(String[] args) {
        SampleButton button = new SampleButton();
        State currentState = button.getCurrentState();

        byte[] serializedMember;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            System.out.println(">>> init baos");
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                System.out.println(">>> init oos");
                oos.writeObject(currentState);
                System.out.println(">>> after writeObject");
                // serializedMember -> 직렬화된 member 객체
                serializedMember = baos.toByteArray();
            }
        } catch (IOException e) {
            System.out.println(">>>>>");
            e.printStackTrace();
        }
    }
}
