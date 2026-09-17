package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server  implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private boolean running;

    public Server(){
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.running = true;
    }

    public void addTask(Task newTask){
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    public Task[] getTasks(){
        Task[] taskArray = new Task[tasks.size()];
        return tasks.toArray(taskArray);
    }

    public int getWaitingPeriod(){
        return waitingPeriod.get();
    }

    public void stop(){
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Task currentTask = tasks.peek();
                if (currentTask != null) {
                    currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                    waitingPeriod.decrementAndGet();

                    if (currentTask.getServiceTime() <= 0) {
                        tasks.poll();
                    }

                    Thread.sleep(1000);

                } else {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}
