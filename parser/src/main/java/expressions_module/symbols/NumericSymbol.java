package expressions_module.symbols;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NumericSymbol extends Symbol {

	private final int value;

	@Override
	public boolean isConstant() {
		return true;
	}

}
