package com.company.library.enums;

public enum OrderBy {
    ID("id"), TITLE("title"), VALUE("averageStars");
    private String OrderByCode;

    OrderBy(String orderBy) {
        this.OrderByCode = orderBy;
    }

    public String getOrderByCode() {
        return this.OrderByCode;
    }
}