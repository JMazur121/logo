package exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UnknownTokenException extends Exception {

	private int absolutePosition;
	private int lineNumber;
	private int numberInLine;
	private String representation;

	@Override
	public String getMessage() {
		return String.format("(Position:%d|Line:%d|Column:%d) " +
				"Cannot recognize token \"%s\"",absolutePosition,lineNumber,numberInLine,representation);
	}
}
