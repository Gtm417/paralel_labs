package command;

import actor.ParkingLotActor;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.LinkedList;

public class ReleaseSlotCommand implements Command {

    @Override
    public void execute(AbstractActor abstractActor) {
        ParkingLotActor actor = (ParkingLotActor) abstractActor;
        releaseParkSlot(actor);
        notifyNextCar(actor);
    }

    public void releaseParkSlot(ParkingLotActor actor) {
        actor.setParkedCar(null);
    }

    public void notifyNextCar(ParkingLotActor actor) {
        LinkedList<ActorRef> carsInAQueue = actor.getCarsInQueue();
        for (int i = 0; i < carsInAQueue.size(); i++) {
            if (carsInAQueue.getFirst().isTerminated()) {
                carsInAQueue.removeFirst();
            } else {
                actor.setParkedCar(carsInAQueue.getFirst());
                carsInAQueue.getFirst().tell(new TakeSlotCommand(), actor.getSelf());
                carsInAQueue.removeFirst();
                return;
            }
        }
    }
}
