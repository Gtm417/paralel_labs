import actor.CarActor;
import actor.ParkingLotActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Main {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("parking");

        ActorRef[] parkingSlot = new ActorRef[3];
        parkingSlot[0] = system.actorOf(ParkingLotActor.props("SLOT-1"), "slot_1");
        parkingSlot[1] = system.actorOf(ParkingLotActor.props("SLOT-2"), "slot_2");
        parkingSlot[2] = system.actorOf(ParkingLotActor.props("SLOT-3"), "slot_3");

        new Thread(carCreator(system, parkingSlot)).start();
    }

    private static Runnable carCreator(ActorSystem system, ActorRef[] parkSlots) {
        return () -> {
            for (int i = 0;  ; i++) {
                try {
                    Thread.sleep((int) (Math.random() * 1000) + 500);
                    system.actorOf(CarActor.props(parkSlots, "CAR-" + i), "car_" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
