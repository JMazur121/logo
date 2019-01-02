package agent;

import exceptions.TokenBuildingException;
import lombok.Getter;
import tokenizer.Lexer;
import tokenizer.Token;
import java.io.IOException;
import java.io.InputStream;

public class LexerAgent {

	private Lexer lexer;
	private Token bufferedToken;
	@Getter
	private boolean isBufferContainingToken;

	public LexerAgent(InputStream newStream) {
		lexer.handleStream(newStream);
	}

	public void restart(InputStream newStream) {
		if (lexer != null)
			lexer.restart();
		else
			lexer = new Lexer();
		lexer.handleStream(newStream);
	}

	public Token bufferAndGetToken() throws IOException, TokenBuildingException {
		if (isBufferContainingToken)
			return bufferedToken;
		isBufferContainingToken = true;
		bufferedToken = lexer.nextToken();
		return bufferedToken;
	}

	public void commitBufferedToken() {
		if (isBufferContainingToken)
			isBufferContainingToken = false;
	}

}
