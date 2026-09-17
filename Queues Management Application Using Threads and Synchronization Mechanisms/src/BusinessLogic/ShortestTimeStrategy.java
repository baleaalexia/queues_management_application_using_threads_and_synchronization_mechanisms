package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;

public class ShortestTimeStrategy implements Strategy {

    @Override
    public int addTask(List<Server> servers, Task t) {
        Server bestServer = servers.get(0);
        for(Server server : servers){
            if(server.getWaitingPeriod() < bestServer.getWaitingPeriod()){
                bestServer = server;
            }
        }
        int waitTime = bestServer.getWaitingPeriod();
        bestServer.addTask(t);
        return waitTime;
    }
}
