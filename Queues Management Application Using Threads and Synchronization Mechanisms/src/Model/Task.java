package Model;

public class Task implements Comparable<Task>{
    private int arrivalTime;
    private int serviceTime;
    private int id;

    public Task(int id, int arrivalTime, int serviceTime){
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getServiceTime(){
        return serviceTime;
    }

    public void setServiceTime(int serviceTime){
        this.serviceTime = serviceTime;
    }

    public int getArrivalTime(){
        return arrivalTime;
    }


    @Override
    public int compareTo(Task o) {
        return Integer.compare(this.arrivalTime, o.arrivalTime);
    }

    @Override
    public String toString(){
        return "(" + id + ", " + arrivalTime + ", " + serviceTime + ")";
    }
}
