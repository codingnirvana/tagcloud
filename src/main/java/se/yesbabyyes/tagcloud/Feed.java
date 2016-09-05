package se.yesbabyyes.tagcloud;

import java.net.*;
import java.util.stream.*;
import java.io.IOException;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.*;

public class Feed {
    private URL url;
    private SyndFeed feed;

    /**
     * Construct a Feed.
     *
     * @param   url     String with url pointing to an Atom/RSS feed
     */
    public Feed(String url) throws HttpException {
        try {
            this.url = new URL(url);
            final SyndFeedInput input = new SyndFeedInput();
            this.feed = input.build(new XmlReader(new URL(url)));
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new HttpException(400, "Invalid URL: " + e.getMessage(), e);
        } catch (FeedException | IOException e) {
            throw new HttpException(503,
                    "Service temporarily unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * Get entry contents from this feed's entries.
     *
     * @return      The text contents from the first content item, with any
     *              HTML tags stripped out.
     */
    public Stream<String> getEntryContents() {
        return feed.getEntries().stream()
            .map(entry -> entry.getContents().isEmpty()
                    ? entry.getDescription().getValue()
                    : entry.getContents().get(0).getValue())
            .map(text -> text.replaceAll("<[^>]+>", ""));
    }
}
