import sun.misc.Unsafe;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: ZhangJun
 * @Date: 2018/10/12 10:17
 * @Description:
 */
public class Content {

    static long sync;
    static long cas = sync;
    static long normal = sync;
    private static CyclicBarrier cyclicBarrier;

    private static Integer n = 0;

    private static AtomicInteger atomicInteger = new AtomicInteger(1);

    private static Unsafe unsafe;

    private static Executor executor = Executors.newFixedThreadPool(100);

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        cyclicBarrier = new CyclicBarrier(100);

//        unsafe=Unsafe.getUnsafe();


        for (int i = 0; i < 1000; i++) {

            executor.execute(new Runnable() {


                public void run() {
                    long t = System.currentTimeMillis();
                    for(int i=0;i<100;i++) {
                        executor.execute(new Runnable() {
                            public void run() {
                                synchronized (n) {
                                    n = n++;
                                }
                            }
                        });
                        sync += System.currentTimeMillis() - t;
                    }

                    n=0;

                    long t1 = System.currentTimeMillis();
                            for(int i=0;i<100;i++){
                                executor.execute(new Runnable() {
                                    public void run() {
                                        atomicInteger.getAndAdd(1);
                                    }
                                });
                            }
                            cas += System.currentTimeMillis() - t1;

                    //System.out.println(n);


                    final long t2 = System.currentTimeMillis();
                    for(int i=0;i<100;i++) {
                        executor.execute(new Runnable() {
                            public void run() {
                                n = n + 1;
                                normal += System.currentTimeMillis() - t2;
                            }
                        });
                    }

                }
            });
            if (i == 1000 - 1) {
                System.out.println("normal: " + normal);
                System.out.println("cas: " + cas);
                System.out.println("sync: " + sync);
            }

        }

        cyclicBarrier.await();


    }

}
