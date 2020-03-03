package task;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScanTask {
    private final ExecutorService  POOL =
            Executors.newFixedThreadPool(4);

    //public static volatile int COUNT = 0;
    private final AtomicInteger COUNT = new AtomicInteger();
    private final CountDownLatch LATCH = new CountDownLatch(1);

    //private FileScanCallback callback;

    public FileScanTask() {

    }


    //测试
   /* public static void main(String[] args) throws InterruptedException {
        FileScanTask task = new FileScanTask();
        task.startScan(new File("d://"));

        synchronized (task) {
            task.wait();
        }
        System.out.println("执行完毕");
    }*/


    public  void startScan(File root) {
//        synchronized (this) {
//            COUNT++;
//        }
        COUNT.incrementAndGet();
        POOL.execute(new Runnable() {
            @Override
            public void run() {
                list(root);
            }
        });
    }

    public void waitFinish() {

        try {
            LATCH.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            POOL.shutdown();
        }
    }

    private  void list(File dir) {
        FileOperateTask callback = new FileOperateTask();
        if (!Thread.interrupted()) {
            try {
                callback.execute(dir);
                //System.out.println(dir.getPath());
                if (dir.isDirectory()) {
                    File[] children = dir.listFiles();
                    if (children != null && children.length > 0) {
                        for (File child : children) {
                            if (child.isDirectory()) {
                                //                            synchronized (this) {
                                //                                COUNT++;
                                //                            }
                                COUNT.incrementAndGet();
                                POOL.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        list(child);
                                    }
                                });
                            }else {
                                callback.execute(child);
                                //System.out.println(child.getPath());
                            }
                        }
                    }
                }
            }catch (Exception i) {
                throw new RuntimeException("线程"+Thread.currentThread().getName()+"正在停止");
            } finally {
                //            synchronized (this) {
                //                COUNT--;
                //                if (COUNT == 0) {
                //                    this.notifyAll();
                //                }
                //            }
                if (COUNT.decrementAndGet() == 0) {
                    LATCH.countDown();
                }
            }
        }
    }
}
