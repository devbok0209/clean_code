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
    private int prefix;
    private int suffix;

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
        // 압축만 수행
        int prefixIndex = findCommonPrefix();
        int suffixIndex = findCommonSuffix(prefixIndex);
        compactExpected = compactString(expected);
        compactActual = compactString(actual);
    }

    private boolean formatCompactedComparison() {
        // 형식을 맞추기만 하는 작업
        return expected == null || actual == null || areStringsEqual();
    }

    private String compactString(String source) {
        String result = DELTA_START +
                source.substring(prefix, source.length() - suffix +1) + DELTA_END;

        if (prefix > 0) {
            result = computeCommonPrefix() + result;
        }

        if (suffix > 0) {
            result = result + computeCommonSuffix();
        }

        return result;
    }

    private int findCommonPrefix() {
        int prefixIndex = 0;
        int end = Math.min(expected.length(), actual.length());
        for (; prefixIndex < end; prefixIndex++) {
            if (expected.charAt(prefixIndex) != actual.charAt(prefixIndex)) {
                break;
            }
        }
        return prefixIndex;
    }

    private int findCommonSuffix(int prefixIndex) {
        int expectedSuffix = expected.length() - 1;
        int actualSuffix = actual.length() - 1;

        for (; actualSuffix >= prefixIndex && expectedSuffix >= prefixIndex;
        actualSuffix--, expectedSuffix--) {
            if (expected.charAt(expectedSuffix) != actual.charAt(actualSuffix)) {
                break;
            }
        }
        return expected.length() - expectedSuffix;
    }

    private String computeCommonPrefix() {
        return (fPrefix > fContextLength ? ELLIPSIS : "") + fExpected.substring(Math.max(0, fPrefix - fContextLength), fPrefix);
    }

    private String computeCommonSuffix() {
        int end = Math.min(fExpected.length() - fSuffix + 1 + fContextLength, fExpected.length());

        return fExpected.substring(fExpected.length() - fSuffix + 1, end) +
                (fExpected.length() - fSuffix + 1 < fExpected.length() - fContextLength ? ELLIPSIS : "");
    }

    private boolean areStringsEqual() {
        return fExpected.equals(fActual);
    }

}
