package org.xyy.service;

import org.xyy.convert.json.*;

/**
 * 通用的结果对象，在常见的HTTP+JSON接口中返回的结果需要含结果码，错误信息，和实体对象。  <br>
 * 结果码定义通常前四位为模块，后四位为操作。<br>
 * 结果码定义范围:  <br>
 *    // 10000001 - 19999999 预留给XYYRPC的核心包使用  <br>
 *    // 20000001 - 29999999 预留给XYYRPC的扩展包使用  <br>
 *    // 30000001 - 99999999 预留给Dev开发系统自身使用  <br>
 * @param <T> 结果对象的泛型
 */
public class RetResult<T> {

    protected int retcode;

    protected String retinfo;

    protected T result;

    public RetResult() {
    }

    public RetResult(T result) {
        this.result = result;
    }

    public RetResult(int retcode) {
        this.retcode = retcode;
    }

    public RetResult(int retcode, String retinfo) {
        this.retcode = retcode;
        this.retinfo = retinfo;
    }

    public RetResult(int retcode, String retinfo, T result) {
        this.retcode = retcode;
        this.retinfo = retinfo;
        this.result = result;
    }

    public static RetResult success() {
        return new RetResult();
    }

    /**
     * 判断结果是否成功返回， retcode = 0 视为成功， 否则视为错误码
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return retcode == 0;
    }

    /**
     * 同 setRetcode
     *
     * @param retcode retcode
     *
     * @return RetResult
     */
    public RetResult<T> retcode(int retcode) {
        this.retcode = retcode;
        return this;
    }

    /**
     * 同 setRetinfo
     *
     * @param retinfo retinfo
     *
     * @return RetResult
     */
    public RetResult<T> retinfo(String retinfo) {
        this.retinfo = retinfo;
        return this;
    }

    /**
     * 同 setResult
     *
     * @param result result
     *
     * @return RetResult
     */
    public RetResult<T> result(T result) {
        this.result = result;
        return this;
    }

    /**
     * 结果码 0表示成功、 非0表示错误
     *
     * @return 结果码
     */
    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    /**
     * 结果信息，通常retcode != 0时值为错误信息
     *
     * @return 结果信息
     */
    public String getRetinfo() {
        return retinfo;
    }

    /**
     * 设置结果信息
     *
     * @param retinfo 结果信息
     */
    public void setRetinfo(String retinfo) {
        this.retinfo = retinfo;
    }

    /**
     * 结果对象， 通常只有在retcode = 0时值才有效
     *
     * @return 结果对象
     */
    public T getResult() {
        return result;
    }

    /**
     * 设置结果对象
     *
     * @param result T
     */
    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return JsonConvert.root().convertTo(this);
    }
}
