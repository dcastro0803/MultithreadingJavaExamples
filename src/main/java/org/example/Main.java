package org.example;

import org.example.myThreads.MyMultiThread;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        //Check and use other methods inside classes
        MyMultiThread.exeInAnyOrderExecutorService();
    }
}