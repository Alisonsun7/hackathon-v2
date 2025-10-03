package reactions;

import dao.model.Message;
import java.util.*;
import reactions.ReactionsFacade;


final class DefaultReactionDataSource implements ReactionDataSource {
    @Override
    public List<ReactionRecord> listEffectiveReactions(Message message) {
        if (message == null) return List.of();

        Map<UUID, Map<ReactionType, Long>> snap = ReactionsFacade._snapshotForMessage(message.id());
        if (snap.isEmpty()) return List.of();

        int size = 0;
        for (var m : snap.values()) size += m.size();
        List<ReactionRecord> out = new ArrayList<>(size);

        for (var e : snap.entrySet()) {
            UUID userId = e.getKey();
            for (var r : e.getValue().entrySet()) {
                out.add(new ReactionRecord(
                        userId,             // userId
                        message.id(),       // messageId
                        r.getKey(),         // type
                        r.getValue()        // timestampNanos
                ));
            }
        }
        return out;
    }
}
