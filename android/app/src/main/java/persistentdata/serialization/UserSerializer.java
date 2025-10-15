package persistentdata.serialization;

import dao.model.User;

import java.util.UUID;

/**
 * TODO: Document your schema here
 */
public class UserSerializer implements Serializer<User, String[]> {
	@Override
	public String[] serialize(User object) {
		// TODO: Complete this method according to the schema you have designed
		return new String[] {
				object.id() == null ? "" : object.id().toString(),
				object.role() == null ? "" : object.role().toString(),
				object.username() == null ? "" : object.username(),
				object.password() == null ? "" : object.password()
		};
	}

	@Override
	public User deserialize(String[] data) {
		// TODO: Complete this method according to the schema you have designed
		UUID uuid = data[0].isEmpty() ? null : UUID.fromString(data[0]);
		User.Role role = data[1].isEmpty() ? User.Role.Member : User.Role.valueOf(data[1]);
		String username = data[2].isEmpty() ? null : data[2];
		String password = data[3].isEmpty() ? null : data[3];

		return new User(uuid, role, username, password);
	}
}
