package org.xyy.source;

/**
 * 常见的SQL聚合函数
 *
 */
public enum FilterFunc {
    AVG, //平均值
    COUNT, //总数
    DISTINCTCOUNT, //去重总数
    MAX, //最大值
    MIN, //最小值
    SUM; //求和

    public String getColumn(String col) {
        if (this == DISTINCTCOUNT) return "COUNT(DISTINCT " + col + ")";
        return this.name() + "(" + col + ")";
    }
}
