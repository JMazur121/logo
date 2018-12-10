package agent;

import lombok.Getter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class CharacterStreamAgent {

	private Reader streamReader;
	@Getter
	private int bufferedPosition;
	private int inStreamPosition;
	private char buffer;
	@Getter
	private boolean isBufferContainingChar;
	@Getter
	private boolean reachedEnd;
	public static final char CHAR_ETX = '\u0003';

	public CharacterStreamAgent() {
		resetAgent();
	}

	public void resetAgent() {
		closeReader();
		streamReader = null;
		bufferedPosition = 0;
		inStreamPosition = 1;
		isBufferContainingChar = false;
		reachedEnd = false;
	}

	public void handleStream(InputStream inputStream) {
		streamReader = new InputStreamReader(inputStream);
	}

	public void handleStream(InputStream inputStream, Charset charset) {
		streamReader = new InputStreamReader(inputStream, charset);
	}

	public char bufferAndGetChar() throws IOException {
		if (reachedEnd)
			return CHAR_ETX;
		if (isBufferContainingChar)
			return buffer;
		int character = streamReader.read();
		++inStreamPosition;
		if (character == -1)
			return handleEndOfTextReached();
		isBufferContainingChar = true;
		++bufferedPosition;
		buffer = (char) character;
		return buffer;
	}

	public void commitBufferedChar() {
		if (isBufferContainingChar)
			isBufferContainingChar = false;
	}

	public void closeReader() {
		if (streamReader != null) {
			try {
				streamReader.close();
			} catch (IOException ignored) {
			}
		}
	}

	private char handleEndOfTextReached() {
		reachedEnd = true;
		closeReader();
		return CHAR_ETX;
	}

}
