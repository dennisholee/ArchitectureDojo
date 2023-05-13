package io.forest.archunit.domains;

public abstract class Validator {

	private ValidationNotificationHandler validationHandler;

	public abstract void validate();

	public Validator(ValidationNotificationHandler validationHandler) {
		this.setNotificationHandler(validationHandler);
	}

	protected ValidationNotificationHandler getNotificationHandler() {
		return this.validationHandler;
	}

	private void setNotificationHandler(ValidationNotificationHandler validationHandler) {
		this.validationHandler = validationHandler;

	}

}
