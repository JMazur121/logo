package tokenizer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiteralToken extends Token{

	private String word;

	public LiteralToken(TokenType tokenType, TokenPosition position, String word) {
		super(tokenType, position);
		this.word = word;
	}

}
