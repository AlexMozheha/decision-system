package com.risk.enums;

public enum CalculationMethodType {
    METHOD_WS_RISK(1),
    METHOD_OTHER(2);

    private final int id;

    public int getId() {
        return id;
    }

    CalculationMethodType(int id) {
        this.id = id;
    }

    public static CalculationMethodType fromId(int id) {
        for (CalculationMethodType method : values()) {
            if (method.id == id) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown CalculationMethodType id: " + id);
    }
}
