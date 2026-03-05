package autoservice.exception;

public class DaoException extends AutoServiceException {

    public DaoException(String message) {
        super(message, ErrorCodes.SYS_DATABASE);
    }

    public DaoException(String message, Throwable cause) {
        super(message, ErrorCodes.SYS_DATABASE, cause);
    }
}
