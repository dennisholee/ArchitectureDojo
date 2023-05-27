package io.forest.archunit.domains.model;

import java.util.Objects;

public class UserStatus {

	public static UserStatus ACTIVE = new UserStatus("ACTIVE");
	
	public static UserStatus INACTIVE = new UserStatus("INACTIVE");
	
	private String status;

	private UserStatus(String status) {
		this.status = status;
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(status);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserStatus other = (UserStatus) obj;
		return Objects.equals(status, other.status);
	}

}
