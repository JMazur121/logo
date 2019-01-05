package tokenizer;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class Token {

	private final TokenType tokenType;
	private final TokenPosition position;

	@Override
	public String toString() {
		return String.format("%s -> %s", position, tokenType);
	}

}
