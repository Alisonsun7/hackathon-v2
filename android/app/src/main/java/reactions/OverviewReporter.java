package reactions;

import dao.model.Message;

import java.util.*;

public class OverviewReporter implements IReactionReporter {

    private final ReactionDAO dao = ReactionDAO.getInstance();

    @Override
    public ReactionDisplayTag[] generateReport(Message message) {
        if (message == null) return new ReactionDisplayTag[0];

        Map<ReactionType, Integer> counts = new HashMap<>();
        Map<ReactionType, Long> firstSeen = new HashMap<>();

        Iterator<Reaction> it = dao.getAll();
        while (it.hasNext()) {
            Reaction r = it.next();
            if (!r.getMessageId().equals(message.id())) continue;

            counts.merge(r.getType(), 1, Integer::sum);
            firstSeen.putIfAbsent(r.getType(), r.getTimestamp());
        }

        return counts.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getValue(), a.getValue());
                    if (cmp != 0) return cmp;
                    // tie-breaker: earliest timestamp
                    return Long.compare(
                            firstSeen.getOrDefault(a.getKey(), Long.MAX_VALUE),
                            firstSeen.getOrDefault(b.getKey(), Long.MAX_VALUE)
                    );
                })
                .limit(5)
                .map(e -> new ReactionDisplayTag(e.getKey(), String.valueOf(e.getValue())))
                .toArray(ReactionDisplayTag[]::new);
    }
}
