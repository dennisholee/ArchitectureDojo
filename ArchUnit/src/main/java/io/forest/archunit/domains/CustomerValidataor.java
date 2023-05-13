package io.forest.archunit.domains;

public class CustomerValidataor extends Validator {

	public CustomerValidataor(Customer customer, ValidationNotificationHandler validationHandler) {
		super(validationHandler);
	}

	@Override
	public void validate() {
	}

}
