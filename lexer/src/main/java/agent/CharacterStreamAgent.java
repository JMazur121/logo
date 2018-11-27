package agent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class CharacterStreamAgent {

	private Reader streamReader;
	private InputStream currentStream;
	private int bufferedPosition;
	private int inStreamPosition;
	private char buffer;
	private boolean isBufferEmpty;

	public CharacterStreamAgent() {
		streamReader = null;
		currentStream = null;
		bufferedPosition = 0;
		inStreamPosition = 0;
		isBufferEmpty = true;
	}

	public void handleStream(InputStream inputStream) {
		streamReader = new InputStreamReader(inputStream);
		currentStream = inputStream;
	}

}
