package mis;

public class SpellChecker {
    private BloomFilter bloomFilter;

    public SpellChecker(BloomFilter bloomFilter) {
        this.bloomFilter = bloomFilter;
    }

    public boolean checkWord(String word) {
        return bloomFilter.contains(word.toLowerCase());
    }
}
