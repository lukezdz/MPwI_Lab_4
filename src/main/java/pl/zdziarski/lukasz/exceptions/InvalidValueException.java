package pl.zdziarski.lukasz.exceptions;

public class InvalidValueException extends Exception {
	public InvalidValueException(String point, String dimension, boolean wasTooBig) {
		super(prepareMessage(point, dimension, wasTooBig));
	}

	public InvalidValueException(String dimension) {
		super(String.format("%s cannot be smaller than 0", dimension));
	}

	private static String prepareMessage(String point, String dimension, boolean wasTooBig) {
		String message = String.format("Invalid value for the %s point. ", point);
		if (wasTooBig) {
			message += String.format("It cannot be bigger than %s", dimension);
		} else {
			message += "It cannot be smaller than 1";
		}
		return message;
	}
}
