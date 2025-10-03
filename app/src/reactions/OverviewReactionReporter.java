package reactions;

import dao.model.Message;
import java.util.List;
import java.util.Arrays;

final class OverviewReactionReporter extends AbstractReactionReporter {
    private static final int TOP_K = 5;
    private final ReactionDataSource data = new DefaultReactionDataSource();

    @Override
    protected ReactionDisplayTag[] doGenerate(Message message) {
        List<ReactionDataSource.ReactionRecord> rs = data.listEffectiveReactions(message);
        if (rs.isEmpty()) return new ReactionDisplayTag[0];

        // 统计：频次 + 最早时间（Long.MAX_VALUE 表示尚未出现）
        final ReactionType[] types = ReactionType.values();
        final int T = types.length;
        int[] counts = new int[T];
        long[] firstSeen = new long[T];
        Arrays.fill(firstSeen, Long.MAX_VALUE);

        for (var r : rs) {
            int idx = r.type().ordinal();
            counts[idx]++;
            long ts = r.timestampNanos();
            if (ts < firstSeen[idx]) firstSeen[idx] = ts;
        }

        // 计算实际出现的类型数，用于分配输出数组长度
        int distinct = 0;
        for (int c : counts) if (c > 0) distinct++;
        int outLen = Math.min(TOP_K, distinct);
        if (outLen == 0) return new ReactionDisplayTag[0];

        ReactionDisplayTag[] out = new ReactionDisplayTag[outLen];

        // 选择 Top-K：次数降序；并列取 firstSeen 更早者
        for (int i = 0; i < outLen; i++) {
            int best = -1;
            for (int t = 0; t < T; t++) {
                if (counts[t] == 0) continue;
                if (best == -1) { best = t; continue; }
                if (counts[t] > counts[best] ||
                        (counts[t] == counts[best] && firstSeen[t] < firstSeen[best])) {
                    best = t;
                }
            }
            // 写出并“清零”以便下一轮选择
            out[i] = new ReactionDisplayTag(types[best], String.valueOf(counts[best]));
            counts[best] = 0;
        }
        return out;
    }
}
