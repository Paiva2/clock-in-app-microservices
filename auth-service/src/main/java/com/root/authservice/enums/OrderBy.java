package com.root.authservice.enums;

public enum OrderBy {
    ASC("asc"), DESC("desc");

    private final String orderBy;

    OrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderByValue() {
        return this.orderBy;
    }
}
