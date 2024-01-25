package org.example.myThreads;
import java.util.concurrent.*;
public class MyMultiThread {
    public static void exeInAnyOrderWithoutExecutorService(){
        Thread blue = new Thread(MyMultiThread::countDown);
        Thread yellow = new Thread(MyMultiThread::countDown, ThreadColor.ANSI_YELLOW.name());
        Thread red = new Thread(MyMultiThread::countDown, ThreadColor.ANSI_RED.name());

        blue.start();
        yellow.start();
        red.start();
        // Run In
        try {
            blue.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            yellow.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            red.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void exeInOrderWithoutExecutorService(){
        Thread blue = new Thread(MyMultiThread::countDown);
        Thread yellow = new Thread(MyMultiThread::countDown, ThreadColor.ANSI_YELLOW.name());
        Thread red = new Thread(MyMultiThread::countDown, ThreadColor.ANSI_RED.name());

        blue.start();
        // Run In
        try {
            blue.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        yellow.start();
        try {
            yellow.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        red.start();
        try {
            red.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void exeInOrderExecutorService(){
        var blueExecutor = Executors.newSingleThreadExecutor();
        blueExecutor.execute(MyMultiThread::countDown);
        blueExecutor.shutdown();

        boolean isDone = false;
        try {
            isDone = blueExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (isDone) {
            System.out.println("Blue finished, starting Yellow");
            var yellowExecutor = Executors.newSingleThreadExecutor(
                    new ColorThreadFactory(ThreadColor.ANSI_YELLOW)
            );
            yellowExecutor.execute(MyMultiThread::countDown);
            yellowExecutor.shutdown();

            try {
                isDone = yellowExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (isDone) {
                System.out.println("Yellow finished, starting Red");
                var redExecutor = Executors.newSingleThreadExecutor(
                        new ColorThreadFactory(ThreadColor.ANSI_RED)
                );
                redExecutor.execute(MyMultiThread::countDown);
                redExecutor.shutdown();
                try {
                    isDone = redExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (isDone) {
                    System.out.println("All processes completed");
                }
            }
        }
    }

    public static void exeInAnyOrderExecutorService(){
        var blueExecutor = Executors.newSingleThreadExecutor();
        blueExecutor.execute(MyMultiThread::countDown);
        blueExecutor.shutdown();

        var yellowExecutor = Executors.newSingleThreadExecutor(
                new ColorThreadFactory(ThreadColor.ANSI_YELLOW)
        );
        yellowExecutor.execute(MyMultiThread::countDown);
        yellowExecutor.shutdown();

        var redExecutor = Executors.newSingleThreadExecutor(
                new ColorThreadFactory(ThreadColor.ANSI_RED)
        );
        redExecutor.execute(MyMultiThread::countDown);
        redExecutor.shutdown();
    }

    //NOT Required we can execute any method with logic
    // This only used to show different colors and see != threads running
    private static void countDown() {

        String threadName = Thread.currentThread().getName();
        var threadColor = ThreadColor.ANSI_RESET;
        try {
            threadColor = ThreadColor.valueOf(threadName.toUpperCase());
        } catch (IllegalArgumentException ignore) {
            // User may pass a bad color name, Will just ignore this error.
        }

        String color = threadColor.color();
        for (int i = 20; i >= 0; i--) {
            System.out.println(color + " " +
                    threadName.replace("ANSI_", "") + "  " + i);
        }
    }
}
