package org.example.enums;

public enum Reason {
    R00("В обработке"),
    R0("Успешный перевод"),
    R1("Сумма перевода слишком большая. Лицо добавлено в черный список для транзакций"),
    R2("Слишком частые переводы от одного лица. Лицо добавлено в черный список для транзакций"),
    R3("Лицо находится в черном списке для транзакций"),
    R4("Не удалось получить подтверждение по почте");

    private String reason;

    Reason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
