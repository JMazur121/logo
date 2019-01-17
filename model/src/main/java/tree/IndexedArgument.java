package tree;

import lombok.AllArgsConstructor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@AllArgsConstructor
public class IndexedArgument implements ReadableArgument {

	private int value;
	private boolean isConstantValue;

	@Override
	public boolean isDictionaryArgument() {
		return false;
	}

	@Override
	public boolean isConstantValue() {
		return isConstantValue;
	}

	@Override
	public String readKey() {
		throw new NotImplementedException();
	}

	@Override
	public int readValue() {
		return value;
	}

}
