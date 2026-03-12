package autoservice.handler;

import autoservice.dto.ErrorResponse;
import autoservice.exception.AutoServiceException;
import autoservice.exception.CsvException;
import autoservice.exception.DaoException;
import autoservice.exception.DuplicateEntityException;
import autoservice.exception.ErrorCodes;
import autoservice.exception.InvalidDateException;
import autoservice.exception.MasterNotAssignedException;
import autoservice.exception.MasterNotFoundException;
import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.OrderNotFoundException;
import autoservice.exception.ResourceBusyException;
import autoservice.exception.ScheduleConflictException;
import autoservice.exception.ValidationException;
import autoservice.exception.WorkshopPlaceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Обрабатывает все исключения и возвращает структурированные ответы.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========== ОБРАБОТКА СВОИХ ИСКЛЮЧЕНИЙ ==========

    @ExceptionHandler(MasterNotAssignedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404 - подресурс не найден
    public ErrorResponse handleMasterNotAssignedException(MasterNotAssignedException ex,
                                                          HttpServletRequest request) {
        log.warn("Мастер не назначен - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOrderNotFoundException(OrderNotFoundException ex,
                                                      HttpServletRequest request) {
        log.warn("Заказ не найден - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());

        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MasterNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMasterNotFoundException(MasterNotFoundException ex,
                                                       HttpServletRequest request) {
        log.warn("Мастер не найден - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(WorkshopPlaceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWorkshopPlaceNotFoundException(WorkshopPlaceNotFoundException ex,
                                                              HttpServletRequest request) {
        log.warn("Рабочее место не найдено - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DuplicateEntityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEntityException(DuplicateEntityException ex,
                                                        HttpServletRequest request) {
        log.warn("Дублирующая сущность - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(InvalidDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDateException(InvalidDateException ex,
                                                    HttpServletRequest request) {
        log.warn("Ошибка валидации даты - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOperationNotAllowedException(OperationNotAllowedException ex,
                                                            HttpServletRequest request) {
        log.warn("Операция не разрешена - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceBusyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleResourceBusyException(ResourceBusyException ex,
                                                     HttpServletRequest request) {
        log.warn("Ресурс занят - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ScheduleConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleScheduleConflictException(ScheduleConflictException ex,
                                                         HttpServletRequest request) {
        log.warn("Конфликт расписания - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException ex,
                                                   HttpServletRequest request) {
        log.warn("Ошибка валидации - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DaoException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDaoException(DaoException ex,
                                           HttpServletRequest request) {
        log.error("Ошибка БД - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                "Ошибка при работе с базой данных",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(CsvException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCsvException(CsvException ex,
                                           HttpServletRequest request) {
        log.warn("Ошибка обработки CSV - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage());
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
        String errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("%s: %s",
                        fieldError.getField(),
                        fieldError.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        log.warn("Ошибка валидации полей - code: {}, path: {}, message: '{}'",
                ErrorCodes.VAL_GENERAL.getCode(),
                request.getRequestURI(),
                errorDetails);

        return new ErrorResponse(
                ErrorCodes.VAL_GENERAL.getCode(),
                "Ошибка валидации входных данных",
                errorDetails,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                   HttpServletRequest request) {
        String expectedType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "неизвестный тип";

        String message = String.format("Параметр '%s' имеет неверный тип. Ожидается: %s",
                ex.getName(), expectedType);

        log.warn("Ошибка типа аргумента - code: {}, path: {}, message: '{}'",
                ErrorCodes.VAL_INVALID_FORMAT.getCode(),
                request.getRequestURI(),
                message);

        log.warn("Ошибка типа аргумента: {}", message);

        return new ErrorResponse(
                ErrorCodes.VAL_INVALID_FORMAT.getCode(),
                message,
                request.getRequestURI()
        );
    }

    // ========== ОБРАБОТКА ОБЩИХ ИСКЛЮЧЕНИЙ ==========

    @ExceptionHandler(AutoServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAutoServiceException(AutoServiceException ex,
                                                    HttpServletRequest request) {

        log.error("Ошибка приложения - code: {}, path: {}, message: '{}'",
                ex.getErrorCode().getCode(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);
        return new ErrorResponse(
                ex.getErrorCode().getCode(),
                "Внутренняя ошибка сервера",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex,
                                                HttpServletRequest request) {
        log.error("Необработанное исключение - code: {}, path: {}, message: '{}'",
                ErrorCodes.SYS_INTERNAL.getCode(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);
        return new ErrorResponse(
                ErrorCodes.SYS_INTERNAL.getCode(),
                "Внутренняя ошибка сервера",
                "Произошла непредвиденная ошибка. Обратитесь к администратору.",
                request.getRequestURI()
        );
    }
}