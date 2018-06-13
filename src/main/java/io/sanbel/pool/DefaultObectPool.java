package io.sanbel.pool;

/**
 * Created by duanjigui on 2018/6/13.
 */
public class DefaultObectPool<T> extends AbstractObjectPool<T> {

    public DefaultObectPool(Class clazz) {
        super(clazz);
    }

    @Override
    public void doClean(T t) {
        System.out.println("对象已经清理！");
    }
}
