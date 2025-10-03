// reactions/ReactionDataSource.java
package reactions;
import dao.model.Message;
import java.util.List;
import java.util.UUID;

interface ReactionDataSource {
    record ReactionRecord(UUID userId, UUID messageId, ReactionType type, long timestampNanos) {}
    List<ReactionRecord> listEffectiveReactions(Message message);
}
