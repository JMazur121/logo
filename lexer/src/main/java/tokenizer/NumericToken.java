package tokenizer;

import lombok.Getter;

@Getter
public class NumericToken extends Token {

	private final int value;

	public NumericToken(TokenPosition position, int value) {
		super(TokenType.T_NUMERIC_CONSTANT, position);
		this.value = value;
	}

}
