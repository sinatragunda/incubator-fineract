/*

    Created by Sinatra Gunda
    At 1:42 PM on 8/16/2021

*/
package main.java.org.apache.fineract.wese.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OffshoreThread {

    public static void run(Runnable runnable){

        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        System.err.println("Thread has been executed now");
    }
}

