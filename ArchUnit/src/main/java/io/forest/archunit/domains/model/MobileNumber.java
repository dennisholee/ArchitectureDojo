package io.forest.archunit.domains.model;

import java.util.Objects;

public class MobileNumber {
	
	private String countryCode;
	
	private String number;
	
	public MobileNumber(String countryCode, String number) {
		this.countryCode = countryCode;
		this.number = number;
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(countryCode, number);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MobileNumber other = (MobileNumber) obj;
		return Objects.equals(countryCode, other.countryCode) && Objects.equals(number, other.number);
	}
	
	

}
