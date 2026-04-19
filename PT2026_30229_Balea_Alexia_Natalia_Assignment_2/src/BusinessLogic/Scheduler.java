package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers){
        this.maxNoServers = maxNoServers;
        this.servers = new ArrayList<>();

        for( int i = 0; i < maxNoServers; i++){
            Server server = new Server();
            servers.add(server);
            Thread t = new Thread(server);
            t.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy){
        if(policy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ShortestQueueStrategy();
        }
        if(policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new ShortestTimeStrategy();
        }
    }

    public int dispatchTask(Task t){
        return strategy.addTask(servers, t);
    }

    public List<Server> getServers(){
        return servers;
    }

    public void stopAll(){
        for(Server s : servers){
            s.stop();
        }
    }

}
