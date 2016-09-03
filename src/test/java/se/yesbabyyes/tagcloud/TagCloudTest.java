package se.yesbabyyes.tagcloud;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import se.yesbabyyes.tagcloud.TagCloud;

/**
 * Unit test for TagCloud.
 */
public class TagCloudTest {
    /**
     * Test hello world
     */
    @Test
    public void helloWorldReturnsTagCloud() {
        assertEquals("Tag Cloud!", TagCloud.helloWorld(null, null));
    }
}
