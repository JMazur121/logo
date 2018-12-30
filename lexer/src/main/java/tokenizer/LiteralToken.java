package tokenizer;

import lombok.Getter;

@Getter
public class LiteralToken extends Token{

	private final String word;

	public LiteralToken(TokenType tokenType, TokenPosition position, String word) {
		super(tokenType, position);
		this.word = word;
	}

}
