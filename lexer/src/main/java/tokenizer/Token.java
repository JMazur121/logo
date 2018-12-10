package tokenizer;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Token {

	private final TokenType tokenType;
	private final TokenPosition position;

}
