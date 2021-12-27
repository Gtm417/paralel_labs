package actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import command.Command;

import java.util.LinkedList;

public class ParkingLotActor extends AbstractActor {

    private final LinkedList<ActorRef> carsInAQueue;
    private ActorRef parkedCar;
    private final String name;

    public ParkingLotActor(String name){
        this.name = name;
        this.carsInAQueue = new LinkedList<>();
    }

    public static Props props(String name) {
        return Props.create(ParkingLotActor.class, name);
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(Command.class, command -> command.execute(this))
                .build();
    }

    public LinkedList<ActorRef> getCarsInQueue() {
        return carsInAQueue;
    }

    public ActorRef getParkedCar() {
        return parkedCar;
    }

    public void setParkedCar(ActorRef parkedCar) {
        this.parkedCar = parkedCar;
    }

    public String getName() {
        return name;
    }
}
