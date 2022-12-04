import computation.contextfreegrammar.ContextFreeGrammar;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyGrammarTest {
    @Test
    public void test1_makeGrammar_spit_variable_by_regex() {
        String input = "ST1\n" +
                "TF1\n" +
                "L1S1\n" +
                "ST1\n" +
                "TF1\n" +
                "L1S1\n" +
                "TF1\n" +
                "L1S1\n" +
                "L1S1\n" +
                "PT\n" +
                "MF\n" +
                "SR1";
        String[] lines = input.split("\n");
        Pattern pattern = Pattern.compile("([A-Z][0-9]?)([A-Z][0-9]?)");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            boolean matchFound = matcher.find();
            if(matchFound) {
                String actual = matcher.group(1) + matcher.group(2);
                Assertions.assertThat(actual).isEqualTo(line);
            }
        }
    }

    @Test
    public void test1_makeGrammar(){
        ContextFreeGrammar actual = MyGrammar.makeGrammar();
        System.out.println(actual);
    }

    @Test
    public void test2_makeGrammar(){
        ContextFreeGrammar grammar = MyGrammar.makeGrammar();
        System.out.println(grammar);

        String expected = "S₀ → ST₁\n" +
                "S₀ → TF₁\n" +
                "S₀ → L₁S₁\n" +
                "S₀ → 1\n" +
                "S₀ → 0\n" +
                "S₀ → x\n" +
                "S → ST₁\n" +
                "S → TF₁\n" +
                "S → L₁S₁\n" +
                "S → 1\n" +
                "S → 0\n" +
                "S → x\n" +
                "T → TF₁\n" +
                "T → L₁S₁\n" +
                "T → 1\n" +
                "T → 0\n" +
                "T → x\n" +
                "F → L₁S₁\n" +
                "F → 1\n" +
                "F → 0\n" +
                "F → x\n" +
                "T₁ → PT\n" +
                "F₁ → MF\n" +
                "S₁ → SR₁\n" +
                "P → +\n" +
                "M → *\n" +
                "R₁ → )\n" +
                "L₁ → (\n";
    }

}