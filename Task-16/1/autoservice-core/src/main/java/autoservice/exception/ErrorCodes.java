package autoservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Хранилище всех кодов ошибок приложения.
 * Структура кода: КАТЕГОРИЯ_ПОДКАТЕГОРИЯ_НОМЕР
 */
@Getter
@AllArgsConstructor
public enum ErrorCodes {

    // ========== ОБЩИЕ ОШИБКИ ПРИЛОЖЕНИЯ (SYS) ==========
    SYS_GENERAL("SYS_GENERAL_001", "Общая ошибка приложения"),
    SYS_DATABASE("SYS_DATABASE_001", "Ошибка базы данных"),
    SYS_INTERNAL("SYS_INTERNAL_001", "Внутренняя ошибка сервера"),

    // ========== ОШИБКИ ВАЛИДАЦИИ (VAL) ==========
    VAL_GENERAL("VAL_GENERAL_001", "Ошибка валидации данных"),
    VAL_DATE_RANGE("VAL_DATE_001", "Неверный диапазон дат"),
    VAL_DATE_PAST("VAL_DATE_002", "Дата в прошлом"),
    VAL_REQUIRED_FIELD("VAL_FIELD_001", "Обязательное поле не заполнено"),
    VAL_INVALID_FORMAT("VAL_FORMAT_001", "Неверный формат данных"),

    // ========== ОШИБКИ "НЕ НАЙДЕНО" (NF) ==========
    NF_ORDER("NF_ORDER_001", "Заказ не найден"),
    NF_MASTER("NF_MASTER_001", "Мастер не найден"),
    NF_WORKSHOP_PLACE("NF_PLACE_001", "Рабочее место не найдено"),
    NF_RESOURCE("NF_RESOURCE_001", "Ресурс не найден"),
    NF_MASTER_NOT_ASSIGNED("NF_MASTER_002", "Мастер не назначен на заказ"),

    // ========== ОШИБКИ БИЗНЕС-ЛОГИКИ (BIZ) ==========
    BIZ_DUPLICATE("BIZ_DUPLICATE_001", "Дублирующая сущность"),
    BIZ_SCHEDULE_CONFLICT("BIZ_SCHEDULE_001", "Конфликт в расписании"),
    BIZ_OPERATION_NOT_ALLOWED("BIZ_OPERATION_001", "Операция не разрешена"),
    BIZ_RESOURCE_BUSY("BIZ_RESOURCE_001", "Ресурс занят"),
    BIZ_INVALID_STATE("BIZ_STATE_001", "Неверное состояние объекта"),

    // ========== ОШИБКИ АУТЕНТИФИКАЦИИ/АВТОРИЗАЦИИ (AUTH) ==========
    AUTH_FAILED("AUTH_FAILED_001", "Ошибка аутентификации"),
    AUTH_ACCESS_DENIED("AUTH_ACCESS_001", "Доступ запрещен"),
    AUTH_TOKEN_INVALID("AUTH_TOKEN_001", "Неверный токен"),
    AUTH_TOKEN_EXPIRED("AUTH_TOKEN_002", "Токен истек"),
    AUTH_INSUFFICIENT_PERMISSIONS("AUTH_PERM_001", "Недостаточно прав");

    private final String code;
    private final String description;

    /**
     * Получить категорию ошибки (первая часть кода)
     */
    public String getCategory() {
        return this.code.split("_")[0];
    }

    /**
     * Получить подкатегорию ошибки (вторая часть кода)
     */
    public String getSubcategory() {
        String[] parts = this.code.split("_");
        return parts.length > 1 ? parts[1] : "";
    }

    /**
     * Получить код по его строковому значению
     */
    public static ErrorCodes fromCode(String code) {
        for (ErrorCodes errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        throw new IllegalArgumentException("Неизвестный код ошибки: " + code);
    }

    /**
     * Получить все коды определенной категории
     */
    public static ErrorCodes[] getByCategory(String category) {
        return java.util.Arrays.stream(values())
                .filter(ec -> ec.getCategory().equals(category))
                .toArray(ErrorCodes[]::new);
    }

    /**
     * Проверить, принадлежит ли код к категории
     */
    public boolean isCategory(String category) {
        return this.getCategory().equals(category);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", code, description);
    }
}