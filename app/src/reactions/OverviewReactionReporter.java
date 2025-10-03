package reactions;

import dao.model.Message;
import java.util.*;


final class OverviewReactionReporter extends AbstractReactionReporter {
    private final ReactionDataSource data = new DefaultReactionDataSource();

    @Override
    protected ReactionDisplayTag[] doGenerate(Message message) {
        List<ReactionDataSource.ReactionRecord> rs = data.listEffectiveReactions(message);
        if (rs.isEmpty()) return new ReactionDisplayTag[0];

        EnumMap<ReactionType, Integer> freq = new EnumMap<>(ReactionType.class);
        EnumMap<ReactionType, Long> firstSeen = new EnumMap<>(ReactionType.class);

        for (var r : rs) {
            freq.put(r.type, freq.getOrDefault(r.type, 0) + 1);
            firstSeen.compute(r.type, (t, old) -> (old == null || r.timestampNanos < old) ? r.timestampNanos : old);
        }
        if (freq.isEmpty()) return new ReactionDisplayTag[0];

        List<Map.Entry<ReactionType, Integer>> entries = new ArrayList<>(freq.entrySet());
        entries.sort((a, b) -> {
            int c = Integer.compare(b.getValue(), a.getValue()); // 次数降序
            if (c != 0) return c;
            long ta = firstSeen.get(a.getKey());
            long tb = firstSeen.get(b.getKey());
            return Long.compare(ta, tb); // 首次出现更早的优先
        });

        int n = Math.min(5, entries.size());
        ReactionDisplayTag[] out = new ReactionDisplayTag[n];
        for (int i = 0; i < n; i++) {
            var e = entries.get(i);
            out[i] = new ReactionDisplayTag(e.getKey(), String.valueOf(e.getValue()));
        }
        return out;
    }
}
