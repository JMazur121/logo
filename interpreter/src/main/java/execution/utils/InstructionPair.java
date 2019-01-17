package execution.utils;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@AllArgsConstructor
@Getter
public class InstructionPair {

	private String name;
	private String paramsList;

	@Override
	public String toString() {
		return String.format("%s(%s)", name, paramsList);
	}

	public static List<InstructionPair> newInstructionList() {
		return ImmutableList.<InstructionPair>builder()
				.add(new InstructionPair("naprzod", "odleglosc"))
				.add(new InstructionPair("wstecz", "odleglosc"))
				.add(new InstructionPair("prawo", "kat"))
				.add(new InstructionPair("lewo", "kat"))
				.add(new InstructionPair("czysc", ""))
				.add(new InstructionPair("podnies", ""))
				.add(new InstructionPair("opusc", ""))
				.add(new InstructionPair("zamaluj", ""))
				.add(new InstructionPair("obrysForemnego", "liczbaBokow,dlugoscBoku"))
				.add(new InstructionPair("pelnyForemny", "liczbaBokow,dlugoscBoku"))
				.add(new InstructionPair("obrysElipsy", "szerokosc,wysokosc"))
				.add(new InstructionPair("pelnaElipsa", "szerokosc,wysokosc"))
				.add(new InstructionPair("okrag", "promien"))
				.add(new InstructionPair("kolo", "promien"))
				.add(new InstructionPair("przesun", "przesuniecieX,przesuniecieY"))
				.add(new InstructionPair("skok", "doceloweX,doceloweY"))
				.add(new InstructionPair("wypisz", "wyrazenieArytmetyczne"))
				.add(new InstructionPair("kolorPisaka", "skladowaR,skladowaG,skladowaB"))
				.add(new InstructionPair("kolorMalowania", "skladowaR,skladowaG,skladowaB"))
				.build();
	}

}
