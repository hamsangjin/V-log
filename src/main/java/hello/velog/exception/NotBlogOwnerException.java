package hello.velog.exception;

public class NotBlogOwnerException extends RuntimeException {
    public NotBlogOwnerException(String message) {
        super(message);
    }
}