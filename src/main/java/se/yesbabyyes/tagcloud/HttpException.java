package se.yesbabyyes.tagcloud;

import java.lang.Exception;
import java.lang.Throwable;

public class HttpException extends Exception {
    private int status;

    public HttpException(int status, String message) {
        super(message);
        this.status = status;
    }

    public HttpException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
