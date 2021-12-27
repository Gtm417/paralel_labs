package command;

import actor.CarActor;
import akka.actor.AbstractActor;

public class NoSlotsCommand implements Command {
    private final CarActor actor;

    public NoSlotsCommand(CarActor actor) {
        this.actor = actor;
    }

    @Override
    public void execute(AbstractActor absActor) {
        CarActor actor = (CarActor) absActor;
        if (actor.getParkedSlot() == null) {
            System.out.println("No Slots I'm leaving:  " + actor.getName());
            actor.getContext().getSystem().stop(actor.getSelf());
        }
    }
}
