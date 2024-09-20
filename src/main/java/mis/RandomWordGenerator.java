package mis;

import java.util.Random;

public class RandomWordGenerator {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    public static String generateRandomWord(int length) {
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            word.append(CHARACTERS.charAt(index));
        }
        return word.toString();
    }
}
