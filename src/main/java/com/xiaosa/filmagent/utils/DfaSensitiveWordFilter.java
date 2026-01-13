package com.xiaosa.filmagent.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DfaSensitiveWordFilter {

    private static final char REPLACEMENT = '*';

    // 根节点
    private final TrieNode rootNode = new TrieNode();

    /**e
     * 添加敏感词到 Trie 树
     */
    public void addSensitiveWords(Collection<String> words) {
        for (String word : words) {
            if (word == null || word.isEmpty()) continue;
            TrieNode tempNode = rootNode;
            for (char c : word.toCharArray()) {
                tempNode = tempNode.addChild(c);
            }
            tempNode.setEnd(true);
        }
    }

    /**
     * 替换文本中的所有敏感词为 *
     */
    public String filter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        int begin = 0;

        while (begin < text.length()) {
            TrieNode node = rootNode;
            int position = begin;
            int matchEnd = -1; // 记录当前 begin 起始下匹配到的最长敏感词结束位置

            // 从 begin 开始，尽可能向前匹配
            while (position < text.length()) {
                char c = text.charAt(position);
                if (!node.hasChild(c)) {
                    break; // 无法继续匹配
                }
                node = node.getChild(c);
                if (node.isEnd()) {
                    matchEnd = position; // 记录有效匹配结束位置（贪心：越往后越长）
                }
                position++;
            }

            if (matchEnd != -1) {
                // 有匹配：从 begin 到 matchEnd 全部替换为 *
                for (int i = begin; i <= matchEnd; i++) {
                    result.append(REPLACEMENT);
                }
                begin = matchEnd + 1; // 跳过已处理部分
            } else {
                // 无匹配：保留当前字符
                result.append(text.charAt(begin));
                begin++;
            }
        }

        return result.toString();
    }

    // Trie 节点内部类
    private static class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<>();
        private boolean end = false;

        public boolean hasChild(char c) {
            return children.containsKey(c);
        }

        public TrieNode getChild(char c) {
            return children.get(c);
        }

        public TrieNode addChild(char c) {
            return children.computeIfAbsent(c, k -> new TrieNode());
        }

        public boolean isEnd() {
            return end;
        }

        public void setEnd(boolean end) {
            this.end = end;
        }
    }
}
