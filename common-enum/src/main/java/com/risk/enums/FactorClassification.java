package com.risk.enums;

public enum FactorClassification {
    /*Об'єктивний фактор оперує обчислювальними значеннями(прибутки, витрати й т.д.)*/
    OBJECTIVE(1),

    /*Суб'єктивний оперує суб'єктивними значеннями цінність яких оцінює експерт*/
    SUBJECTIVE(2);

    private final int id;

    public int getId() {
        return id;
    }

    FactorClassification(int id) {
        this.id = id;
    }

    public static FactorClassification fromId(int id) {
        for (FactorClassification classification : values()) {
            if (classification.id == id) {
                return classification;
            }
        }
        throw new IllegalArgumentException("Unknown FactorClassification id: " + id);
    }
}
