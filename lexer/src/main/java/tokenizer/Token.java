package tokenizer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Token {

	private TokenType tokenType;
	private int line;
	private int positionInLine;
	private int absolutePosition;

}
