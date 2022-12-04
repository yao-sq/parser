import computation.contextfreegrammar.*;
import computation.parsetree.ParseTreeNode;
import org.assertj.core.api.StrictAssertions;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.StrictAssertions.assertThat;


public class ParserTest {
    private Parser parser = new Parser();
    private ContextFreeGrammar cfg = MyGrammar.makeGrammar();


    @Test
    public void test1_isInLanguage_arithmetic() {
        ContextFreeGrammar grammar = MyGrammar.makeGrammar();
        assertThat(new Parser().isInLanguage(grammar, new Word("1+x"))).isTrue();
    }

    @Test
    public void test2_isInLanguage_arithmetic() {
        ContextFreeGrammar grammar = MyGrammar.makeGrammar();
        assertThat(new Parser().isInLanguage(grammar, new Word("(x)"))).isTrue();
    }

    @Test
    public void test3_isInLanguage_arithmetic() {
        ContextFreeGrammar grammar = MyGrammar.makeGrammar();
        assertThat(new Parser().isInLanguage(grammar, new Word("x*0"))).isTrue();
    }

    @Test
    public void test4_isInLanguage_arithmetic() {
        ContextFreeGrammar grammar = MyGrammar.makeGrammar();
        assertThat(new Parser().isInLanguage(grammar, new Word("(x+0)*1"))).isTrue();   //FIXME:to slow to run, never finish..
    }



    /*
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
     */

    @Test
    public void test1_generateParseTree_1_plus_x() {
        Word input = new Word("1+x");

        ParseTreeNode expected = n("S0",
                n("S", n("1")),
                n("T1",
                        n("P", n("+")),
                        n("T", n("x"))));

        ParseTreeNode actual = parser.generateParseTree(cfg, input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void test2_generateParseTree_x_times_0() {
        Word input = new Word("x*0");

        ParseTreeNode expected = n("S0",
                n("T", n("x")),
                n("F1",
                        n("M", n("*")),
                        n("F", n("0"))));

        ParseTreeNode actual = parser.generateParseTree(cfg, input);
        assertThat(actual).isEqualTo(expected);
    }

    /*
    S0
    T F1
    L1S1  MF
    (S) *1
    S T1
    x PT
     */
    @Test
    public void test3_generateParseTree() {
        Word input = new Word("(x+0)*1");

        ParseTreeNode expected = n("S0",
                n("T",
                        n("L1", n("(")),
                        n("S1", n("S",
                                n("S", n("x")),
                                n("T1",
                                        n("P", n("+")),
                                        n("T", n("0")))),
                                n("R1", n(")")))),
                n("F1",
                        n("M", n("*")),
                        n("F", n("1")))
        );

        ParseTreeNode actual = parser.generateParseTree(cfg, input);
        assertThat(actual).isEqualTo(expected);
    }


    private ParseTreeNode n(String name, ParseTreeNode... children){
        return new ParseTreeNode(toSymbol(name), children);
    }

    private Symbol toSymbol(String name) {
        Optional<Terminal> terminal = cfg.getTerminals().stream()
                .filter(s -> name.equals(s.toString()))
                .findFirst();
        return terminal.isPresent() ? terminal.get() : new Variable(name);
    }
}