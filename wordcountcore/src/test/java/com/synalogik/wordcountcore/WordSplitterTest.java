package com.synalogik.wordcountcore;

import org.junit.Test;
import java.util.List;

import static com.synalogik.wordcountcore.WordSplitter.wordsFromString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;

/**
 * Tests that the word splitting rules are correctly applied.
 */
public class WordSplitterTest {

    @Test
    public void exampleStringGivenInSpec() {
        List<String> words = wordsFromString("Hello world & good morning. The date is 18/05/2016");
        assertThat( words, containsInAnyOrder("Hello", "world", "&", "good", "morning", "The", "date", "is", "18/05/2016"));
    }

    @Test
    public void nullStringReturnsEmptyList() {
        List<String> words = wordsFromString(null);
        assertThat( words, empty());
    }

    @Test
    public void emptyStringReturnsEmptyList() {
        List<String> words = wordsFromString(null);
        assertThat( words, empty());
    }

    @Test
    public void formattedNumericValueIsNotSplit() {
        List<String> words = wordsFromString("350,000.56");
        assertThat(words, containsInAnyOrder("350,000.56"));
    }

    @Test
    public void commaFollowingNonDigitSplitsOnComma() {
        List<String> words = wordsFromString("NonNumericOne,NonNumericTwo");
        assertThat(words, containsInAnyOrder("NonNumericOne", "NonNumericTwo"));
    }

    @Test
    public void commaBetweenDigitAndNonDigitSplitsOnComma() {
        List<String> words = wordsFromString("35,NonNumeric");
        assertThat(words, containsInAnyOrder("35", "NonNumeric"));
    }

    @Test
    public void periodBetweenDigitAndNonDigitSplitsOnPeriod() {
        List<String> words = wordsFromString("35.NonNumeric");
        assertThat(words, containsInAnyOrder("35", "NonNumeric"));
    }

    @Test
    public void periodBetweenTwoNonDigitsSplitsOnPeriod() {
        List<String> words = wordsFromString("NonNumericOne.NonNumericTwo");
        assertThat(words, containsInAnyOrder("NonNumericOne", "NonNumericTwo"));
    }

    @Test
    public void periodFollowingNonDigitSplitsOnPeriod() {
        List<String> words = wordsFromString("NonNumericOne.350");
        assertThat(words, containsInAnyOrder("NonNumericOne", "350"));
    }

    @Test
    public void commaAtEndOfStringFollowingADigitShouldNotBeIncludedInWord() {
        List<String> words = wordsFromString("350,");
        assertThat(words, containsInAnyOrder("350"));
    }

    @Test
    public void periodAtEndOfStringFollowingADigitShouldNotBeIncludedInWord() {
        List<String> words = wordsFromString("350.");
        assertThat(words, containsInAnyOrder("350"));
    }

    @Test
    public void commaAtStartOfStringShouldNotBeIncludedInWord() {
        List<String> words = wordsFromString(",Hello");
        assertThat(words, containsInAnyOrder("Hello"));
    }

    @Test
    public void periodAtStartOfStringShouldNotBeIncludedInWord() {
        List<String> words = wordsFromString(".Hello");
        assertThat(words, containsInAnyOrder("Hello"));
    }

    @Test
    public void correctlySplitsOnSpace() {
        List<String> words = wordsFromString("Hello World");
        assertThat(words, containsInAnyOrder("Hello", "World"));
    }

    @Test
    public void correctlySplitsOnExclamation() {
        List<String> words = wordsFromString("Hello!World");
        assertThat(words, containsInAnyOrder("Hello", "World"));
    }

    @Test
    public void correctlySplitsOnQuestionMark() {
        List<String> words = wordsFromString("Hello?World");
        assertThat(words, containsInAnyOrder("Hello", "World"));
    }

    @Test
    public void correctlySplitsOnQuestionColon() {
        List<String> words = wordsFromString("Hello:World");
        assertThat(words, containsInAnyOrder("Hello", "World"));
    }

    @Test
    public void correctlySplitsOnQuestionSemiColon() {
        List<String> words = wordsFromString("Hello;World");
        assertThat(words, containsInAnyOrder("Hello", "World"));
    }

}