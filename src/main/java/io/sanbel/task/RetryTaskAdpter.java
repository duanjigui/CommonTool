package io.sanbel.task;

/**
 * Created by duanjigui on 2018/6/12.
 */
public class RetryTaskAdpter extends AbstractRetryTask {

    private Strategy strategy;

    private long duration = -1; //持续的时常，即多长时间之后关闭

    private long delay = 3; //延迟时间

    private long max_num = -1;  //最大尝试次数

    public RetryTaskAdpter strategy(Strategy strategy){
        this.strategy=strategy;
        return this;
    }
    public RetryTaskAdpter duration(long duration){
        this.duration=duration;
        return this;
    }
    public RetryTaskAdpter delay(long delay){
        this.delay=delay;
        return this;
    }
    public RetryTaskAdpter max_num(long max_num){
        this.max_num=max_num;
        return this;
    }
    //构建
    public RetryTaskAdpter build(){
        super.setDelay(this.delay);
        super.setMax_num(this.max_num);
        super.setDuration(this.duration);
        return this;
    }


    @Override
    protected boolean execute() {
        return strategy.execute();
    }

    @Override
    protected void doClean() {
        strategy.doClean();
    }

    public void start(){
        this.doTask();
    }
}
