package org.xyy.source;

/**
 * 函数表达式， 均与SQL定义中的表达式相同
 *
 * <p>
 */
public enum ColumnExpress {
    /**
     * 直接赋值 col = val
     */
    MOV,
    /**
     * 追加值 col = col + val
     */
    INC,
    /**
     * 乘值 col = col * val
     */
    MUL,
    /**
     * 与值 col = col &#38; val
     */
    AND, //与值 col = col & val
    /**
     * 或值 col = col | val
     */
    ORR;
}
