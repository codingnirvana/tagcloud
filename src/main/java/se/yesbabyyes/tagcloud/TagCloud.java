package se.yesbabyyes.tagcloud;

import spark.Request;
import spark.Response;

public class TagCloud {
    public static Object helloWorld(Request req, Response res) {
        return "Tag Cloud!";
    }
}
