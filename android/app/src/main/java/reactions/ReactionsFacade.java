package reactions;

import java.util.*;
import java.util.stream.Collectors;

public class ReactionsFacade {
    // use our custom ReactionDAO
    private static final ReactionDAO dao = ReactionDAO.getInstance();

    /**
     * Adds a reaction by a particular user of a particular type to a particular message.
     * Returns true if the reaction was successfully added, and false otherwise.
     * Users may have an arbitrary number of reactions on a single message, but only one of a given type.
     */
    public static boolean addReaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
        if (userUUID == null || messageUUID == null || type == null) return false;

        // check duplicate
        Reaction existing = dao.get(userUUID, messageUUID, type);
        if (existing != null) {
            return false; // already exists
        }

        Reaction reaction = new Reaction(userUUID, messageUUID, type, timestamp);
        return dao.add(reaction);
    }

    /**
     * Removes a reaction by a particular user of a particular type to a particular message.
     * Returns true if the reaction was successfully removed, and false otherwise.
     */
    public static boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        if (userUUID == null || messageUUID == null || type == null) return false;
        return dao.remove(userUUID, messageUUID, type);
    }

    /**
     * Fetches all reactions made by a particular user on a particular message.
     * Returns null if either userUUID or messageUUID do not correspond to actual User or Message.
     * They must be returned in chronological (time-based) order, from oldest to newest.
     */
    public static List<ReactionType> getReactions(UUID userUUID, UUID messageUUID) {
        if (userUUID == null || messageUUID == null) return null;
        List<Reaction> reactions = dao.getByUserAndMessage(userUUID, messageUUID);

        return reactions.stream()
                .sorted(Comparator.comparingLong(Reaction::getTimestamp))
                .map(Reaction::getType)
                .collect(Collectors.toList());
    }

    /**
     * Loads all persistent data (users, messages, posts, and importantly reactions) from persistent data.
     */
    public static void loadPersistentData() {
        dao.clear();
        for (Reaction r : ReactionPersistence.loadAll()) {
            dao.add(r);
        }
    }
}
