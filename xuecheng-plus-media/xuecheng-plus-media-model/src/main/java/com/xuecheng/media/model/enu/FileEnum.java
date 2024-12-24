package com.xuecheng.media.model.enu;

public enum FileEnum {
    IMAGE(1),
    VIDEO(2),
    OTHER(3);

    FileEnum(Integer i) {

    }

    private Integer v;

    public Integer getValue() {
        return v;
    }

}
