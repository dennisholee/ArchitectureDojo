package io.forest.archunit.domains.model;

import java.util.Objects;

public class CustomerStatus {

	public static CustomerStatus ACTIVE = new CustomerStatus("ACTIVE");
	
	public static CustomerStatus INACTIVE = new CustomerStatus("INACTIVE");
	
	private String status;

	private CustomerStatus(String status) {
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
		CustomerStatus other = (CustomerStatus) obj;
		return Objects.equals(status, other.status);
	}

}
