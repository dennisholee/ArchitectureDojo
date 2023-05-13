package io.forest.archunit.domains;

import io.forest.archunit.domains.model.ContactInformation;
import io.forest.archunit.domains.model.CustomerProfileId;
import io.forest.archunit.domains.model.CustomerStatus;
import io.forest.archunit.domains.model.Name;

public class Customer implements ISetCustomerPreferences {

	private CustomerProfileId customerProfileId;

	private Name name;

	private CustomerStatus customerStatus;

	private ContactInformation contactInformation;

	private CustomerPreferences customerPreferences;

	public Customer(CustomerProfileId customerProfileId) {
		this.setCustomerProfileId(this.customerProfileId);

	}
	
	public void offboardCustomer() {
		this.customerStatus = CustomerStatus.INACTIVE;
	}

	public void registerCustomer() {
		this.customerStatus = CustomerStatus.ACTIVE;
	}

	@Override
	public void setPreferences(CustomerPreferences customerPreferences) {
		this.customerPreferences = customerPreferences;
	}

	public void validate(ValidationNotificationHandler notificationHandler) {
		new CustomerValidataor(this, notificationHandler).validate();
	}

	protected void setCustomerProfileId(CustomerProfileId customerProfileId) {
		this.customerProfileId = customerProfileId;
	}

}
