package io.sanbel.task;

/**
 * 策略接口
 * Created by duanjigui on 2018/6/8.
 */
public interface Strategy {

     boolean execute();

     void doClean();

}
