package agent;

import exceptions.LexerException;
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

	public LexerAgent() {
		restart();
	}

	public void handleStream(InputStream inputStream) {
		lexer.restart();
		lexer.handleStream(inputStream);
	}

	public Token bufferAndGetToken() throws LexerException {
		if (isBufferContainingToken)
			return bufferedToken;
		isBufferContainingToken = true;
		try {
			bufferedToken = lexer.nextToken();
		} catch (IOException | TokenBuildingException e) {
			throw new LexerException(e.getMessage());
		}
		return bufferedToken;
	}

	public void commitBufferedToken() {
		if (isBufferContainingToken)
			isBufferContainingToken = false;
	}

	public void restart() {
		if (lexer != null)
			lexer.restart();
		else
			lexer = new Lexer();
	}

}
