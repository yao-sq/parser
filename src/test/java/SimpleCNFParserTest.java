import computation.contextfreegrammar.*;
import computation.parsetree.ParseTreeNode;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.StrictAssertions.assertThat;


public class SimpleCNFParserTest {
    private Parser parser = new Parser();
    private ContextFreeGrammar cfg = ContextFreeGrammar.simpleCNF();

    /*
A₀ → ε
A₀ → ZY
A₀ → ZB
A → ZY
A → ZB
B → AY
Z → 0
Y → 1
     */

    @Test
    public void test1_isInLanguage() {
        Word input = new Word("0011");
        boolean actual = parser.isInLanguage(cfg, input);
        assertThat(actual).isTrue();
    }

    @Test
    public void test2_isInLanguage() {
        Word input = new Word("1011");
        boolean actual = parser.isInLanguage(cfg, input);
        assertThat(actual).isFalse();
    }

    @Test
    public void test3_isInLanguage() {
        Word input = new Word();
        boolean actual = parser.isInLanguage(cfg, input);
        assertThat(actual).isTrue();
    }

    @Test
    public void test1_generateParseTree() {
        Word input = new Word("0011");

        ParseTreeNode expected = n("A0",
                n("Z",
                        n("0")),
                n("B",
                        n("A",
                                n("Z", n("0")),
                                n("Y", n("1"))),
                        n("Y", n("1")))
        );

        ParseTreeNode actual = parser.generateParseTree(cfg, input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void test2_generateParseTree() {
        Word input = new Word("1011");

        ParseTreeNode expected = null;

        ParseTreeNode actual = parser.generateParseTree(cfg, input);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void test3_generateParseTree() {
        Word input = new Word();

        ParseTreeNode expected = n("A0");

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