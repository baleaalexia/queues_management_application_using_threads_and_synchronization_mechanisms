package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.Task;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SimulationManager implements Runnable{
    //cred ca vine ceva aici idk
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int numberOfServers; //Q
    private int numberOfClients; //N
    private SelectionPolicy policy;

    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private double totalWaitTime = 0.0;
    private double totalServiceTime = 0.0;
    private int peakHour = 0;
    private int maxActiveClients = 0;
    private SimulationFrame frame;

    public SimulationManager(int time, int maxSer, int minSer, int minArr, int maxArr, int nSer, int nClients, SimulationFrame frame, SelectionPolicy policy){
        this.timeLimit = time;
        this.maxProcessingTime = maxSer;
        this.minProcessingTime = minSer;
        this.minArrivalTime = minArr;
        this.maxArrivalTime = maxArr;
        this.numberOfServers = nSer;
        this.numberOfClients = nClients;
        this.frame = frame;
        this.policy = policy;

        scheduler = new Scheduler(numberOfServers);
        scheduler.changeStrategy(this.policy);
        generateNRandomTasks();
    }

    private void generateNRandomTasks() {
        generatedTasks = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= numberOfClients; i++) {
            int processingTime = random.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            generatedTasks.add(new Task(i, arrivalTime, processingTime));
            totalServiceTime += processingTime;
        }

        Collections.sort(generatedTasks);
    }

    @Override
    public void run() {
        try(PrintWriter writer = new PrintWriter(new FileWriter("log.txt"))){
            int currentTime = 0;
            while(currentTime <= timeLimit){
                dispatchTasksForCurrentTime(currentTime);
                updatePeakHour(currentTime);
                logCurrentState(currentTime, writer);

                if(isSimulationComplete()){
                    break;
                }

                Thread.sleep(1000);
                currentTime++;
            }
            logFinalStatistics(writer);
            scheduler.stopAll();
        } catch (IOException | InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    private void dispatchTasksForCurrentTime(int currentTime) {
        List<Task> dispatchedTasks = new ArrayList<>();
        for(Task task : generatedTasks){
            if(task.getArrivalTime() == currentTime){
                int waitTime = scheduler.dispatchTask(task);
                totalWaitTime += waitTime;

                dispatchedTasks.add(task);
            }
        }
        generatedTasks.removeAll(dispatchedTasks);
    }

    private void updatePeakHour(int currentTime){
        int clientsInQueues = 0;
        for(Server server : scheduler.getServers()){
            clientsInQueues += server.getTasks().length;
        }

        if(clientsInQueues > maxActiveClients){
            maxActiveClients = clientsInQueues;
            peakHour = currentTime;
        }
    }

    private void logCurrentState(int currentTime, PrintWriter writer){
        StringBuilder sb = new StringBuilder();
        sb.append("\n-----------------------------------\n");
        sb.append("Time ").append(currentTime).append("\nWaiting clients:\n");

        for(Task t : generatedTasks){
            sb.append(t.toString()).append(" ");
        }
        sb.append("\n-----------------------------------\n\n");

        List<Server> servers = scheduler.getServers();
        for(int i = 0; i < servers.size(); i++) {
            sb.append("Queue ").append(i + 1).append(":\n");
            Task[] currentQueue = servers.get(i).getTasks();
            if (currentQueue.length == 0) {
                sb.append("Closed\n");
            } else {
                for (Task t : currentQueue) {
                    sb.append(t.toString()).append(" ");
                }
                sb.append("\n\n");
            }
        }
            String state = sb.toString();
            writer.print(state);
            if(frame != null){
                SwingUtilities.invokeLater(() -> frame.appendLog(state));
            }
            sb.append("\n\n");
    }

    private boolean isSimulationComplete(){
        if(!generatedTasks.isEmpty()){
            return false;
        }
        for(Server s : scheduler.getServers()){
            if(s.getTasks().length > 0){
                return false;
            }
        }
        return true;
    }

    private void logFinalStatistics(PrintWriter writer){
        double avgW = totalWaitTime / numberOfClients;
        double avgS = totalServiceTime / numberOfClients;

        String stats = "\n\nAverage waiting time: " + avgW + "\n" +
                        "Average service time: " + avgS + "\n" +
                        "Peak hour: " + peakHour + "\n";

        writer.print(stats);
        if(frame != null){
            SwingUtilities.invokeLater(() -> frame.updateStats(avgW, avgS, peakHour));
        }
    }
}