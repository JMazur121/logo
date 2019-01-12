package exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExecutionException extends Exception {

	private String message;

	@Override
	public String getMessage() {
		return message;
	}

}
