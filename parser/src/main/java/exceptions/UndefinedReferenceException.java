package exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tokenizer.Token;

@Getter
@Setter
@AllArgsConstructor
public class UndefinedReferenceException extends Exception {

	private Token expectedReference;

	@Override
	public String getMessage() {
		return String.format("UndefinedReferenceError. Expected token : %s", expectedReference);
	}

}
