package dao;

import dao.model.HasUUID;
import dao.model.Message;
import dao.model.Post;
import dao.model.User;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PostDAO extends DAO<Post> {
	/**
	 * Generates a PostDAO by automatically building a Comparator that
	 * checks just that the UUID fields match. If you don't understand
	 * this syntax, don't worry. It's an advanced Java technique.
	 */
	private PostDAO() {
		super(Comparator.comparing(HasUUID::getUUID));
	}
	private static PostDAO instance;

	/**
	 * Gets a singleton instance of PostDAO, creating one if necessary.
	 * @return the instance
	 */
	public static PostDAO getInstance() {
		if (instance == null) instance = new PostDAO();
		return instance;
	}

	/**
	 * Gets the ith post, in order of timestamp
	 * @param i the index of the post to search for
	 * @return the post
	 */
	public Post getAtIndex(int i) {
		return data.getAtIndex(i);
	}

	/**
	 * Returns an Iterator that iterates through every message given as a reply to
	 * every post stored within the DAO, in no particular order.
	 * @return the iterator
	 */
	public Iterator<Message> getAllMessages() {
		// TODO: Complete this method using the Iterator design pattern
		return new Iterator<Message>() {
			private Iterator<Post> postIterator = PostDAO.this.getAll();
			private Iterator<Message> messageIterator = null;
			@Override
			public boolean hasNext() {
				while((messageIterator == null || !messageIterator.hasNext()) && postIterator.hasNext()) {
					Post next = postIterator.next();
					messageIterator = next.messages.getAll();
				}
				return messageIterator != null && messageIterator.hasNext();
			}

			@Override
			public Message next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return messageIterator.next();
			}
		};
	}

	/**
	 * Fetches a Post by just a UUID
	 * @param id the UUID to search for
	 * @return the post if they exist, else null
	 */
	public Post getByUUID(UUID id) {
		for (Iterator<Post> it = data.getAll(); it.hasNext(); ) {
			Post post = it.next();
			if (post.getUUID().equals(id)) return post;
		}
		return null;
	}
}
