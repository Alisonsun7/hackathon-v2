package reactions;

import dao.UserDAO;
import dao.model.Message;
import dao.model.User;

import java.util.*;

final class DefaultReactionDataSource implements ReactionDataSource {

    @Override
    public List<ReactionRecord> listEffectiveReactions(Message message) {
        if (message == null) return List.of();

        // 从 facade 获取该消息的快照；若你实现了 Task 1，这里即可拿到有效反应
        Map<UUID, Map<ReactionType, Long>> snap =
                ReactionsFacade._snapshotForMessage(message.id());
        if (snap == null || snap.isEmpty()) return List.of();

        List<ReactionRecord> out = new ArrayList<>(snap.size() * 2);
        for (var e : snap.entrySet()) {
            UUID userId = e.getKey();
            User user = UserDAO.getInstance().getByUUID(userId);
            String username = (user == null) ? userId.toString() : user.username();
            for (var kv : e.getValue().entrySet()) {
                out.add(new ReactionRecord(kv.getKey(), username, kv.getValue()));
            }
        }
        return out;
    }
}
