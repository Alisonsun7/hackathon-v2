package reactions;


import reactions.persistence.ReactionDAO;

import java.util.List;
import java.util.UUID;

public class ReactionsFacade {

    public static boolean addReaction(UUID userUUID, UUID messageUUID,
                                      ReactionType type, long timestamp) {
        if (userUUID == null || messageUUID == null || type == null) return false;
        return ReactionDAO.getInstance().addIfAbsent(userUUID, messageUUID, type, timestamp);
    }

    public static boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
        if (userUUID == null || messageUUID == null || type == null) return false;
        return ReactionDAO.getInstance().removeIfPresent(userUUID, messageUUID, type);
    }

    public static List<ReactionType> getReactions(UUID userUUID, UUID messageUUID) {
        if (userUUID == null || messageUUID == null) return null; // 按题目要求返回 null
        return ReactionDAO.getInstance().getTypesOrdered(userUUID, messageUUID);
    }

    public static void loadPersistentData() {
        // TODO
    }
}
