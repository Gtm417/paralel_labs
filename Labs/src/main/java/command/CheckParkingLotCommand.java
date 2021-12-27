package command;

import actor.ParkingLotActor;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;

public class CheckParkingLotCommand implements Command {
    @Override
    public void execute(AbstractActor absActor) {
        ParkingLotActor actor = (ParkingLotActor) absActor;
        ActorRef sender = actor.getSender();
        ActorRef self = actor.getSelf();
        if (actor.getParkedCar() == null) {
            actor.setParkedCar(sender);
            sender.tell(new TakeSlotCommand(), self);
        } else {
            actor.getCarsInQueue().add(sender);
        }
    }
}
