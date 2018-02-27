package org.xyy.convert;

/**
 * 序列化自定义异常类
 *
 */
public class ConvertException extends RuntimeException {

    public ConvertException() {
        super();
    }

    public ConvertException(String s) {
        super(s);
    }

    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertException(Throwable cause) {
        super(cause);
    }
}
