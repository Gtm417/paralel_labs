package command;

import actor.CarActor;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.time.Duration;

public class TakeSlotCommand implements Command {
    @Override
    public void execute(AbstractActor abstractActor) {
        CarActor actor = (CarActor) abstractActor;
        ActorRef sender = actor.getSender();
        if (actor.getParkedSlot() == null) {
            actor.setParkedSlot(sender);
            System.out.println(actor.getName() + " took spot: " + actor.getParkedSlot());
            scheduleLeaving(actor);
        } else {
            sender.tell(new ReleaseSlotCommand(), actor.getSelf());
        }
    }

    private void scheduleLeaving(CarActor actor) {
        actor.getContext().system().scheduler().scheduleOnce(Duration.ofMillis((int) (Math.random() * 3000) + 3000),
                actor.getParkedSlot(), new ReleaseSlotCommand(), actor.getContext().dispatcher(), actor.getSelf());
    }
}
