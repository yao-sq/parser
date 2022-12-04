import computation.contextfreegrammar.ContextFreeGrammar;
import computation.contextfreegrammar.Rule;
import computation.contextfreegrammar.Symbol;
import computation.contextfreegrammar.Word;
import computation.parser.IParser;
import computation.parsetree.ParseTreeNode;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Parser implements IParser {

    @Override
    public boolean isInLanguage(ContextFreeGrammar cfg, Word w) {
        return generateParseTree(cfg, w) != null;
    }

    @Override
    public ParseTreeNode generateParseTree(ContextFreeGrammar cfg, Word w) {
        Node root = new Node(cfg.getStartVariable());
        int stepsRemaining = growTree(root, root, cfg, w, w.length() * 2 - 1);
        return (stepsRemaining == 0) ? root.convert() : null;
    }

    /**
     * Grow (mutating) the given tree, expanding to matching/possible children, recursively,
     * up to the step limit.
     *
     * @param root           the root of the tree to grow/expand
     * @param tree           the tree to grow/expand - will be mutated
     * @param cfg            the context free grammar to use for expanding
     * @param w              the input word to try and match
     * @param stepsRemaining the step limit at which to stop growing
     * @return the step remaining / not taken; zero means exact number of steps was taken
     */
    protected int growTree(Node root, Node tree, ContextFreeGrammar cfg, Word w, int stepsRemaining) {
        if (stepsRemaining == 0) return -1;
        loopRules: for (Rule rule : rulesForVariable(cfg, tree.getSymbol())) {
            tree.setChildren(rule.getExpansion().stream().map(Node::new).toArray(Node[]::new));
            int steps = stepsRemaining - 1;

            Word generatedWord = collectTerminals(root);
            if (generatedWord.equals(w)) return 0;
            if (!isPossibleExpansion(generatedWord, w))
                continue;

            //handle the epsilon case
            if (tree.getChildren().length == 0) {
                continue;
            }

            if (Arrays.stream(tree.getChildren()).allMatch(n -> n.getSymbol().isTerminal()))
                return steps;

            for (int i = tree.getChildren().length - 1; i >= 0; i--) {
                steps = growTree(root, tree.getChildren()[i], cfg, w, steps);
                if (steps < 0) continue loopRules;

            }
            if (steps >= 0) return steps;
        }
        return -1;
    }


    protected Word collectTerminals(Node root) {
        return new Word(streamNodes(root, n -> n.getSymbol().isTerminal())
                .map(Node::getSymbol)
                .toArray(Symbol[]::new));
    }

    protected boolean isPossibleExpansion(Word partialWord, Word sourceWord) {
        return endsWith(sourceWord, partialWord);
    }


    // Tree help methods //

    public static Stream<Node> streamNodes(Node tree, Predicate<Node> filter) {
        Stream.Builder<Node> result = Stream.builder();
        walkTree(tree, node -> { if (filter.test(node)) result.add(node); });
        return result.build();
    }

    public static void walkTree(Node tree, Consumer<Node> visitorEnter) {
        visitorEnter.accept(tree);
        for (Node child : tree.getChildren()) {
            walkTree(child, visitorEnter);
        }
    }


    // Extension methods for ContextFreeGrammar //

    public static List<Rule> rulesForVariable(ContextFreeGrammar cfg, Symbol symbol) {
        return cfg.getRules().stream()
                .filter(r -> r.getVariable().equals(symbol))
                .collect(toList());
    }


    // Extension methods for Word //

    public static boolean endsWith(Word source, Word suffix) {
        int d = source.length() - suffix.length();
        if (d < 0) return false;
        for (int i = suffix.length()-1; i >=0 ; i--) {
            if (!source.get(d+i).equals(suffix.get((i)))){
                return false;
            }
        }
        return true;
    }



    private static class Node {
        private Symbol symbol;
        private Node[] children;


        public Node(Symbol symbol, Node... children) {
            this.symbol = symbol;
            this.children = children;
        }

        public Symbol getSymbol() {
            return symbol;
        }

        public Node[] getChildren() {
            return children;
        }

        public void setChildren(Node[] children) {
            this.children = children;
        }

        public ParseTreeNode convert() {
            return new ParseTreeNode(symbol, Arrays.stream(children).map(Node::convert).toArray(ParseTreeNode[]::new));
        }
    }
}