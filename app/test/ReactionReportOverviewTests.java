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

        // Scenario 1: Basic frequency ordering
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();
            User userB = getDifferentUser(userA);

            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
            ReactionsFacade.addReaction(userB.getUUID(), message.id(), ReactionType.ANGRY, 2);
            ReactionsFacade.addReaction(getDifferentUser(userA, userB).getUUID(), message.id(), ReactionType.HAPPY, 3);

            ReactionDisplayTag[] expected = {
                    new ReactionDisplayTag(ReactionType.HAPPY, "2"),
                    new ReactionDisplayTag(ReactionType.ANGRY, "1")
            };
            scenarios.add(new Object[]{message, expected});
        }

        // Scenario 2: Tie-breaker by earliest appearance
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();
            User userB = getDifferentUser(userA);

            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
            ReactionsFacade.addReaction(userB.getUUID(), message.id(), ReactionType.ANGRY, 2);
            ReactionsFacade.addReaction(getDifferentUser(userA, userB).getUUID(), message.id(), ReactionType.ANGRY, 3);
            ReactionsFacade.addReaction(getDifferentUser(userA, userB).getUUID(), message.id(), ReactionType.HAPPY, 4);

            ReactionDisplayTag[] expected = {
                    new ReactionDisplayTag(ReactionType.HAPPY, "2"),
                    new ReactionDisplayTag(ReactionType.ANGRY, "2")
            };
            scenarios.add(new Object[]{message, expected});
        }

        // Scenario 3: Deletion removes type completely
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 1);
            ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.SAD);

            ReactionDisplayTag[] expected = {};
            scenarios.add(new Object[]{message, expected});
        }

        // Scenario 4: More than 5 types, should truncate
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.ANGRY, 2);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD, 3);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.LAUGH, 4);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.GOOD_LUCK, 5);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.CONGRATULATIONS, 6);

            ReactionDisplayTag[] expected = {
                    new ReactionDisplayTag(ReactionType.HAPPY, "1"),
                    new ReactionDisplayTag(ReactionType.ANGRY, "1"),
                    new ReactionDisplayTag(ReactionType.SAD, "1"),
                    new ReactionDisplayTag(ReactionType.LAUGH, "1"),
                    new ReactionDisplayTag(ReactionType.GOOD_LUCK, "1")
            };
            scenarios.add(new Object[]{message, expected});
        }

        // Scenario 5: Empty message, expect []
        {
            Message message = getRandomMessage();
            ReactionDisplayTag[] expected = {};
            scenarios.add(new Object[]{message, expected});
        }

        // Scenario 6: Mixed counts and tie-breaking
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();
            User userB = getDifferentUser(userA);
            User userC = getDifferentUser(userA, userB);

            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
            ReactionsFacade.addReaction(userB.getUUID(), message.id(), ReactionType.ANGRY, 2);
            ReactionsFacade.addReaction(userC.getUUID(), message.id(), ReactionType.ANGRY, 3);
            ReactionsFacade.addReaction(getDifferentUser(userA, userB, userC).getUUID(), message.id(), ReactionType.HAPPY, 4);
            ReactionsFacade.addReaction(getDifferentUser(userA, userB, userC).getUUID(), message.id(), ReactionType.LAUGH, 5);
            ReactionsFacade.addReaction(getDifferentUser(userA, userB, userC).getUUID(), message.id(), ReactionType.LAUGH, 6);

            ReactionDisplayTag[] expected = {
                    new ReactionDisplayTag(ReactionType.HAPPY, "2"),
                    new ReactionDisplayTag(ReactionType.ANGRY, "2"),
                    new ReactionDisplayTag(ReactionType.LAUGH, "2")
            };
            scenarios.add(new Object[]{message, expected});
        }

        // Scenario 7: Deleting one type completely
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 1);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD, 2);

            ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.SAD);
            ReactionsFacade.removeReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD);

            ReactionDisplayTag[] expected = {};
            scenarios.add(new Object[]{message, expected});
        }

        // Scenario 8: Exactly five different reaction types
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.ANGRY, 2);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD, 3);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.LAUGH, 4);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.GOOD_LUCK, 5);

            ReactionDisplayTag[] expected = {
                    new ReactionDisplayTag(ReactionType.HAPPY, "1"),
                    new ReactionDisplayTag(ReactionType.ANGRY, "1"),
                    new ReactionDisplayTag(ReactionType.SAD, "1"),
                    new ReactionDisplayTag(ReactionType.LAUGH, "1"),
                    new ReactionDisplayTag(ReactionType.GOOD_LUCK, "1")
            };
            scenarios.add(new Object[]{message, expected});
        }

        // Scenario 9: More than five types, check truncation order
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.ANGRY, 2);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD, 3);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.LAUGH, 4);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.GOOD_LUCK, 5);
            ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.CONGRATULATIONS, 6);

            ReactionDisplayTag[] expected = {
                    new ReactionDisplayTag(ReactionType.HAPPY, "1"),
                    new ReactionDisplayTag(ReactionType.ANGRY, "1"),
                    new ReactionDisplayTag(ReactionType.SAD, "1"),
                    new ReactionDisplayTag(ReactionType.LAUGH, "1"),
                    new ReactionDisplayTag(ReactionType.GOOD_LUCK, "1")
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
