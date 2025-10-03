package reactions;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class MessageReactions {

    // class-level field; must be inside the class body, not in a method block
    private final Map<UUID, EnumMap<ReactionType, Long>> byUser = new HashMap<>();

    public boolean add(UUID userId, ReactionType type, long timestamp) {
        EnumMap<ReactionType, Long> m = byUser.computeIfAbsent(userId, k -> new EnumMap<>(ReactionType.class));
        if (m.containsKey(type)) return false; // one reaction per type per user
        m.put(type, timestamp);
        return true;
    }

    public boolean remove(UUID userId, ReactionType type) {
        EnumMap<ReactionType, Long> m = byUser.get(userId);
        if (m == null) return false;
        if (m.remove(type) == null) return false;
        if (m.isEmpty()) byUser.remove(userId);
        return true;
    }

    public List<ReactionType> getByUserChronological(UUID userId) {
        EnumMap<ReactionType, Long> m = byUser.get(userId);
        if (m == null || m.isEmpty()) return java.util.Collections.emptyList();
        return m.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()) // oldest -> newest by timestamp
                .map(Map.Entry::getKey)
                .toList(); // if your JDK <16, replace with Collectors.toList()
    }
}



