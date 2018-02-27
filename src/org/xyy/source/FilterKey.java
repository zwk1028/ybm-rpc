package org.xyy.source;

import java.util.Objects;

/**
 * FilterKey主要用于自身字段间的表达式, 如： a.recordid = a.parentid , a.parentid就需要FilterKey来表示 new FilterKey("parentid")
 *   <br>
 * 注意：该类型不支持表达式：FV_XXX、BETWEEN、NOTBETWEEN、IN、NOTIN
 *
 */
public class FilterKey implements java.io.Serializable {

    private final String column;

    @java.beans.ConstructorProperties({"column"})
    public FilterKey(String column) {
        this.column = Objects.requireNonNull(column);
    }

    public String getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "a." + getColumn();
    }

}
