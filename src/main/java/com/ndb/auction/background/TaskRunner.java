package com.ndb.auction.background;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class TaskRunner implements Runnable{

    private final static int MAX_QUEUE_SIZE = 10;

    private Queue<BackgroundTask> taskQueue;

    public TaskRunner() {
        this.taskQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
    }

    public void addNewTask(BackgroundTask task) {
        this.taskQueue.add(task);
    }

    @Override
    public void run() {
        while (true) {
            BackgroundTask task = taskQueue.peek();
            if(task == null) continue;
            task.runTask();
        }
    }
    
}
