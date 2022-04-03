package com.redblue.junit;

import org.springframework.util.Assert;

public class ComparisonCompactor {

    private static final String ELLIPSIS = "...";
    private static final String DELTA_END = "]";
    private static final String DELTA_START = "[";

    private int contextLength;
    private String expected;
    private String actual;
    private String compactExpected;
    private String compactActual;
    private int prefixLength;
    private int suffixLength;

    public ComparisonCompactor(int contextLength, String expected, String actual) {
        this.contextLength = contextLength;
        this.expected = expected;
        this.actual = actual;
    }

    public String compact(String message) {
        if (formatCompactedComparison()) {
            compactExpectedAndActual();
            return String.format("%s %s %s", message, compactExpected, compactActual);
        } else {
            return String.format("%s %s %s", message, expected, actual);
        }
    }

    private void compactExpectedAndActual() {
        findCommonPrefixAmdSuffix();
        compactExpected = compactString(expected);
        compactActual = compactString(actual);
    }

    private boolean formatCompactedComparison() {
        return expected == null || actual == null || areStringsEqual();
    }

    private String compactString(String source) {
       return computeCommonPrefix() +
                DELTA_START +
                source.substring(prefixLength, source.length() - suffixLength) +
                DELTA_END +
                computeCommonSuffix();

    }

    private void findCommonPrefixAmdSuffix() {
        findCommonPrefix();
        suffixLength = 0;

        for (; !suffixOverlapsPrefix(suffixLength); suffixLength++) {
            if (charFromEnd(expected, suffixLength) !=
                   charFromEnd(actual, suffixLength)) {
                break;
            }
        }
    }

    private char charFromEnd(String s, int i) {
        return s.charAt(s.length() - i - 1);
    }

    private boolean suffixOverlapsPrefix(int suffixLength) {
        return actual.length() - suffixLength <= prefixLength ||
                expected.length() - suffixLength <= prefixLength;
    }

    private void findCommonPrefix() {
        int prefixIndex = 0;
        int end = Math.min(expected.length(), actual.length());
        for (; prefixIndex < end; prefixIndex++) {
            if (expected.charAt(prefixIndex) != actual.charAt(prefixIndex)) {
                break;
            }
        }
    }

    private String computeCommonPrefix() {
        return (prefixLength > contextLength ? ELLIPSIS : "") + expected.substring(Math.max(0, prefixLength - contextLength), prefixLength);
    }

    private String computeCommonSuffix() {
        int end = Math.min(expected.length() - suffixLength + contextLength, expected.length());

        return expected.substring(expected.length() - suffixLength, end) +
                (expected.length() - suffixLength < expected.length() - contextLength ?
                        ELLIPSIS : "");
    }

    private boolean areStringsEqual() {
        return expected.equals(actual);
    }

}
