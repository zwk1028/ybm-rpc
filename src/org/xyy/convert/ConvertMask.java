package org.xyy.convert;

/**
 * Mask接口
 *
 */
public interface ConvertMask {

    default byte mask(byte value) {
        return value;
    }

    default byte unmask(byte value) {
        return value;
    }
}
