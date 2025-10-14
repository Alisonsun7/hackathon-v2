package dao;

import dao.model.Message;
import dao.model.Post;
import dao.model.User;

import reactions.ReactionType;
import reactions.ReactionsFacade;

import java.util.Random;
import java.util.UUID;
import java.util.Iterator;

public class RandomContentGenerator {
	private static final String[] NAMES = new String[] {
			"Alexandria", "Beatrice", "Carmen", "Diego", "Ema", "Farah", "Georg", "Hadrian"
	};
	private static final String[] TOPICS = new String[] {
			"UML", "design patterns", "data structures", "persistent data",
			"modelling", "software construction", "exam", "mini project", "group project",
			"singleton", "observer", "factory", "strategy", "state", "facade", "DAO",
			"IntelliJ", "Android Studio", "AVL tree", "tree balancing", "concurrency"
	};
	private static final String[] POST_NAMES = new String[] {
			"Question about %s", "I love %s", "Study session: %s",
			"Practicing %s", "I don't understand %s", "Applications of %s", "How to implement %s?"
	};

	private static final Random random = new Random();

	/**
	 * Fills the DAOs with a reasonable amount of data, for testing purposes
	 */
	public static void populateRandomData() {
		System.out.println("Generating users...");
		for (int i = 0; i < 200; i++) {
			RandomContentGenerator.generateUser();
		}

		System.out.println("Generating posts...");
		for (int i = 0; i < 50; i++) {
			RandomContentGenerator.generatePost();
		}

		System.out.println("Generating comments...");
		for (int i = 0; i < 2500; i++) {
			RandomContentGenerator.generateComment();
		}

		System.out.println("Generating reactions...");
		for (int i = 0; i < 5000; i++) {
			RandomContentGenerator.generateReaction();
		}

		System.out.println("populateRandomData() finished!");
	}

	/**
	 * Uniformly randomly selects an item from a generic array
	 * @param array the array of objects to choose from
	 * @return a random element, or a RuntimeException if array is empty or null
	 * @param <T> the type of array
	 */
	private static <T> T chooseRandomFromArray(T[] array) {
		if (array == null || array.length == 0)
			throw new RuntimeException("Targeted array is empty");
		return array[random.nextInt(array.length)];
	}

	public static void generateUser() {
		String username = chooseRandomFromArray(NAMES) + random.nextInt(1000);
		User.Role role = random.nextInt(4) == 0 ? User.Role.Admin : User.Role.Member;
		User user = new User(UUID.randomUUID(), role, username, generatePassword());
		UserDAO.getInstance().add(user);
	}

	private static String generatePassword() {
		return "password" + random.nextInt(10000);
	}

	public static void generatePost() {
		User user = UserDAO.getInstance().getRandom();
		if (user == null) return;
		String postName = chooseRandomFromArray(POST_NAMES).formatted(chooseRandomFromArray(TOPICS));
		Post post = new Post(UUID.randomUUID(), user.getUUID(), postName);
		PostDAO.getInstance().add(post);
	}

	public static void generateComment() {
		generateComment(PostDAO.getInstance().getRandom());
	}

	public static void generateComment(Post post) {
		User user = UserDAO.getInstance().getRandom();
		if (post == null || user == null) return;

		String content = "Hello from %s".formatted(user.username());
		long timestamp =  System.currentTimeMillis() - random.nextInt(2000000);

		Message message = new Message(UUID.randomUUID(), user.getUUID(), post.getUUID(), timestamp, content);
		post.messages.insert(message);
	}

	/** 新增：随机为随机消息添加随机用户的随机 reaction */
	public static void generateReaction() {
		Message message = getRandomMessageGlobally();
		if (message == null) return;

		User user = UserDAO.getInstance().getRandom();
		if (user == null) return;

		ReactionType type = chooseRandomFromArray(ReactionType.values());
		long timestamp = System.currentTimeMillis() - random.nextInt(2000000);

		// 注意：Message 没有 getUUID()，用 id() 取 UUID
//		System.out.println("User: " + user.getUUID() + ", Msg: " + message.id() + ", time: " + timestamp);
		boolean changed = ReactionsFacade.addReaction(user.getUUID(), message.id(), type, timestamp);
//		System.out.println("Changed: " + changed);
	}

	private static Message getRandomMessageGlobally() {
		Iterator<Message> it = PostDAO.getInstance().getAllMessages();
		Post post = PostDAO.getInstance().getRandom();
		return post.messages.getRandom();
	}

	/* ========= 可直接在命令行运行的 main（临时调试入口） =========
       终端命令（在包含 dao/ 的目录下，例如 app/src）：
         javac -d . dao/RandomContentGenerator.java
         java dao.RandomContentGenerator
       如果你用 Java 23 并已启用 preview：
         javac --enable-preview --release 23 -d . dao/RandomContentGenerator.java
         java  --enable-preview dao.RandomContentGenerator
    */
	public static void main(String[] args) {
		System.out.println(" Testing RandomContentGenerator...");
		populateRandomData();
		System.out.println("Data generation complete!");
	}
}