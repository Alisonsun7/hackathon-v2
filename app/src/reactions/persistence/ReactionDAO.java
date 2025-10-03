package reactions.persistence;

import reactions.ReactionType;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

public interface ReactionDAO extends Closeable {
    void appendAdd(UUID userId, UUID messageId, ReactionType type, long timestamp) throws IOException;

    void appendRemove(UUID userId, UUID messageId, ReactionType type) throws IOException;

    interface Consumer {
        void onAdd(UUID userId, UUID messageId, ReactionType type, long timestamp);
        void onRemove(UUID userId, UUID messageId, ReactionType type);
    }

    void loadAll(Consumer consumer) throws IOException;
    // High-level storage API for Task 1
    boolean add(java.util.UUID userId, java.util.UUID messageId, reactions.ReactionType type, long timestamp) throws java.io.IOException;

    boolean remove(java.util.UUID userId, java.util.UUID messageId, reactions.ReactionType type) throws java.io.IOException;

    // Returns null if ids are considered non-existent; otherwise chronological list
    java.util.List<reactions.ReactionType> getReactions(java.util.UUID userId, java.util.UUID messageId);

    // Clear in-memory state and rebuild from persistent log
    void resetAndLoadAll() throws java.io.IOException;

    @Override
    void close() throws IOException;
}
