package com.risk.enums;

public enum UserRole {

    INVESTOR(1),
    ADMIN(2);

    private final int id;

    public int getId() {
        return id;
    }

    UserRole(int id) {
        this.id = id;
    }

    public static UserRole fromId(int id) {
        for (UserRole role : values()) {
            if (role.id == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown Role id: " + id);
    }
}
