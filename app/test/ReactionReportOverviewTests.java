import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import reactions.*;
import dao.*;
import dao.model.*;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ReactionReportOverviewTests {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        RandomContentGenerator.populateRandomData();

        List<Object[]> scenarios = new ArrayList<>();

        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();
            User userB = getDifferentUser(userA);

            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.LAUGH, 2);
            ReactionsFacade.addReaction(userB.getUUID(), message.id(), ReactionType.ANGRY, 3);

            ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.LAUGH);

            ReactionDisplayTag[] expected = {
                    new ReactionDisplayTag(ReactionType.HAPPY, "1"),
                    new ReactionDisplayTag(ReactionType.ANGRY, "1")
            };
            scenarios.add(new Object[]{message, expected});
        }

        {
            Message message = getRandomMessage();
            User u1 = UserDAO.getInstance().getRandom();
            User u2 = getDifferentUser(u1);
            User u3 = getDifferentUser(u1, u2);

            ReactionsFacade.addReaction(u1.getUUID(), message.id(), ReactionType.HAPPY, 1);
            ReactionsFacade.addReaction(u2.getUUID(), message.id(), ReactionType.HAPPY, 2);
            ReactionsFacade.addReaction(u3.getUUID(), message.id(), ReactionType.HAPPY, 3);

            ReactionsFacade.addReaction(u1.getUUID(), message.id(), ReactionType.ANGRY, 4);
            ReactionsFacade.addReaction(u2.getUUID(), message.id(), ReactionType.ANGRY, 5);

            ReactionsFacade.addReaction(u1.getUUID(), message.id(), ReactionType.LAUGH, 6);
            ReactionsFacade.addReaction(u2.getUUID(), message.id(), ReactionType.LAUGH, 7);

            ReactionDisplayTag[] expected = {
                    new ReactionDisplayTag(ReactionType.HAPPY, "3"),
                    new ReactionDisplayTag(ReactionType.ANGRY, "2"),
                    new ReactionDisplayTag(ReactionType.LAUGH, "2")
            };
            scenarios.add(new Object[]{message, expected});
        }

        return scenarios;
    }

    @Parameterized.Parameter(0)
    public Message message;

    @Parameterized.Parameter(1)
    public ReactionDisplayTag[] expected;

    @Test
    public void testOverviewReport() {
        ReportSources sources = new ReportSources();
        for (IReactionReporter reporter : sources.getReporters()) {
            ReactionDisplayTag[] actual = reporter.generateReport(message);
            assertArrayEquals("Failed on " + reporter.getClass().getSimpleName(), expected, actual);
        }
    }

    private static Message getRandomMessage() {
        Message msg = null;
        while (msg == null) {
            Post post = PostDAO.getInstance().getRandom();
            msg = post.messages.getRandom();
        }
        return msg;
    }

    private static User getDifferentUser(User... exclude) {
        User u = null;
        while (u == null || Arrays.asList(exclude).contains(u)) {
            u = UserDAO.getInstance().getRandom();
        }
        return u;
    }
}
