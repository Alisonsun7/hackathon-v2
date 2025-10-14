package reactions;

import java.io.*;
import java.util.*;

public class ReactionPersistence {
    private static final String FILE = "reactions.dat";

    /** Save one reaction (append mode) */
    public static synchronized void appendReaction(Reaction r) {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(FILE, true)))) {
            writeReaction(dos, r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Rewrite file with all current reactions (e.g., after remove) */
    public static synchronized void saveAll(Collection<Reaction> all) {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(FILE, false)))) {
            for (Reaction r : all) {
                writeReaction(dos, r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Load reactions from file */
    public static synchronized List<Reaction> loadAll() {
        List<Reaction> result = new ArrayList<>();
        File f = new File(FILE);
        if (!f.exists()) return result;

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(
                new FileInputStream(f)))) {
            while (dis.available() > 0) {
                UUID userId = new UUID(dis.readLong(), dis.readLong());
                UUID msgId = new UUID(dis.readLong(), dis.readLong());
                byte typeOrd = dis.readByte();
                long ts = dis.readLong();

                Reaction r = new Reaction(userId, msgId,
                        ReactionType.values()[typeOrd], ts);
                result.add(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void writeReaction(DataOutputStream dos, Reaction r) throws IOException {
        dos.writeLong(r.getUserId().getMostSignificantBits());
        dos.writeLong(r.getUserId().getLeastSignificantBits());
        dos.writeLong(r.getMessageId().getMostSignificantBits());
        dos.writeLong(r.getMessageId().getLeastSignificantBits());
        dos.writeByte(r.getType().ordinal());
        dos.writeLong(r.getTimestamp());
    }
}
