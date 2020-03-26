package com.cntracechain.star.thread;

import com.cntracechain.star.qrCode.QrCodeCreate;
import com.cntracechain.star.qrCode.QrCodeHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.util.List;
import java.util.logging.Logger;

/**
 * @ClassName TaskExecuteThread
 * @Description 执行生成三维星阵码
 * @Author chenxw
 * @Date 2019/7/23 18:44
 * @Version 1.0
 **/
public class TaskExecuteThread implements Runnable {

    private static Logger log = Logger.getLogger(TaskExecuteThread.class.getSimpleName());

    private List<Integer> range;
    private File targetDirectory;
    private TaskThreadPool taskThreadPool;

    @Override
    public void run() {
        taskThreadPool.addCountDown();
        log.info("执行队列+1");
        try {
            sync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskThreadPool.countDown();
        log.info("尚有" + taskThreadPool.getCountDownLatch().getCount() + "个子任务需要执行。。。");
    }

    public TaskExecuteThread(File targetDirectory, List<Integer> range, TaskThreadPool taskThreadPool){
        this.range = range;
        this.targetDirectory = targetDirectory;
        this.taskThreadPool = taskThreadPool;
    }

    private void sync() throws IOException {
        String directory = targetDirectory.getAbsolutePath().concat(File.separator);
        try {
            for (int i = range.get(0); i <= range.get(1); i++) {
                String imgPath = directory.concat(File.separator) + i + ".bmp";
                String encoderContent = "https://2641.cn/" + i;
                QrCodeHandler handler = new QrCodeCreate();
                handler.encoderQRCodeWithoutBackground(encoderContent,new FileOutputStream(imgPath),taskThreadPool.getSize());
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
