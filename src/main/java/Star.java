import com.cntracechain.star.thread.TaskThreadPool;

import java.util.Scanner;

/**
 * @ClassName Star
 * @Description TODO
 * @Author chenxw
 * @Date 2019/10/28 17:25
 * @Version 1.0
 **/
public class Star {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("请输入起始参数：");
        int index = Integer.valueOf(scan.nextLine());
        System.out.print("请输入生产数量：");
        int quantity = Integer.valueOf(scan.nextLine());
        System.out.print("请输入图片保存路径：");
        String path = scan.nextLine();
        new TaskThreadPool(index,quantity,600,path);
    }

}
