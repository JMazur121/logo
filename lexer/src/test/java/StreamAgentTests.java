import agent.CharacterStreamAgent;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import static agent.CharacterStreamAgent.*;
import static org.assertj.core.api.Assertions.*;

public class StreamAgentTests {

	private CharacterStreamAgent agent = new CharacterStreamAgent();

	@Test
	public void resetAgent_agentCreated_bufferIsEmpty() {
		//when
		agent.resetAgent();
		//then
		assertThat(agent.isBufferContainingChar()).isFalse();
	}

	@Test
	public void resetAgent_agentCreated_agentIsNotCorrupted() {
		//when
		agent.resetAgent();
		//then
		assertThat(agent.isCorrupted()).isFalse();
	}

	@Test
	public void resetAgent_agentCreated_agentNotReachedEnd() {
		//when
		agent.resetAgent();
		//then
		assertThat(agent.isReachedEnd()).isFalse();
	}

	@Test
	public void resetAgent_agentCreated_positionEqualsZero() {
		//when
		agent.resetAgent();
		//then
		assertThat(agent.getBufferedPosition()).isEqualTo(0);
	}

	@Test
	public void bufferAndGetChar_readingEmptyStream_returnsETX() {
		//before
		agent.resetAgent();
		agent.handleStream(emptyStream());
		//when
		char character = agent.bufferAndGetChar();
		//then
		assertThat(character).isEqualTo(CHAR_ETX);
	}

	@Test
	public void bufferAndGetChar_readingEmptyStream_noCharIsBuffered() {
		//before
		agent.resetAgent();
		agent.handleStream(emptyStream());
		//when
		agent.bufferAndGetChar();
		//then
		assertThat(agent.isBufferContainingChar()).isFalse();
	}

	@Test
	public void bufferAndGetChar_readingEmptyStream_positionIsUnchanged() {
		//before
		agent.resetAgent();
		agent.handleStream(emptyStream());
		//when
		agent.bufferAndGetChar();
		//then
		assertThat(agent.getBufferedPosition()).isEqualTo(0);
	}

	@Test
	public void bufferAndGetChar_callingAfterReachingEnd_stillReturnsETX() {
		//before
		agent.resetAgent();
		agent.handleStream(emptyStream());
		agent.bufferAndGetChar();
		//when
		char character = agent.bufferAndGetChar();
		//then
		assertThat(character).isEqualTo(CHAR_ETX);
	}

	@Test
	public void bufferAndGetChar_nonEmptyStream_returnsFirstChar() {
		//before
		agent.resetAgent();
		agent.handleStream(sampleStream());
		//when
		char character = agent.bufferAndGetChar();
		//then
		assertThat(character).isEqualTo('s');
	}

	@Test
	public void bufferAndGetChar_readNewChar_bufferIsNotEmpty() {
		//before
		agent.resetAgent();
		agent.handleStream(sampleStream());
		//when
		char character = agent.bufferAndGetChar();
		//then
		assertThat(agent.isBufferContainingChar()).isTrue();
	}

	@Test
	public void bufferAndGetChar_readAndNotCommited_positionChanged() {
		//before
		agent.resetAgent();
		agent.handleStream(sampleStream());
		//when
		agent.bufferAndGetChar();
		//then
		assertThat(agent.getBufferedPosition()).isEqualTo(1);
	}

	@Test
	public void bufferAndGetChar_readAfterCommit_nextCharIsReturned() {
		//before
		agent.resetAgent();
		agent.handleStream(sampleStream());
		agent.bufferAndGetChar();
		agent.commitBufferedChar();
		//when
		char nextChar = agent.bufferAndGetChar();
		//then
		assertThat(nextChar).isEqualTo('1');
	}

	@Test
	public void bufferAndGetChar_readAfterLastChar_returnsETX() {
		//before
		agent.resetAgent();
		agent.handleStream(sampleStream());
		agent.bufferAndGetChar();
		agent.commitBufferedChar();
		agent.bufferAndGetChar();
		agent.commitBufferedChar();
		//when
		char character = agent.bufferAndGetChar();
		//then
		assertThat(character).isEqualTo(CHAR_ETX);
	}

	@Test
	public void commitBufferedChar_noCharInBuffer_positionIsUnchanged() {
		//before
		agent.resetAgent();
		//when
		agent.commitBufferedChar();
		//then
		assertThat(agent.getBufferedPosition()).isEqualTo(0);
	}

	@Test
	public void commitBufferedChar_charIsBuffered_positionIsUnchanged() {
		//before
		agent.resetAgent();
		agent.handleStream(sampleStream());
		agent.bufferAndGetChar();
		int before = agent.getBufferedPosition();
		//when
		agent.commitBufferedChar();
		//then
		assertThat(agent.getBufferedPosition()).isEqualTo(before);
	}

	@Test
	public void commitBufferedChar_charIsBuffered_bufferBecomesEmpty() {
		//before
		agent.resetAgent();
		agent.handleStream(sampleStream());
		agent.bufferAndGetChar();
		//when
		agent.commitBufferedChar();
		//then
		assertThat(agent.isBufferContainingChar()).isFalse();
	}

	private ByteArrayInputStream emptyStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	private ByteArrayInputStream sampleStream() {
		return new ByteArrayInputStream("s1".getBytes());
	}

}
