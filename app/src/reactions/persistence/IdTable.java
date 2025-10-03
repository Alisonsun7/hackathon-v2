package reactions.persistence;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Persistent bidirectional mapping between UUID and a compact int id.
 * File format: a flat sequence of 16-byte UUIDs in insertion order.
 */
final class IdTable implements Closeable {
    private final Path file;
    private final Map<UUID, Integer> toInt = new HashMap<>();
    private final List<UUID> toUuid = new ArrayList<>();
    private final FileChannel channel;

    IdTable(Path file) throws IOException {
        this.file = file;
        Files.createDirectories(file.getParent());
        this.channel = FileChannel.open(
                file,
                StandardOpenOption.CREATE,
                StandardOpenOption.READ,
                StandardOpenOption.WRITE
        );
        loadAll();
    }

    private void loadAll() throws IOException {
        channel.position(0);
        ByteBuffer buf = ByteBuffer.allocate(16);
        int id = 0;
        while (true) {
            buf.clear();
            int n = channel.read(buf);
            if (n == -1) break;
            if (n < 16) break; // ignore trailing partial record
            buf.flip();
            long msb = buf.getLong();
            long lsb = buf.getLong();
            UUID u = new UUID(msb, lsb);
            toUuid.add(u);
            toInt.put(u, id++);
        }
    }

    int getOrAdd(UUID u) throws IOException {
        Integer id = toInt.get(u);
        if (id != null) return id;
        int newId = toUuid.size();
        toUuid.add(u);
        toInt.put(u, newId);

        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.putLong(u.getMostSignificantBits());
        buf.putLong(u.getLeastSignificantBits());
        buf.flip();

        channel.position(channel.size());
        while (buf.hasRemaining()) channel.write(buf);
        return newId;
    }

    UUID getUuid(int id) {
        return toUuid.get(id);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
