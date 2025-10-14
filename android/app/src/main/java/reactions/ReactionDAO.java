package reactions;

import java.util.*;

/**
 * DAO for storing and manipulating Reaction objects.
 * Supports add, remove, get, getAll, and getRandom.
 */
public class ReactionDAO {

    // messageId -> userId -> type -> Reaction
    private final Map<UUID, Map<UUID, Map<ReactionType, Reaction>>> storage = new HashMap<>();
    private final Random random = new Random();

    private static ReactionDAO instance;

    private ReactionDAO() {}

    public static ReactionDAO getInstance() {
        if (instance == null) instance = new ReactionDAO();
        return instance;
    }

    /**
     * Adds a reaction if it does not already exist.
     * @return true if added, false if already exists.
     */
    public boolean add(Reaction reaction) {
        if (reaction == null) return false;

        storage.putIfAbsent(reaction.getMessageId(), new HashMap<>());
        Map<UUID, Map<ReactionType, Reaction>> users = storage.get(reaction.getMessageId());

        users.putIfAbsent(reaction.getUserId(), new HashMap<>());
        Map<ReactionType, Reaction> types = users.get(reaction.getUserId());

        if (types.containsKey(reaction.getType())) {
            return false; // already exists
        }
        types.put(reaction.getType(), reaction);
        return true;
    }

    /**
     * Removes a specific reaction.
     * @return true if removed, false otherwise.
     */
    public boolean remove(UUID userId, UUID messageId, ReactionType type) {
        if (userId == null || messageId == null || type == null) return false;

        Map<UUID, Map<ReactionType, Reaction>> users = storage.get(messageId);
        if (users == null) return false;

        Map<ReactionType, Reaction> types = users.get(userId);
        if (types == null || !types.containsKey(type)) return false;

        types.remove(type);
        if (types.isEmpty()) users.remove(userId);
        if (users.isEmpty()) storage.remove(messageId);

        return true;
    }

    /**
     * Fetches a single reaction by (user, message, type).
     * @return the Reaction if found, null otherwise.
     */
    public Reaction get(UUID userId, UUID messageId, ReactionType type) {
        if (userId == null || messageId == null || type == null) return null;
        Map<UUID, Map<ReactionType, Reaction>> users = storage.get(messageId);
        if (users == null) return null;
        Map<ReactionType, Reaction> types = users.get(userId);
        if (types == null) return null;
        return types.get(type);
    }

    /**
     * Gets all reactions of a user on a message.
     * Sorted by timestamp ascending.
     */
    public List<Reaction> getByUserAndMessage(UUID userId, UUID messageId) {
        if (userId == null || messageId == null) return Collections.emptyList();
        Map<UUID, Map<ReactionType, Reaction>> users = storage.get(messageId);
        if (users == null) return Collections.emptyList();

        Map<ReactionType, Reaction> types = users.get(userId);
        if (types == null) return Collections.emptyList();

        List<Reaction> result = new ArrayList<>(types.values());
        result.sort(Comparator.comparingLong(Reaction::getTimestamp));
        return result;
    }

    /**
     * Iterator over all reactions in storage.
     */
    public Iterator<Reaction> getAll() {
        List<Reaction> all = new ArrayList<>();
        for (Map<UUID, Map<ReactionType, Reaction>> users : storage.values()) {
            for (Map<ReactionType, Reaction> types : users.values()) {
                all.addAll(types.values());
            }
        }
        return all.iterator();
    }

    /**
     * Get a random reaction, or null if empty.
     */
    public Reaction getRandom() {
        List<Reaction> all = new ArrayList<>();
        for (Map<UUID, Map<ReactionType, Reaction>> users : storage.values()) {
            for (Map<ReactionType, Reaction> types : users.values()) {
                all.addAll(types.values());
            }
        }
        if (all.isEmpty()) return null;
        return all.get(random.nextInt(all.size()));
    }

    /**
     * Clears all stored reactions.
     */
    public void clear() {
        storage.clear();
    }
}
