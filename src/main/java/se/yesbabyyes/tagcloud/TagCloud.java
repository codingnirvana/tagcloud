package se.yesbabyyes.tagcloud;

import java.util.*;
import java.util.stream.*;
import java.util.function.Function;

import spark.Request;
import spark.Response;

public class TagCloud {
    private TwitterClient twitterClient;
    private Set<String> stopWords;
    private Brand brand = new Brand();
    private static final String[] STOP_WORDS = new String[] {
        "the", "of", "and", "to", "a", "in", "that", "is", "was", "he", "for",
        "it", "with", "as", "his", "on", "be", "at", "by", "she", "her", "we",
        "you", "your", "its", "thats", "this", "an", "am", "so", "but", "are",
        "from", "to", "if", "them", "they", "there", "who", "which", "about",
        "can", "one", "two", "three", "four", "five", "six", "seven", "eight",
        "nine", "ten", "no", "yes", "not", "hes", "ive", "than", "then", "when",
        "just", "very", "or", "first", "second", "like", "would", "same", "rt",
        "even", "other", "new", "now", "what", "more", "will", "all", "were",
        "have", "their", "how", "off", "little", "big", "him", "into", "get",
        "our", "may", "most", "has", "do", "some", "out", "us", "stripbooks", "everything", "else"
    };

    /**
     * Constructor for the TagCloud.
     *
     * @param   twitterClient   An initialized TwitterClient
     */
    public TagCloud(TwitterClient twitterClient) {
        this.twitterClient = twitterClient;
        this.stopWords = new HashSet<String>(Arrays.asList(STOP_WORDS));
    }

    /**
     * Constructor for the TagCloud with custom stop words.
     *
     * @param   twitterClient   An initialized TwitterClient
     * @param   stopWords       Custom stop words
     */
    public TagCloud(TwitterClient twitterClient, Set<String> stopWords) {
        this.twitterClient = twitterClient;
        this.stopWords = stopWords;
    }

    /**
     * Returns a tag cloud with the most frequent words and their word counts
     * from an RSS feed or Twitter hashtag.
     *
     * @param   req     Spark request
     * @param   res     Spark response
     * @return          Tag cloud of most frequent words in the RSS feed, or
     *                  recent tweets with hashtag.
     */
    public Map view(Request req, Response res) throws HttpException {
        Stream<String> result;

        if(req.queryParams("url") != null) {
            result = brand.getCategoryTagCloud(req.queryParams("url"));
        } else if(req.queryParams("hashtag") != null) {
            result = brand.getWordTagCloud(req.queryParams("hashtag"));
        } else {
            throw new HttpException(400, "Provide either url or hashtag");
        }

        Map<String, Long> wordCount = wordCount(result);

        Long maxValue = wordCount.values().stream().max(Long::compare).get();
        Double fraction = maxValue/12.0;

        Map<String, Long> modifiedWordCount = wordCount.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Double(e.getValue() / fraction).longValue()));

        return tagCloud(modifiedWordCount, 100);
    }

    /**
     * Returns the words in the stream of texts, filtered from links, short and
     * stop words and grouped by word and counted.
     *
     * @param   texts   a Stream of texts
     * @return          a map of the words and their counts
     */
    Map<String, Long> wordCount(Stream<String> texts) {
        return texts
            // Split texts on whitespace and flatten
            .flatMap(text -> Stream.of(text.split("\\s+")))
            // Filter out links
            .filter(word -> !word.startsWith("https://"))
            // Lower-case and strip non-word characters
            .map(word -> word.toLowerCase().replaceAll("[^\\p{L}\\p{Nd}]+", ""))
            // Filter out short words
            .filter(word -> word.length() > 1)
            // Filter out stop words
            .filter(word -> !stopWords.contains(word))
            // Group by word and their counts
            .collect(Collectors.groupingBy(
                    Function.identity(), Collectors.counting()));
    }

    /**
     * Returns the 100 most frequent terms in wordCount, sorted by most
     * frequent.
     *
     * @param   wordCount   a Map of words to their counts
     * @param   limit       an integer to limit to the top results
     * @return              a sorted, descending map of the limit most frequent
     *                      words and their counts
     */
    Map<String, Long> tagCloud(Map<String, Long> wordCount, int limit) {
        return wordCount
            .entrySet()
            .stream()
            // Sort by value, descending
            .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
            .limit(limit)
            // Collect in a LinkedHashMap, preserving order
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
}
