package tokenizer;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {

	private TokenType tokenType;
	private int line;
	private int positionInLine;
	private int absolutePosition;

}
