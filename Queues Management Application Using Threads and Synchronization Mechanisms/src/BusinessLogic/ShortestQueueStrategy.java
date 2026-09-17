package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {

    @Override
    public int addTask(List<Server> servers, Task t) {
        Server bestServer = servers.get(0);
        for(Server server : servers){
            if(server.getTasks().length < bestServer.getTasks().length){
                bestServer = server;
            }
        }

        int waitTime = bestServer.getWaitingPeriod();
        bestServer.addTask(t);
        return waitTime;
    }
}
