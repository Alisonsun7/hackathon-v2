package reactions;
import reactions.persistence.ReactionDAO;
import reactions.persistence.ReactionFileDAO;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.UUID;

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


}
