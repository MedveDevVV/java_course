package autoservice.exception;

public class CsvException extends AutoServiceException {

    public CsvException(String message) {
        super(message, ErrorCodes.VAL_CSV_ERROR);
    }

    public CsvException(String message, Throwable cause) {
        super(message, ErrorCodes.VAL_CSV_ERROR, cause);
    }
}
