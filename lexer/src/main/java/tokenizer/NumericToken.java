package tokenizer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumericToken extends Token {

	private int value;

	public NumericToken(TokenPosition position, int value) {
		super(TokenType.T_NUMERIC_CONSTANT, position);
		this.value = value;
	}

}
