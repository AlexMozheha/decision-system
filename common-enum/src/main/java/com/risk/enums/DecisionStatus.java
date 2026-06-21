package com.risk.enums;

public enum DecisionStatus {

    /*Рішення створюється або редагується, але ще не готове до розрахунку.*/
    DRAFT(1),

    /*Рішення готове до відправки на обчислення.
     Використовується Decision Service перед викликом Calculation Service.*/
    READY_FOR_CALCULATION(2),

    /*Запит надіслано до Calculation Service, і обчислення триває.*/
    CALCULATING(3),

    /*Обчислення успішно завершено, результати збережено.Рішення готове до перегляду та фіналізації.*/
    CALCULATED(4),

    /*Обчислення в Calculation Service не вдалося через внутрішню помилку або невалідні вхідні дані.*/
    CALCULATION_FAILED(5);

    private final int id;

    public int getId() {
        return id;
    }

    DecisionStatus(int id) {
        this.id = id;
    }

    public static DecisionStatus fromId(int id) {
        for (DecisionStatus status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown DecisionStatus id: " + id);
    }
}
