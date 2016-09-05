package se.yesbabyyes.tagcloud;

import static spark.Spark.*;
import spark.ResponseTransformer;
import se.yesbabyyes.tagcloud.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.*;

public class App {
    private static final String TWITTER_KEY = "9IZamTlXCUxQh8ujeklhDMiyy";
    private static final String TWITTER_SECRET =
        "1RFZkG5Kg9zIxZwXdG87bODai6jB88epy4YfHvxNlAk1Ozm8Z8";

    public static void main(String[] args) {
        TwitterClient twitterClient =
            new TwitterClient(TWITTER_KEY, TWITTER_SECRET);
        TagCloud tagCloud = new TagCloud(twitterClient);
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().configure( JsonGenerator.Feature.ESCAPE_NON_ASCII,
                true);

        staticFiles.location("/public");

        get("/tagcloud", "application/json", tagCloud::view,
                mapper::writeValueAsString);

        after((req, res) -> res.type("application/json"));

        exception(HttpException.class, (exception, req, res) -> {
            res.status(((HttpException) exception).getStatus());
            res.type("application/json");
            res.body(String.format("{\"error\":\"%s\"}",
                                   exception.getMessage()));
        });
    }
}
