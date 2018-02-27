package org.xyy.net;

import java.io.IOException;
import org.xyy.util.*;

/**
 * 协议拦截器类
 * @author evanliux
 * 	@param <C> Context的子类型
 * 	@param <R> Request的子类型
 * 	@param <P> Response的子类型
 */
public abstract class Filter<C extends Context, R extends Request<C>, P extends Response<C, R>> implements Comparable {

    AnyValue _conf; //当前Filter的配置

    Filter<C, R, P> _next; //下一个Filter，链表结构

    public void init(C context, AnyValue config) {
    }

    public abstract void doFilter(R request, P response) throws IOException;

    public void destroy(C context, AnyValue config) {
    }

    /**
     * 值越小越靠前执行
     *
     * @return int
     */
    public int getIndex() {
        return 0;
    }

    @Override
    public final int compareTo(Object o) {
        if (!(o instanceof Filter)) return 1;
        return this.getIndex() - ((Filter) o).getIndex();
    }
}
