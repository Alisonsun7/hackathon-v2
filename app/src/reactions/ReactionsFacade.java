package reactions;
import reactions.persistence.ReactionDAO;
import reactions.persistence.ReactionFileDAO;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.UUID;
import dao.PostDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ReactionsFacade {
    // In-memory index: messageId -> per-message reactions
    private static final Map<UUID, MessageReactions> byMessage = new ConcurrentHashMap<>();

    // Persistence DAO (lazy init)
    private static ReactionDAO dao;

    private static MessageReactions bucket(UUID messageId) {
        return byMessage.computeIfAbsent(messageId, k -> new MessageReactions());
    }

    private static boolean isNullAny(Object... xs) {
        for (Object x : xs) if (x == null) return true;
        return false;
    }

    private static void ensureDAO() throws IOException {
        if (dao == null) {
            dao = new ReactionFileDAO(Path.of("data", "reactions"));
        }
    }



    /**
     * Adds a reaction by a particular user of a particular type to a particular message.
     * Returns true if the reaction was successfully added, and false otherwise.
     * Users may have an arbitrary number of reactions on a single message, but only one of a given type.
     */
    public static boolean addReaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
        try {
            ensureDAO();
            return dao.add(userUUID, messageUUID, type, timestamp);
        } catch (IOException e) {
            return false; // fail gracefully
        }
    }



    /**
     * Removes a reaction by a particular user of a particular type to a particular message.
     * Returns true if the reaction was successfully removed, and false otherwise.
     */
    public static boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        try {
            ensureDAO();
            return dao.remove(userUUID, messageUUID, type);
        } catch (IOException e) {
            return false; // fail gracefully
        }
    }



    /**
     * Fetches all reactions made by a particular user on a particular message.
     * Returns null if either userUUID or messageUUID do not correspond to actual User or Message.
     * They must be returned in chronological (time-based) order, from oldest to newest.
     */
    public static List<ReactionType> getReactions(UUID userUUID, UUID messageUUID) {
        try {
            ensureDAO();
            return dao.getReactions(userUUID, messageUUID);
        } catch (IOException e) {
            return null; // per spec: return null on illegal inputs / failures
        }
    }



    /**
     * Loads all persistent data (users, messages, posts, and importantly reactions) from persistent data.
     */
    public static void loadPersistentData() {
        try {
            ensureDAO();
            dao.resetAndLoadAll();
        } catch (IOException e) {
            // fail gracefully
        }
    }

    // ---------------- 给 DefaultReactionDataSource 提供的只读快照 ----------------

    static Map<UUID, Map<ReactionType, Long>> _snapshotForMessage(UUID messageUUID) {
        Map<UUID, Map<ReactionType, Long>> perMsg = STATE.get(messageUUID);
        if (perMsg == null || perMsg.isEmpty()) return Map.of();

        Map<UUID, Map<ReactionType, Long>> copy = new HashMap<>();
        for (var e : perMsg.entrySet()) {
            copy.put(e.getKey(), Map.copyOf(e.getValue()));
        }
        return Map.copyOf(copy);
    }

    // ---------------- 私有辅助 ----------------

    private static void recordEvent(ReactionEvent e) {
        EVENTS.computeIfAbsent(e.messageUUID, k -> new ArrayList<>()).add(e);
    }

    private static Message findMessageById(UUID messageUUID) {
        var it = PostDAO.getInstance().getAllMessages();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.id().equals(messageUUID)) return m;
        }
        return null;
    }

    static final class ReactionEvent {
        final UUID userUUID;
        final UUID messageUUID;
        final ReactionType type;
        final long timestampNanos;
        final boolean deleted;
        ReactionEvent(UUID userUUID, UUID messageUUID, ReactionType type, long ts, boolean deleted) {
            this.userUUID = userUUID;
            this.messageUUID = messageUUID;
            this.type = type;
            this.timestampNanos = ts;
            this.deleted = deleted;
        }
    }

    /** 当前有效状态： message -> ( user -> ( type -> timestamp ) ) */
    private static final Map<UUID, Map<UUID, Map<ReactionType, Long>>> STATE =
            new ConcurrentHashMap<>();

    /** 事件流（可选） */
    private static final Map<UUID, List<ReactionEvent>> EVENTS =
            new ConcurrentHashMap<>();

}
