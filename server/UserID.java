package newbank.server;

import java.util.Objects;

public class UserID {
	private String key;
	
	public UserID(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserID userID = (UserID) o;
		return key.equals(userID.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}
}
