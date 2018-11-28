import agent.CharacterStreamAgent;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class StreamAgentTests {

	@Test
	public void resetAgent_agentCreated_bufferIsEmpty() {
		//when
		CharacterStreamAgent agent = new CharacterStreamAgent();
		//then
		assertThat(agent.bufferContainsChar()).isFalse();
	}

}
