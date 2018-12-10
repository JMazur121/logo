package tokenizer;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class Token {

	private final TokenType tokenType;
	private final TokenPosition position;

}
