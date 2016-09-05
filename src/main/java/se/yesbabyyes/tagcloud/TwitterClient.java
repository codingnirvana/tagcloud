package se.yesbabyyes.tagcloud;

import java.io.IOException;
import java.util.*;
import java.util.stream.*;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.*;

public class TwitterClient {
    private String key;
    private String secret;
    private String token;

    /**
     * Construct a TwitterClient.
     *
     * @param   key     Twitter API consumer key
     * @param   secret  Twitter API consumer secret
     */
    public TwitterClient(String key, String secret) {
        this.key = key;
        this.secret = secret;

        this.init();
    }

    /**
     * Initialize unirest and fetch token for Twitter authentication.
     */
    public void init() {
        initUnirestObjectMapper();
        try {
            token = fetchToken();
        } catch (UnirestException e) {
            throw new RuntimeException("Unable to authorize with Twitter", e);
        }
    }

    /**
     * Return a collection of tweets with the supplied hashtag from
     * Twitter's API.
     *
     * @param   hashtag     The hashtag to search for
     * @return              A collection of tweets matching the hashtag
     */
    public Map search(String hashtag) throws HttpException {
        try {
            return Unirest.get("https://api.twitter.com/1.1/search/tweets.json")
                .header("Authorization", "Bearer " + token)
                .queryString("q", hashtag)
                .asObject(HashMap.class)
                .getBody();
        } catch (UnirestException e) {
            throw new HttpException(503,
                    "Service temporarily unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * Return a stream of tweet contents.
     *
     * @param   tweets  A collection of statuses from a JSON response
     * @return          A stream of the tweet contents
     */
    public static Stream<String> getTweetContents(Map tweets) {
        return ((Collection) tweets.get("statuses"))
            .stream()
            .map(tweet -> ((Map) tweet).get("text"));
    }

    /**
     * Authorize with Twitter using credentials and return bearer token.
     *
     * @return  A bearer token for application-only authentication
     */
    String fetchToken() throws UnirestException {
        HttpResponse<JsonNode> response =
            Unirest.post("https://api.twitter.com/oauth2/token")
                .header("Authorization", "Basic " + credentials())
                .header("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8")
                .field("grant_type", "client_credentials")
                .asJson();
        return response.getBody().getObject().getString("access_token");
    }

    /**
     * Initialize Unirest Object Mapper, using Jackson to marshall objects to
     * and from JSON.
     */
    void initUnirestObjectMapper() {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                        = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Base64 encode the API key and secret according to Twitter's API.
     *
     * @return      this.key concatenated with ":" and this.secret, then
     *              bas64 encoded
     */
    String credentials() {
        // FIXME: key and secret should be RFC 1738 URL encoded first, but
        // only for future eventualities, so it's omitted for now.
        return Base64.getUrlEncoder()
            .encodeToString((key + ":" + secret).getBytes());
    }
}
