package mis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DictionaryLoader {
    public static void loadDictionary(BloomFilter bloomFilter, String dictionaryPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                bloomFilter.add(line.trim().toLowerCase());
            }
        }
    }
}
