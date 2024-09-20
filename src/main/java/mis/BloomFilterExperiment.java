package mis;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.io.*;

public class BloomFilterExperiment {
    public static void main(String[] args) {
        int size = 1438233;
        int numHashFunctions = 10;
        BloomFilter bloomFilter = new BloomFilter(size, numHashFunctions);

        try {
            DictionaryLoader.loadDictionary(bloomFilter, "src/main/resources/dictionary.txt");
        } catch (IOException e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        SpellChecker spellChecker = new SpellChecker(bloomFilter);

        // Test with some words
        String[] testWords = {"apple", "banana", "asvanth"};
        for (String word : testWords) {
            boolean isPresent = spellChecker.checkWord(word);
            System.out.println("Word: " + word + " is present: " + isPresent);
        }

        // Generate and test random 5-character words
        Set<String> dictionaryWords = null;
        try {
            dictionaryWords = loadDictionaryWords("src/main/resources/dictionary.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int numTests = 100;
        int falsePositives = 0;

        for (int i = 0; i < numTests; i++) {
            String randomWord = RandomWordGenerator.generateRandomWord(5);
            boolean isPresent = spellChecker.checkWord(randomWord);
            if (isPresent && !dictionaryWords.contains(randomWord)) {
                falsePositives++;
                System.out.println("False positive: " + randomWord);
            }
        }

        System.out.println("Number of false positives: " + falsePositives);
    }

    private static Set<String> loadDictionaryWords(String dictionaryPath) throws IOException {
        Set<String> words = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toLowerCase());
            }
        }
        return words;
    }
}
