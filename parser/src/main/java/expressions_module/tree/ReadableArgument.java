package expressions_module.tree;

public interface ReadableArgument {

	boolean isDictionaryArgument();
	boolean isConstantValue();
	String readKey();
	int readValue();

}
