package tokenizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenPosition {

	private int line;
	private int positionInLine;
	private int absolutePosition;

}
