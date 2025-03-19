/*
 * TC: O(nk) for adding n sentences of average length k to the Trie,
 * O(np) for searching for a prefix of length p, O(n log 3) for extracting
 * the top 3 sentences from n sentences in the worst case
 * SC: O(nk) for the Trie, O(n) for the history
 */
import java.util.*;
import java.util.stream.Collectors;

public class AutocompleteSystem {

    static class Sentence implements Comparable<Sentence> {
        String sentence;
        int frequency;

        Sentence(String s, int f) {
            sentence = s;
            frequency = f;
        }

        @Override
        public int compareTo(Sentence b) {
            if(this.frequency == b.frequency) {
                return b.sentence.compareTo(this.sentence);
            } else {
                return this.frequency - b.frequency;
            }
        }
    }

    static class TrieNode {
        TrieNode[] children;
        int frequency;

        TrieNode() {
            // lowercase letters and ' '
            children = new TrieNode[27];
            frequency = 0;
        }
    }

    /**
     * Add sentences to the Trie, storing
     * the frequencies at the end
     * (not that spaces need to be specially handled)
     * @param s Sentence to add
     * @param frequency Frequency
     */
    void add(String s, int frequency) {
        TrieNode node = root;
        for(char c: s.toCharArray()) {
            if(c == ' ') {
                if(node.children[26] == null) {
                    node.children[26] = new TrieNode();
                }
                node = node.children[26];
            }
            else {
                if(node.children[c - 'a'] == null) {
                    node.children[c - 'a'] = new TrieNode();
                }
                node = node.children[c - 'a'];
            }
        }
        node.frequency = frequency;
    }

    /**
     * Find all sentences starting with a particular
     * @param prefix . Goes until the length of the prefix
     * in the Trie and then does a dfs from there
     * @return a list of Sentences starting with the given prefix
     */
    List<Sentence> searchPrefix(String prefix) {
        TrieNode node = root;
        StringBuilder sentenceBuilder = new StringBuilder();
        for(char c: prefix.toCharArray()) {
            sentenceBuilder.append(c);
            if(c == ' ') {
                node = node.children[26];
            }
            else {
                node = node.children[c - 'a'];
            }
            if(node == null) {
                break;
            }
        }
        List<Sentence> sentences = new ArrayList<>();
        dfs(node, sentenceBuilder, sentences);
        return sentences;
    }

    void dfs(TrieNode node, StringBuilder sBuilder, List<Sentence> sentences) {
        if(node == null) return;
        // non-zero frequency indicates end of sentence in Trie
        if(node.frequency != 0) {
            sentences.add(new Sentence(sBuilder.toString(), node.frequency));
        }
        for(char c = 'a'; c <= 'z'; c++) {
            sBuilder.append(c);
            dfs(node.children[c - 'a'], sBuilder, sentences);
            sBuilder.deleteCharAt(sBuilder.length() - 1);
        }
        sBuilder.append(' ');
        dfs(node.children[26], sBuilder, sentences);
        sBuilder.deleteCharAt(sBuilder.length() - 1);
    }

    /**
     * Uses a heap to extract the top 3 sentences from
     * @param sentences by frequency/lexicographic order.
     */
    List<String> top3(List<Sentence> sentences) {
        PriorityQueue<Sentence> top3 = new PriorityQueue<>();
        for(Sentence sentence: sentences) {
            top3.add(sentence);
            if(top3.size() > 3) {
                top3.poll();
            }
        }
        List<Sentence> top3List = new ArrayList<>(top3);
        top3List.sort(Comparator.reverseOrder());
        return top3List.stream().map(s -> s.sentence).collect(Collectors.toList());
    }

    // builds the input until a # is encountered
    StringBuilder inputBuilder = new StringBuilder();

    // stores the history of sentences and their frequencies
    Map<String, Integer> history = new HashMap<>();

    // root of the Trie
    TrieNode root;

    public AutocompleteSystem(String[] sentences, int[] times) {
        root = new TrieNode();
        // add the initial sentences to the Trie/history
        for(int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i];
            history.put(sentence, times[i]);
            add(sentence, history.get(sentence));
        }
    }

    public List<String> input(char c) {
        if(c == '#') {
            // at the end of an input add the input to
            // the Trie and to the history
            String input = inputBuilder.toString();
            inputBuilder = new StringBuilder();
            history.putIfAbsent(input, 0);
            history.compute(input, (k, v) -> v + 1);
            add(input, history.get(input));
            return new ArrayList<>();
        }
        inputBuilder.append(c);
        // return the top 3 sentences starting with this input
        return top3(searchPrefix(inputBuilder.toString()));
    }
}
