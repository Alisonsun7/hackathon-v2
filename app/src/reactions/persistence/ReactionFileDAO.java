package reactions.persistence;

import reactions.ReactionType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public final class ReactionFileDAO implements ReactionDAO {
    private static final byte OP_ADD = 0;
    private static final byte OP_REMOVE = 1;

    private final Path baseDir;
    private final IdTable users;
    private final IdTable messages;
    private final FileChannel log;
    // In-memory index: messageId -> per-message reactions
    private final java.util.concurrent.ConcurrentHashMap<java.util.UUID, reactions.MessageReactions> byMessage = new java.util.concurrent.ConcurrentHashMap<>();

    public ReactionFileDAO(Path baseDir) throws IOException {
        this.baseDir = baseDir;
        Files.createDirectories(baseDir);
        this.users = new IdTable(baseDir.resolve("users.idx"));
        this.messages = new IdTable(baseDir.resolve("messages.idx"));
        this.log = FileChannel.open(
                baseDir.resolve("reactions.log"),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
        );
    }

    @Override
    public void appendAdd(java.util.UUID userId, java.util.UUID messageId,
                          ReactionType type, long timestamp) throws IOException {
        int u = users.getOrAdd(userId);
        int m = messages.getOrAdd(messageId);
        writeRecord(OP_ADD, m, u, (byte) type.ordinal(), timestamp);
    }

    @Override
    public void appendRemove(java.util.UUID userId, java.util.UUID messageId,
                             ReactionType type) throws IOException {
        int u = users.getOrAdd(userId);
        int m = messages.getOrAdd(messageId);
        writeRecord(OP_REMOVE, m, u, (byte) type.ordinal(), 0L);
    }

    private void writeRecord(byte op, int msg, int user, byte type, long ts) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1 + 4 + 4 + 1 + 8);
        buf.put(op).putInt(msg).putInt(user).put(type).putLong(ts);
        buf.flip();
        while (buf.hasRemaining()) log.write(buf);
        // no per-record fsync for performance
    }

    @Override
    public void loadAll(Consumer consumer) throws IOException {
        Path logFile = baseDir.resolve("reactions.log");
        FileChannel ch = FileChannel.open(
                logFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.READ
        );
        ByteBuffer buf = ByteBuffer.allocate(1 + 4 + 4 + 1 + 8);
        while (true) {
            buf.clear();
            int n = ch.read(buf);
            if (n == -1) break;
            if (n < buf.capacity()) {
                // ignore trailing partial record
                break;
            }
            buf.flip();
            byte op = buf.get();
            int m = buf.getInt();
            int u = buf.getInt();
            byte t = buf.get();
            long ts = buf.getLong();

            java.util.UUID userId = users.getUuid(u);
            java.util.UUID msgId = messages.getUuid(m);
            ReactionType rt = ReactionType.values()[t & 0xFF];

            if (op == OP_ADD) {
                consumer.onAdd(userId, msgId, rt, ts);
            } else if (op == OP_REMOVE) {
                consumer.onRemove(userId, msgId, rt);
            }
        }
        ch.close();
    }

    @Override
    public void close() throws IOException {
        try {
            users.close();
        } finally {
            try {
                messages.close();
            } finally {
                log.close();
            }
        }
    }
    private reactions.MessageReactions bucket(java.util.UUID messageId) {
        return byMessage.computeIfAbsent(messageId, k -> new reactions.MessageReactions());
    }
    @Override
    public boolean add(java.util.UUID userId, java.util.UUID messageId,
                       reactions.ReactionType type, long timestamp) throws java.io.IOException {
        if (userId == null || messageId == null || type == null) return false;
        // accept timestamp >= 0; reject only negative if you want a guard
        if (timestamp < 0) return false;

        boolean changed = bucket(messageId).add(userId, type, timestamp);
        if (changed) {
            appendAdd(userId, messageId, type, timestamp);
        }
        return changed;
    }


    @Override
    public boolean remove(java.util.UUID userId, java.util.UUID messageId, reactions.ReactionType type) throws java.io.IOException {
        if (userId == null || messageId == null || type == null) return false;
        boolean changed = bucket(messageId).remove(userId, type);
        if (changed) {
            appendRemove(userId, messageId, type); // reuse low-level log
        }
        return changed;
    }

    @Override
    public java.util.List<reactions.ReactionType> getReactions(java.util.UUID userId, java.util.UUID messageId) {
        if (userId == null || messageId == null) return null;
        // If you have real existence check of user/message, use it here.
        if (!byMessage.containsKey(messageId)) return null;
        var out = bucket(messageId).getByUserChronological(userId);
        return out.isEmpty() ? java.util.Collections.emptyList() : out;
    }

    @Override
    public void resetAndLoadAll() throws java.io.IOException {
        byMessage.clear();
        // reuse existing loadAll to replay events
        this.loadAll(new ReactionDAO.Consumer() {
            @Override
            public void onAdd(java.util.UUID userId, java.util.UUID messageId, reactions.ReactionType type, long timestamp) {
                bucket(messageId).add(userId, type, timestamp);
            }
            @Override
            public void onRemove(java.util.UUID userId, java.util.UUID messageId, reactions.ReactionType type) {
                bucket(messageId).remove(userId, type);
            }
        });
    }

}
