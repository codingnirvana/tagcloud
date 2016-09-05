package se.yesbabyyes.tagcloud;

import java.util.*;
import java.util.stream.*;
import static org.junit.Assert.*;
import org.junit.*;

import se.yesbabyyes.tagcloud.TagCloud;

/**
 * Unit test for TagCloud.
 */
public class TagCloudTest {
    private TagCloud tagCloud;

    @Before
    public void setUp() {
        this.tagCloud = new TagCloud(null);
    }

    /**
     * Test word counting
     */
    @Test
    public void wordCountCountsWords() {
        Stream<String> texts = Stream.of("foo bar", "bar baz");
        Map<String, Long> result = tagCloud.wordCount(texts);

        assertEquals(new Long(2), result.get("bar"));
        assertEquals(new Long(1), result.get("foo"));
        assertEquals(new Long(1), result.get("baz"));
    }

    /**
     * Test word counting with stop word
     */
    @Test
    public void wordCountDoesNotCountStopWord() {
        Stream<String> texts = Stream.of("foo bar", "bar is");
        Map<String, Long> result = tagCloud.wordCount(texts);

        assertNull(result.get("is"));
    }

    /**
     * Test tag cloud from word count
     */
    @Test
    public void tagCloudHasCorrectOrder() {
        Map wordCount = new HashMap();
        wordCount.put("one", 1);
        wordCount.put("two", 2);

        Map<String, Long> result = tagCloud.tagCloud(wordCount, 2);
        assertEquals("two", result.entrySet().iterator().next().getKey());
    }

    /**
     * Test tag cloud has limit
     */
    @Test
    public void tagCloudHasLimit() {
        Map wordCount = new HashMap();
        wordCount.put("one", 1);
        wordCount.put("two", 2);
        wordCount.put("three", 3);

        Map<String, Long> result = tagCloud.tagCloud(wordCount, 2);
        assertNull(result.get("one"));
    }
}
