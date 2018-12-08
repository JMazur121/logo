package tokenizer;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Token {

	private TokenType tokenType;
	private TokenPosition position;

}
