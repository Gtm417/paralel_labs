package command;

import akka.actor.AbstractActor;

public interface Command {
    void execute(AbstractActor actor);
}
