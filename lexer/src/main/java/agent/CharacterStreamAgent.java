package agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class CharacterStreamAgent {

	private Reader streamReader;
	private int bufferedPosition;
	private int inStreamPosition;
	private char buffer;
	private boolean bufferContainsChar;
	private boolean isCorrupted;
	private boolean reachedEnd;

	public CharacterStreamAgent() {
		resetAgent();
	}

	public void resetAgent() {
		streamReader = null;
		bufferedPosition = 1;
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

	public void handleStream(InputStream inputStream) {
		streamReader = new InputStreamReader(inputStream);
	}

	public char bufferAndGetChar() {
		if (reachedEnd)
			return '\u0003';
		if (isCorrupted)
			return '\u0000';
		if (bufferContainsChar)
			return buffer;
		try {
			int character = streamReader.read();
			inStreamPosition++;
			if (character == -1) {
				reachedEnd = true;
				closeReader();
				return '\u0003';
			}
			bufferContainsChar = true;
			buffer = (char) character;
			return buffer;
		} catch (IOException e) {
			isCorrupted = true;
			closeReader();
			return '\u0000';
		}
	}

	public void commitBufferedChar() {
		if (bufferContainsChar) {
			bufferContainsChar = false;
			bufferedPosition++;
		}
	}

	private void closeReader() {
		if (streamReader != null) {
			try {
				streamReader.close();
			} catch (IOException ignored) {
			}
		}
	}

}
