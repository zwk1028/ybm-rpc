package org.xyy.convert;

/**
 * 序列化类型枚举，结合&#64;ConvertColumn使用
 *
 */
public enum ConvertType {

    JSON(1),
    BSON(2),
    ALL(127);

    private final int value;

    private ConvertType(int v) {
        this.value = v;
    }

    public boolean contains(ConvertType type) {
        if (type == null) return false;
        return this.value >= type.value && (this.value & type.value) > 0;
    }
}
