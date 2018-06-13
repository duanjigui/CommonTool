package io.sanbel.pool;

import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by duanjigui on 2018/6/13.
 */
public abstract class AbstractObjectPool<T> implements ObjectPool<T> {

    private BlockingQueue<T> pool;

    private int capacity; //默认创建的n个容量

    private final static int DEAFULT_CAPACITY=10; //缓存池的默认容量

    private AtomicInteger current_capacity =new AtomicInteger(0);//当前容量

    private Class clazz;

    public AbstractObjectPool(Class clazz) {
        this(DEAFULT_CAPACITY,clazz);
    }

    public AbstractObjectPool(int capacity,Class clazz) {
        this.clazz=clazz;
        this.capacity = capacity;
        pool=new ArrayBlockingQueue<T>(capacity);
        initPool(this.clazz); //初始化n个对象
    }

    private void  initPool(Class clazz){
        //初始化创建n个对象
        try {
            for (int i =0 ;i<capacity;i++){
                 T t= (T) clazz.newInstance();
                pool.put(t);
                current_capacity.incrementAndGet();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T getObject() {
        T t=null;
        try {
           t= pool.take();
            current_capacity.decrementAndGet();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return t;
    }
    //返回对象池需要做一些清理操作
    @Override
    public void returnObject(T t) {
        try {
            if (getCapacity()>=capacity){
                System.out.println("当前对象池中对象已满！放弃入池");
            }else {
                doClean(t);
                pool.put(t);
                current_capacity.incrementAndGet();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //获取当前池中对象的数目
    public int getCapacity(){
        return current_capacity.get();
    }

    //获取泛型类,有缺陷
    @Deprecated
    protected Class getCurrentTargetClass() throws ClassNotFoundException {
        Type type= getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType){
           Type[] t= ((ParameterizedType) type).getActualTypeArguments();
            TypeVariableImpl variable= (TypeVariableImpl) t[0];
            String className=variable.getName();
            return Class.forName(className);
        }else {
            return Object.class;
        }
    }

    //执行对象属性置空操作[具体的类可以进行重写该方法]
    public abstract void doClean(T t);


}
