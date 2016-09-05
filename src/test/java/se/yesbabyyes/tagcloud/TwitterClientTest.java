package se.yesbabyyes.tagcloud;

import java.util.*;
import java.util.stream.*;
import static org.junit.Assert.*;
import org.junit.*;

import se.yesbabyyes.tagcloud.TagCloud;

/**
 * Unit test for TwitterClient.
 */
public class TwitterClientTest {

    /**
     * Test getting tweet contents
     */
    @Test
    public void getTweetContents() {
        Map tweets = new HashMap();
        Map tweet1 = new HashMap();
        Map tweet2 = new HashMap();
        tweet1.put("text", "1");
        tweet2.put("text", "2");
        tweets.put("statuses", Arrays.asList(tweet1, tweet2));

        Iterator result = TwitterClient.getTweetContents(tweets)
            .collect(Collectors.toList()).iterator();

        assertEquals("1", result.next());
        assertEquals("2", result.next());
        assertFalse(result.hasNext());
    }
}
