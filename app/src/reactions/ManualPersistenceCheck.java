package reactions;

import java.util.List;
import java.util.UUID;

public class ManualPersistenceCheck {
    public static void main(String[] args) {
        UUID user = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID msg  = UUID.fromString("22222222-2222-2222-2222-222222222222");

        if (args.length == 0 || args[0].equalsIgnoreCase("A")) {
            // Phase A: write some events
            long now = System.currentTimeMillis();
            ReactionType t0 = ReactionType.values()[0]; // pick two distinct types without depending on names
            ReactionType t1 = ReactionType.values()[1];

            System.out.println("Phase A: adding reactions");
            System.out.println("add t0: " + ReactionsFacade.addReaction(user, msg, t0, now));
            System.out.println("add t0 again (should be false): " + ReactionsFacade.addReaction(user, msg, t0, now + 1));
            System.out.println("add t1: " + ReactionsFacade.addReaction(user, msg, t1, now + 2));
            System.out.println("remove t0: " + ReactionsFacade.removeReaction(user, msg, t0));
            System.out.println("add t0 after remove: " + ReactionsFacade.addReaction(user, msg, t0, now + 3));

            List<ReactionType> listA = ReactionsFacade.getReactions(user, msg);
            System.out.println("Current reactions (chronological): " + listA);
            System.out.println("Phase A done. Now run Phase B.");
        } else if (args[0].equalsIgnoreCase("B")) {
            // Phase B: reload from persistence
            ReactionsFacade.loadPersistentData();
            List<ReactionType> listB = ReactionsFacade.getReactions(user, msg);
            System.out.println("Phase B: loaded reactions (chronological): " + listB);
        } else {
            System.out.println("Usage: run with arg A or B");
        }
    }
}
