package tokenizer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenPosition {

	private final int line;
	private final int positionInLine;
	private final int absolutePosition;

	@Override
	public String toString() {
		return String.format("(Position:%d|Line:%d|Column:%d) ",absolutePosition, line, positionInLine);
	}

}
