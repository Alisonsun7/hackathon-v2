package reactions;

import dao.model.Message;
import java.util.List;

interface ReactionDataSource {

    List<ReactionRecord> listEffectiveReactions(Message message);


    final class ReactionRecord {
        public final ReactionType type;
        public final String username;
        public final long timestampNanos;
        public ReactionRecord(ReactionType type, String username, long timestampNanos) {
            this.type = type;
            this.username = username;
            this.timestampNanos = timestampNanos;
        }
    }
}
