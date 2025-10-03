package reactions;

import dao.PostDAO;
import dao.UserDAO;
import dao.model.Message;
import dao.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReactionsFacade {


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


	private static final Map<UUID, Map<UUID, Map<ReactionType, Long>>> STATE =
			new ConcurrentHashMap<>();


	private static final Map<UUID, List<ReactionEvent>> EVENTS =
			new ConcurrentHashMap<>();



	public static boolean addReaction(UUID userUUID, UUID messageUUID, ReactionType type, long timestamp) {
		if (userUUID == null || messageUUID == null || type == null) return false;
		// 校验用户与消息存在
		User u = UserDAO.getInstance().getByUUID(userUUID);
		Message m = findMessageById(messageUUID);
		if (u == null || m == null) return false;

		Map<UUID, Map<ReactionType, Long>> perMsg =
				STATE.computeIfAbsent(messageUUID, k -> new ConcurrentHashMap<>());
		Map<ReactionType, Long> perUser =
				perMsg.computeIfAbsent(userUUID, k -> new ConcurrentHashMap<>());

		// 同一消息-用户-类型：只能存在一个
		if (perUser.containsKey(type)) return false;

		perUser.put(type, timestamp);
		recordEvent(new ReactionEvent(userUUID, messageUUID, type, timestamp, false));
		return true;
	}

	public static boolean removeReaction(UUID userUUID, UUID messageUUID, ReactionType type) {
		if (userUUID == null || messageUUID == null || type == null) return false;

		Map<UUID, Map<ReactionType, Long>> perMsg = STATE.get(messageUUID);
		if (perMsg == null) return false;
		Map<ReactionType, Long> perUser = perMsg.get(userUUID);
		if (perUser == null || !perUser.containsKey(type)) return false;

		long ts = perUser.remove(type);
		recordEvent(new ReactionEvent(userUUID, messageUUID, type, ts, true));
		// 清理空表
		if (perUser.isEmpty()) perMsg.remove(userUUID);
		if (perMsg.isEmpty()) STATE.remove(messageUUID);
		return true;
	}

	public static List<ReactionType> getReactions(UUID userUUID, UUID messageUUID) {
		if (userUUID == null || messageUUID == null) return null;
		if (UserDAO.getInstance().getByUUID(userUUID) == null) return null;
		if (findMessageById(messageUUID) == null) return null;

		Map<UUID, Map<ReactionType, Long>> perMsg = STATE.get(messageUUID);
		if (perMsg == null) return List.of();
		Map<ReactionType, Long> perUser = perMsg.get(userUUID);
		if (perUser == null || perUser.isEmpty()) return List.of();

		// 按时间升序返回该用户的类型
		return perUser.entrySet().stream()
				.sorted(Comparator.comparingLong(Map.Entry::getValue))
				.map(Map.Entry::getKey)
				.toList();
	}

	public static void loadPersistentData() {
		// TODO (task 3)：需要时在这里把持久化数据重放到 STATE/EVENTS
	}



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
}
