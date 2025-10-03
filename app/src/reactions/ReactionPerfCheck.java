package reactions;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.Comparator;

public class ReactionPerfCheck {

    private static void deleteDir(Path p) throws IOException {
        if (!Files.exists(p)) return;
        try (var s = Files.walk(p)) {
            s.sorted(Comparator.reverseOrder()).forEach(pp -> {
                try { Files.deleteIfExists(pp); } catch (IOException ignored) {}
            });
        }
    }

    public static void main(String[] args) throws Exception {
        // 0) clean data dir to measure from scratch
        Path dataDir = Path.of("data", "reactions");
        deleteDir(dataDir);
        System.out.println("Cleaned data dir: " + dataDir.toAbsolutePath());

        // 1) parameters
        int N = 100_000;
        UUID msg  = UUID.fromString("22222222-2222-2222-2222-222222222222");
        ReactionType type = ReactionType.values()[0];

        // 2) write N valid add events (distinct users)
        long t1 = System.nanoTime();
        long base = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            UUID user = new UUID(0L, i + 1L); // deterministic unique user
            boolean ok = ReactionsFacade.addReaction(user, msg, type, base + i);
            if (!ok) {
                throw new RuntimeException("Unexpected add failure at i=" + i);
            }
        }
        long t2 = System.nanoTime();
        System.out.println("Wrote " + N + " add events.");

        // 3) reload from persistence
        ReactionsFacade.loadPersistentData();
        long t3 = System.nanoTime();
        System.out.println("Reloaded from persistence.");

        // 4) stats
        long writeMs = (t2 - t1) / 1_000_000;
        long loadMs  = (t3 - t2) / 1_000_000;
        System.out.println("Write time: " + writeMs + " ms");
        System.out.println("Load time : " + loadMs  + " ms");
        System.out.println("Total time: " + (writeMs + loadMs) + " ms");

        long logSize   = Files.exists(dataDir.resolve("reactions.log")) ? Files.size(dataDir.resolve("reactions.log")) : 0;
        long usersSize = Files.exists(dataDir.resolve("users.idx")) ? Files.size(dataDir.resolve("users.idx")) : 0;
        long msgsSize  = Files.exists(dataDir.resolve("messages.idx")) ? Files.size(dataDir.resolve("messages.idx")) : 0;
        long totalSize = logSize + usersSize + msgsSize;

        System.out.println("File sizes: log=" + logSize + "B, users.idx=" + usersSize + "B, messages.idx=" + msgsSize + "B, total=" + totalSize + "B");
        System.out.println("Avg bytes per event: " + (totalSize / (double) N));

        // 5) spot-check one user after reload
        UUID checkUser = new UUID(0L, N / 2 + 1L);
        var list = ReactionsFacade.getReactions(checkUser, msg);
        System.out.println("Reactions for user " + checkUser + " after reload: " + list);
    }
}
