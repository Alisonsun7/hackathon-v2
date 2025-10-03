package dao;

import reactions.ReactionType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ReactionsDAO {


    private static final ReactionsDAO INSTANCE = new ReactionsDAO();
    public static ReactionsDAO getInstance() { return INSTANCE; }
    private ReactionsDAO() {}

    private final ConcurrentMap<UUID, ConcurrentMap<UUID, ConcurrentMap<ReactionType, Long>>> store =
            new ConcurrentHashMap<>();


    public boolean addIfAbsent(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
        if (userUUID == null || messageUUID == null || type == null) return false;

        ConcurrentMap<UUID, ConcurrentMap<ReactionType, Long>> perMsg =
                store.computeIfAbsent(messageUUID, k -> new ConcurrentHashMap<>());

        ConcurrentMap<ReactionType, Long> perUser =
                perMsg.computeIfAbsent(userUUID, k -> new ConcurrentHashMap<>());

        return perUser.putIfAbsent(type, timestamp) == null;
    }


    public boolean removeIfPresent(UUID userUUID, UUID messageUUID, ReactionType type) {
        if (userUUID == null || messageUUID == null || type == null) return false;

        ConcurrentMap<UUID, ConcurrentMap<ReactionType, Long>> perMsg = store.get(messageUUID);
        if (perMsg == null) return false;

        ConcurrentMap<ReactionType, Long> perUser = perMsg.get(userUUID);
        if (perUser == null) return false;

        Long removed = perUser.remove(type);
        if (removed == null) return false;

        // 及时清理空 map，避免内存残留
        if (perUser.isEmpty()) perMsg.remove(userUUID, perUser);
        if (perMsg.isEmpty()) store.remove(messageUUID, perMsg);
        return true;
    }


    public List<ReactionType> getTypesOrdered(UUID userUUID, UUID messageUUID) {
        if (userUUID == null || messageUUID == null) return Collections.emptyList();

        Map<UUID, ConcurrentMap<ReactionType, Long>> perMsg = store.get(messageUUID);
        if (perMsg == null) return Collections.emptyList();

        Map<ReactionType, Long> perUser = perMsg.get(userUUID);
        if (perUser == null || perUser.isEmpty()) return Collections.emptyList();

        return perUser.entrySet().stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue)) // 时间戳升序
                .map(Map.Entry::getKey)
                .toList();
    }

    public void clearAll() {
        store.clear();
    }
}
