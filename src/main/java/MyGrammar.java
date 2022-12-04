import computation.contextfreegrammar.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyGrammar {

	public static final Pattern PATTERN_VARIABLE_EXPANSION = Pattern.compile("\\w\\d*");
	public static final String[] DEFINITION = {
			"S0 -> ST1 | TF1 | L1S1 | 1 | 0 | x",
			"S -> ST1 | TF1 | L1S1 | 1 | 0 | x",
			"T -> TF1 | L1S1 | 1 | 0 | x",
			"F -> L1S1 | 1 | 0 | x",
			"T1 -> PT",
			"F1 -> MF",
			"S1 -> SR1",
			"P -> +",
			"M -> *",
			"R1 -> )",
			"L1 -> ("
	};

	public static ContextFreeGrammar makeGrammar() {
		List<Rule> rules = new ArrayList<>();

		for (String line : DEFINITION) {
			rules.addAll(parseRule(line));
		}

		return new ContextFreeGrammar(rules);
	}

	protected static List<Rule> parseRule(String line){
		String[] parts = line.split("\\s*->\\s*");
		List<Rule> rules = new ArrayList<>();
		Variable name = new Variable(parts[0]);
		for (String expansion : parts[1].split("\\s*\\|\\s*")) {
			Rule rule = new Rule(name, getExpansion(expansion));
			rules.add(rule);
		}
		return rules;
	}

	protected static Word getExpansion(String expansion) {
		if (expansion.length() ==1) {
			return new Word(new Terminal(expansion.charAt(0)));
		}
		Matcher matcher = PATTERN_VARIABLE_EXPANSION.matcher(expansion);
		List<Symbol> symbols = new ArrayList<>(2);
		while (matcher.find()){
			symbols.add(new Variable(matcher.group()));
		}
		if (symbols.isEmpty()) {
			throw new IllegalArgumentException("Unsupported pattern for rule expansion: "+expansion);
		}
		return new Word(symbols.toArray(new Symbol[0]));
	}
}
