package autoservice.exception;

public class OrderNotFoundException extends AutoServiceException{
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
