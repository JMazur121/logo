package exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tokenizer.Token;

@Getter
@Setter
@AllArgsConstructor
public class ExpressionCorruptedException extends Exception{

	private Token received;

	@Override
	public String getMessage() {
		return String.format("ExpressionCorrupted - cannot parse expression - operator mismatching : %s", received);
	}

}
