package agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class CharacterStreamAgent {

	private Reader streamReader;
	private int bufferedPosition;
	private int inStreamPosition;
	private char buffer;
	private boolean bufferContainsChar;
	private boolean isCorrupted;
	private boolean reachedEnd;
	public static final char CHAR_ETX = '\u0003';
	public static final char CHAR_NULL = '\u0000';

	public CharacterStreamAgent() {
		resetAgent();
	}

	public void resetAgent() {
		streamReader = null;
		bufferedPosition = 0;
		inStreamPosition = 1;
		bufferContainsChar = false;
		isCorrupted = false;
		reachedEnd = false;
	}

	public int getBufferedPosition() {
		return bufferedPosition;
	}

	public boolean isCorrupted() {
		return isCorrupted;
	}

	public boolean bufferContainsChar() {
		return bufferContainsChar;
	}

	public boolean reachedEnd() {
		return reachedEnd;
	}

	public void handleStream(InputStream inputStream) {
		streamReader = new InputStreamReader(inputStream);
	}

	public void handleStream(InputStream inputStream, Charset charset) {
		streamReader = new InputStreamReader(inputStream, charset);
	}

	public char bufferAndGetChar() {
		if (reachedEnd)
			return CHAR_ETX;
		if (isCorrupted)
			return CHAR_NULL;
		if (bufferContainsChar)
			return buffer;
		try {
			int character = streamReader.read();
			++inStreamPosition;
			if (character == -1)
				return handleEndOfTextReached();
			bufferContainsChar = true;
			++bufferedPosition;
			buffer = (char) character;
			return buffer;
		} catch (IOException e) {
			return handleStreamCorruption();
		}
	}

	public void commitBufferedChar() {
		if (bufferContainsChar)
			bufferContainsChar = false;
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

	private char handleStreamCorruption() {
		isCorrupted = true;
		closeReader();
		return CHAR_NULL;
	}

}
