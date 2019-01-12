package tree;

import lombok.AllArgsConstructor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@AllArgsConstructor
public class DictionaryArgument implements ReadableArgument {

	private String dictionaryKey;

	@Override
	public boolean isDictionaryArgument() {
		return true;
	}

	@Override
	public boolean isConstantValue() {
		return false;
	}

	@Override
	public String readKey() {
		return dictionaryKey;
	}

	@Override
	public int readValue() {
		throw new NotImplementedException();
	}

}
