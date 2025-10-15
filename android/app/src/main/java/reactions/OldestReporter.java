package reactions;

import dao.model.Message;

import java.util.*;

public class OldestReporter implements IReactionReporter {

    private final ReactionDAO dao = ReactionDAO.getInstance();

    @Override
    public ReactionDisplayTag[] generateReport(Message message) {
        if (message == null) return new ReactionDisplayTag[0];

        // Map user -> their oldest reaction
        Map<UUID, Reaction> oldestByUser = new HashMap<>();

        Iterator<Reaction> it = dao.getAll();
        while (it.hasNext()) {
            Reaction r = it.next();
            if (!r.getMessageId().equals(message.id())) continue;

            Reaction current = oldestByUser.get(r.getUserId());
            if (current == null || r.getTimestamp() < current.getTimestamp()) {
                oldestByUser.put(r.getUserId(), r);
            }
        }

        // Sort by timestamp ascending
        List<Reaction> oldestList = new ArrayList<>(oldestByUser.values());
        oldestList.sort(Comparator.comparingLong(Reaction::getTimestamp));

        return oldestList.stream()
                .limit(5)
                .map(r -> new ReactionDisplayTag(r.getType(), r.getUserId().toString()))
                .toArray(ReactionDisplayTag[]::new);
    }
}
