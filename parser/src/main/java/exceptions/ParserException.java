package exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ParserException extends Exception {

	String message;

	@Override
	public String getMessage() {
		return message;
	}

}
