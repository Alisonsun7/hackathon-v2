package reactions;

import dao.model.Message;
import java.util.*;


final class OldestReactionReporter extends AbstractReactionReporter {
    private final ReactionDataSource data = new DefaultReactionDataSource();

    @Override
    protected ReactionDisplayTag[] doGenerate(Message message) {
        List<ReactionDataSource.ReactionRecord> rs = data.listEffectiveReactions(message);
        if (rs.isEmpty()) return new ReactionDisplayTag[0];

        Map<String, ReactionDataSource.ReactionRecord> earliestByUser = new HashMap<>(Math.max(16, rs.size()/4));
        for (var r : rs) {
            var existed = earliestByUser.get(r.username);
            if (existed == null || r.timestampNanos < existed.timestampNanos) {
                earliestByUser.put(r.username, r);
            }
        }
        if (earliestByUser.isEmpty()) return new ReactionDisplayTag[0];

        // 升序取前 5
        List<ReactionDataSource.ReactionRecord> list = new ArrayList<>(earliestByUser.values());
        list.sort(Comparator.comparingLong(a -> a.timestampNanos));

        int n = Math.min(5, list.size());
        ReactionDisplayTag[] out = new ReactionDisplayTag[n];
        for (int i = 0; i < n; i++) {
            var r = list.get(i);
            out[i] = new ReactionDisplayTag(r.type, r.username);
        }
        return out;
    }
}
