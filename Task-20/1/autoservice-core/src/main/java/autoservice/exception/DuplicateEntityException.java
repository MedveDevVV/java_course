package autoservice.exception;

/**
 * Исключение, выбрасываемое при попытке создать дублирующую сущность.
 */
public class DuplicateEntityException extends AutoServiceException {

    /**
     * Для дублирующихся записей (уникальные имена и т.д.)
     * %s с %s: '%s' уже существует
     *
     * @param entityName название сущности
     * @param fieldName название поля
     * @param fieldValue данные поля
     */
    public DuplicateEntityException(String entityName, String fieldName, String fieldValue) {
        super(
                String.format("%s с %s: '%s' уже существует", entityName, fieldName, fieldValue),
                ErrorCodes.BIZ_DUPLICATE
        );
    }

    public DuplicateEntityException(String message) {
        super(message, ErrorCodes.BIZ_DUPLICATE);
    }
}