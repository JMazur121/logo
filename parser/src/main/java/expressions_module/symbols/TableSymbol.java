package expressions_module.symbols;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TableSymbol extends Symbol {

	private final String identifier;

	@Override
	public boolean isConstant() {
		return false;
	}

}
