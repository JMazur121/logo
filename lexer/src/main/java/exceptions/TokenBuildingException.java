package exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tokenizer.TokenPosition;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TokenBuildingException extends Exception {

	private TokenPosition position;
	private String representation;
	private String errorCause;

	@Override
	public String getMessage() {
		return String.format("%s TokenBuildingException -> %s. Read so far : \"%s\"", position.toString(), errorCause, representation);
	}

}
