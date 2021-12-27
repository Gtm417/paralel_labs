package actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import command.CheckParkingLotCommand;
import command.Command;
import command.NoSlotsCommand;

import java.time.Duration;

public class CarActor extends AbstractActor {

    private ActorRef[] parkingSlots;
    private ActorRef parkedSlot;
    private String name;

    public CarActor(ActorRef[] parkingSlots, String name) {
        System.out.println("New car has just arrived " + name);
        this.name = name;
        this.parkingSlots = parkingSlots;
        for (ActorRef parkingSlot : parkingSlots) {
            if (parkedSlot == null) {
                parkingSlot.tell(new CheckParkingLotCommand(), getSelf());
            }
        }
        if (parkedSlot == null) {
            getContext().system().scheduler().scheduleOnce(
                    Duration.ofMillis((int) (Math.random() * 2000) + 2000),
                    getSelf(),
                    new NoSlotsCommand(this),
                    getContext().dispatcher(),
                    getSelf()
            );
        }
    }

    public static Props props(ActorRef[] parkPlaces, String name) {
        return Props.create(CarActor.class, (Object) parkPlaces, name);
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(Command.class, command -> command.execute(this))
                .build();
    }

    public ActorRef getParkedSlot() {
        return parkedSlot;
    }

    public void setParkedSlot(ActorRef parkedSlot) {
        this.parkedSlot = parkedSlot;
    }

    public String getName() {
        return name;
    }
}
