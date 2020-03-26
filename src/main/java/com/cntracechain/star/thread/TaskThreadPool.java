package com.cntracechain.star.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @ClassName TaskThreadPool
 * @Description 任务线程池
 * @Author chenxw
 * @Date 2019/7/23 18:22
 * @Version 1.0
 **/
public class TaskThreadPool {

    public int getQuotient() {
        return quotient;
    }

    public void setQuotient(int quotient) {
        this.quotient = quotient;
    }

    public int getRemainder() {
        return remainder;
    }

    public void setRemainder(int remainder) {
        this.remainder = remainder;
    }

    private static Logger log = Logger.getLogger(TaskThreadPool.class.getSimpleName());

    private int countDownNum;

    private CountDownLatch latch;

    private int index;

    private int quantity;

    private int quotient;

    private int remainder;

    private int size;

    private String path;

    //记录执行索引值
    private int queue = 0;

    public ThreadPoolExecutor executor;

    public TaskThreadPool(int index, int quantity, int size, String path) {
        this.index = index;
        this.quantity = quantity;
        this.path = path;
        this.size = size;
        countDownNum = 0;
        quotient = quantity/1000;
        //本次生成的三维码数量不足1000个
        if (quotient <= 1) {
            latch = new CountDownLatch(1);
        } else {
            //本次生成的三维码数量超过1000个
            remainder = quantity%1000;
            if (remainder > 0) {
                latch = new CountDownLatch(1 + quotient);
            } else {
                latch = new CountDownLatch(quotient);
            }
        }
        executor = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        log.info("------ 生成星阵三维码队列启动完成。");
        Thread dispatchThread = new Thread(new TaskThreadDispatchThread(this));
        dispatchThread.start();
        log.info("------ 生成星阵三维码分配线程启动完成。");
    }

    public synchronized void addQueue() {
        queue++;
    }

    public synchronized int getQueue() {
        return queue;
    }

    public synchronized void countDown() {
        countDownNum--;
        latch.countDown();
    }

    public synchronized void setCountDownNum(int num) {
        countDownNum = num;
    }

    public synchronized void addCountDown() {
        countDownNum++;
    }

    public synchronized int getCountDownNum() {
        return countDownNum;
    }

    public CountDownLatch getCountDownLatch() {
        return latch;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
