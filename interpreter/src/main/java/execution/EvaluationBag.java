package execution;

import lombok.Getter;

@Getter
public class EvaluationBag {

	private int value;
	private boolean isBoolean;

	private EvaluationBag(int value, boolean isBoolean) {
		this.value = value;
		this.isBoolean = isBoolean;
	}

	public static EvaluationBag newNumericBag(int value) {
		return new EvaluationBag(value, false);
	}

	public static EvaluationBag newBooleanBag(boolean value) {
		if (value)
			return new EvaluationBag(1, true);
		else
			return new EvaluationBag(0, true);
	}

	public boolean readValue() {
		return value == 1;
	}

}
