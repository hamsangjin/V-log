package hello.velog.exception;

public class NotCommentOwnerException extends RuntimeException {
    public NotCommentOwnerException(String message) {
        super(message);
    }
}