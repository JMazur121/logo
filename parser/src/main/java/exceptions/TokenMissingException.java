package exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tokenizer.Token;

@Getter
@Setter
@AllArgsConstructor
public class TokenMissingException extends Exception{

	private String parsedExpression;
	private String expected;
	private Token received;

	@Override
	public String getMessage() {
		return String.format("Exception while building %s. Expected : %s , but received : %s",parsedExpression, expected, received);
	}

}
