package tree;

public interface ReadableArgument {

	boolean isDictionaryArgument();
	boolean isConstantValue();
	String readKey();
	int readValue();

}
