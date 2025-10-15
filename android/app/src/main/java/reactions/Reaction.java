package reactions;

import dao.model.HasUUID;
import java.util.Objects;
import java.util.UUID;

public class Reaction implements HasUUID {
    private final UUID userId;
    private final UUID messageId;
    private final ReactionType type;
    private final long timestamp;
    private final UUID uuid; // generated composite key

    public Reaction(UUID userId, UUID messageId, ReactionType type, long timestamp) {
        this.userId = userId;
        this.messageId = messageId;
        this.type = type;
        this.timestamp = timestamp;
        // generate a deterministic UUID from composite fields
        this.uuid = UUID.nameUUIDFromBytes((userId.toString() + messageId.toString() + type.name()).getBytes());
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public ReactionType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reaction)) return false;
        Reaction r = (Reaction) o;
        return userId.equals(r.userId) &&
                messageId.equals(r.messageId) &&
                type == r.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, messageId, type);
    }
}
