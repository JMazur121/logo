package tokenizer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperatorToken extends Token{

	private boolean isUnary;

	public OperatorToken(TokenType tokenType, int line, int positionInLine, int absolutePosition, boolean isUnary) {
		super(tokenType, line, positionInLine, absolutePosition);
		this.isUnary = isUnary;
	}

}
