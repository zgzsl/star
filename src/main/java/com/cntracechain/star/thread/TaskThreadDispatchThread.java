package com.cntracechain.star.thread;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @ClassName TaskThreadDispatchThread
 * @Description 生成三维星阵图
 * @Author chenxw
 * @Date 2019/7/23 18:27
 * @Version 1.0
 **/
public class TaskThreadDispatchThread implements Runnable{

    private static Logger log = Logger.getLogger(TaskThreadDispatchThread.class.getSimpleName());

    private TaskThreadPool taskThreadPool;

    public TaskThreadDispatchThread (TaskThreadPool taskThreadPool) {
        this.taskThreadPool = taskThreadPool;
    }

    @Override
    public void run() {

        int index = taskThreadPool.getIndex();
        int quantity = taskThreadPool.getQuantity();
        StringBuilder sb = new StringBuilder(taskThreadPool.getPath());
        sb.append(File.separator).append(index).append("-").append(index+quantity-1);
        //本次任务生产的三维码存放的目标目录
        File file = Paths.get(sb.toString()).toFile();
        if (!file.exists()) {
            file.mkdir();
        }
        //本次生产三维码总共需要多少个线程（每个线程固定产生1千个三维码）
        long count = taskThreadPool.getCountDownLatch().getCount();
        List<List<Integer>> portion = new ArrayList<>();
        if (count == 1) {
            List<Integer> range = new ArrayList<>();
            range.add(index);
            range.add(index + quantity - 1);
            portion.add(range);
        } else {
            for (int i = 0; i < count; i++) {
                List<Integer> range = new ArrayList<>();
                if (i == count - 1) {
                    if (taskThreadPool.getRemainder() == 0) {
                        range.add(index + i * 1000);
                        range.add(index + (i + 1) * 1000 - 1);
                    } else {
                        range.add(portion.get(portion.size() - 1).get(1) + 1);
                        range.add(portion.get(portion.size() - 1).get(1) + taskThreadPool.getRemainder());
                    }
                } else {
                    range.add(index + i * 1000);
                    range.add(index + (i + 1) * 1000 - 1);
                }
                portion.add(range);
            }
        }
        while (true) {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("当前运行队列个数：" + taskThreadPool.getCountDownNum());
            if (taskThreadPool.getCountDownNum() < 20) {
                if (taskThreadPool.getQueue() < portion.size()) {
                    taskThreadPool.addQueue();
                    List<Integer> range = portion.get(taskThreadPool.getQueue() - 1);
                    taskThreadPool.executor.execute(new Thread(new TaskExecuteThread(file,range,taskThreadPool)));
                    log.info("已创建 " + (taskThreadPool.getQueue()) + "个子任务。。。");
                } else {
                    break;
                }
            }
        }

        try {
            taskThreadPool.getCountDownLatch().await();
            taskThreadPool.executor.shutdown();
            log.info("本批次星阵二维码底图已生产完毕......");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
