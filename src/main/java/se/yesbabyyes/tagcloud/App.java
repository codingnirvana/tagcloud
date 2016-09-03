package se.yesbabyyes.tagcloud;

import static spark.Spark.get;
import se.yesbabyyes.tagcloud.TagCloud;

public class App {
    public static void main(String[] args) {
        get("/", TagCloud::helloWorld);
    }
}
