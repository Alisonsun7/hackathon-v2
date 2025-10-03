import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import reactions.*;
import dao.*;
import dao.model.*;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ReactionDAOTests {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        RandomContentGenerator.populateRandomData();

        List<Object[]> scenarios = new ArrayList<>();

        // Scenario 1: Add a reaction successfully -> expect [HAPPY]
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1L))
            };
            List<ReactionType> expected = Arrays.asList(ReactionType.HAPPY);

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 2: Duplicate add -> expect only [SAD]
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 1L)),
                    () -> assertFalse(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 2L))
            };
            List<ReactionType> expected = Arrays.asList(ReactionType.SAD);

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 3: Remove reaction successfully -> expect []
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.ANGRY, 1L)),
                    () -> assertTrue(ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.ANGRY))
            };
            List<ReactionType> expected = Collections.emptyList();

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 4: Remove non-existing reaction -> expect [GOOD_LUCK]
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.GOOD_LUCK, 1L)),
                    () -> assertFalse(ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.HAPPY))
            };
            List<ReactionType> expected = Arrays.asList(ReactionType.GOOD_LUCK);

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 5: Multiple reactions, should be ordered by timestamp -> expect [SAD, HAPPY]
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 10L)),
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 5L))
            };
            List<ReactionType> expected = Arrays.asList(ReactionType.SAD, ReactionType.HAPPY);

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 6: Invalid parameters -> expect null
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {
                    () -> assertFalse(ReactionsFacade.addReaction(null, message.id(), ReactionType.HAPPY, 1L)),
                    () -> assertFalse(ReactionsFacade.removeReaction(userA.getUUID(), null, ReactionType.HAPPY))
            };
            List<ReactionType> expected = Collections.emptyList();

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 7: User adds multiple reactions, remove one -> expect [SAD]
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1L)),
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 2L)),
                    () -> assertTrue(ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.HAPPY))
            };
            List<ReactionType> expected = Arrays.asList(ReactionType.SAD);

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 8: Two different users on same message -> expect only u1’s reactions
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();
            User userB = getDifferentUser(userA);

            Runnable[] ops = {
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1L)),
                    () -> assertTrue(ReactionsFacade.addReaction(userB.getUUID(), message.id(), ReactionType.ANGRY, 2L))
            };
            List<ReactionType> expected = Arrays.asList(ReactionType.HAPPY);

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 9: User has no reactions -> expect []
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {};
            List<ReactionType> expected = Collections.emptyList();

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 10: Remove non-existing reaction, list unchanged
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1L)),
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.ANGRY, 2L)),
                    () -> assertFalse(ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.SAD))
            };
            List<ReactionType> expected = Arrays.asList(ReactionType.HAPPY, ReactionType.ANGRY);

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        // Scenario 11: Delete then re-add reactions, check order -> expect [SAD, HAPPY]
        {
            Message message = getRandomMessage();
            User userA = UserDAO.getInstance().getRandom();

            Runnable[] ops = {
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 1L)),
                    () -> assertTrue(ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.SAD)),
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 5L)),
                    () -> assertTrue(ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 10L))
            };
            List<ReactionType> expected = Arrays.asList(ReactionType.SAD, ReactionType.HAPPY);

            scenarios.add(new Object[]{message, userA, ops, expected});
        }

        return scenarios;
    }

    @Parameterized.Parameter(0)
    public Message message;

    @Parameterized.Parameter(1)
    public User user;

    @Parameterized.Parameter(2)
    public Runnable[] operations;

    @Parameterized.Parameter(3)
    public List<ReactionType> expected;

    @Test
    public void testDAOScenarios() {
        // Execute all operations
        for (Runnable op : operations) {
            op.run();
        }

        // Check final result
        List<ReactionType> actual = ReactionsFacade.getReactions(user.getUUID(), message.id());

        if (expected == null) {
            assertNull(actual);
        } else {
            assertEquals(expected, actual);
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
