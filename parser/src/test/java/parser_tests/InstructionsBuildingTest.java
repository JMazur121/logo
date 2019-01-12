package parser_tests;

import com.google.common.collect.ImmutableMap;
import exceptions.LexerException;
import exceptions.ParserException;
import instructions_module.scope.Scope;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import parser.Parser;
import static org.assertj.core.api.Assertions.*;

public class InstructionsBuildingTest {

	private Map<String, Integer> globalVars = new HashMap<>();
	private Map<String, Scope> knownMethods = new HashMap<>();
	private Parser parser = new Parser(globalVars, knownMethods);


	@Test
	public void getNextScope_givenEmptyStream_reachedETX() throws LexerException, ParserException {
		//before
		ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
		parser.reset();
		parser.handleStream(is);
		//when
		Scope scope = parser.getNetScope();
		//then
		assertThat(scope).isNull();
		assertThat(parser.isReachedETX()).isTrue();
	}

}
