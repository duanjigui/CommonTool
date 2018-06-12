package io.sanbel.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 任务重试类
 * Created by duanjigui on 2018/6/7.
 */
public abstract class AbstractRetryTask implements Task{

   private  ScheduledExecutorService service;

   private long duration =60*1000;  //执行多长时间之后自动关闭

    private long delay =3;  //每隔多长时间执行一次任务 单位 s

    private long max_num =-1; //最大重试次数

    public AbstractRetryTask() {
        service= Executors.newSingleThreadScheduledExecutor();
    }

    public AbstractRetryTask(long duration, long delay) {
        this.duration = duration;
        this.delay = delay;
        service= Executors.newSingleThreadScheduledExecutor();
    }
    public AbstractRetryTask(long duration, long delay, long max_num) {
        this.duration = duration;
        this.delay = delay;
        this.max_num = max_num;
        service= Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void doTask() {

        final CountDownLatch countDownLatch =new CountDownLatch(1);

        final Long[] startTimeStamp = {null};

        service.scheduleAtFixedRate(new Runnable() {

            private int i=0;

            @Override
            public void run() {

                if (max_num>0&&i>=max_num){ //最大重试   重试次数大于最大重试次数
                    shutDownTask(service,countDownLatch,i,startTimeStamp);
                    return;
                }
                if (startTimeStamp[0] ==null){
                    startTimeStamp[0] =System.currentTimeMillis();
                }else {
                    if (duration>=0){
                        if (System.currentTimeMillis()-startTimeStamp[0]>duration){  //重发次数的时间大于规定时间
                            System.out.println("重发时间达到上限，开始停止操作");
                            shutDownTask(service,countDownLatch,i,startTimeStamp);
                            return;
                        }
                    }
                    boolean flag= execute();
                    i++;
                    System.out.println("执行重发操作! 当前时间戳:"+System.currentTimeMillis());
                  if (flag){  //如果执行成功的话
                      shutDownTask(service,countDownLatch,i,startTimeStamp);
                      return;
                  }
                }
            }
        },0,delay, TimeUnit.SECONDS);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("当前任务执行完毕！");
    }


    //执行具体的操作，返回执行结果
    protected abstract boolean execute();

    //清理操作（如关闭订单等操作）
    protected abstract void doClean();


    //关闭任务操作
    private void shutDownTask(ScheduledExecutorService service, CountDownLatch countDownLatch,int i,Long[] startTimeStamp){
        doClean();
        System.out.println("统计数据:");
        System.out.print("当前任务重复执行了 "+i+" 次");
        System.out.print(" 执行时间 "+(System.currentTimeMillis() -startTimeStamp[0])/1000 +" s");
        System.out.println();
        service.shutdownNow();
        countDownLatch.countDown();
    }

    protected void setDuration(long duration) {
        this.duration = duration;
    }

    protected void setDelay(long delay) {
        this.delay = delay;
    }

    protected void setMax_num(long max_num) {
        this.max_num = max_num;
    }
}
