package agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

public class CharacterStreamAgent {

	private Reader streamReader;
	private InputStream currentStream;
	private int bufferedPosition;
	private int inStreamPosition;
	private char buffer;
	private boolean bufferContainsChar;
	private boolean isCorrupted;

	public CharacterStreamAgent() {
		resetAgent();
	}

	public void resetAgent() {
		streamReader = null;
		currentStream = null;
		bufferedPosition = 1;
		inStreamPosition = 1;
		bufferContainsChar = true;
		isCorrupted = false;
	}

	public int getBufferedPosition() {
		return bufferedPosition;
	}

	public boolean isCorrupted() {
		return isCorrupted;
	}

	public void handleStream(InputStream inputStream) {
		streamReader = new InputStreamReader(inputStream);
		currentStream = inputStream;
	}

	public Optional<Character> bufferAndGetChar() {
		if (isCorrupted)
			return Optional.empty();
		if (bufferContainsChar)
			return Optional.of(buffer);
		try {
			int character = streamReader.read();
			inStreamPosition++;
			if (character == -1)
				return Optional.empty();
			bufferContainsChar = true;
			char newChar = (char)character;
			return Optional.of(newChar);
		} catch (IOException e) {
			isCorrupted = true;
			return Optional.empty();
		}
	}

	public void commitBufferedChar() {
		if (bufferContainsChar) {
			bufferContainsChar = false;
			bufferedPosition++;
		}
	}
}
