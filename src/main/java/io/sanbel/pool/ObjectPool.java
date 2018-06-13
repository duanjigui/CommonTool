package io.sanbel.pool;

/**
 * 通用对象池接口
 * Created by duanjigui on 2018/6/13.
 */
public interface ObjectPool<T> {

    public T getObject();

    public void returnObject(T t);


}
