package com.synalogik.wordcountcore.model;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;

/**
 * Testing that the WordCountMetrics class behaves as expected.
 * Testing that the class is thread safe, and that the methods that return the metrics are accurate.
 */
public class WordCountMetricsTest {

    @Test(expected = UnsupportedOperationException.class)
    public void ensureReadOnlyMapIsReturned() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        final Map<Integer, Integer> mapFrequencyOfWordsGroupedByWordLength = wordCountMetrics.getMapFrequencyOfWordsGroupedByWordLength();
        mapFrequencyOfWordsGroupedByWordLength.put(100, 10);
    }

    @Test
    public void allMetricsArePresentInReturnedMap() {
        final WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();
        final Map<Integer, Integer> mapFrequencyOfWordsGroupedByWordLength = wordCountMetrics.getMapFrequencyOfWordsGroupedByWordLength();
        assertThat(mapFrequencyOfWordsGroupedByWordLength.keySet(), containsInAnyOrder(10, 4, 9, 7));
        assertThat(mapFrequencyOfWordsGroupedByWordLength.get(10), is(2));
        assertThat(mapFrequencyOfWordsGroupedByWordLength.get(4), is(1));
        assertThat(mapFrequencyOfWordsGroupedByWordLength.get(9), is(1));
        assertThat(mapFrequencyOfWordsGroupedByWordLength.get(7), is(1));
    }

    @Test
    public void noMetricsArePresentInReturnedMapForNewlyCreatedObject() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        final Map<Integer, Integer> mapFrequencyOfWordsGroupedByWordLength = wordCountMetrics.getMapFrequencyOfWordsGroupedByWordLength();
        assertThat(mapFrequencyOfWordsGroupedByWordLength.keySet(), empty());
    }

    @Test
    /**
     * Call registerWordOccurenceOfLength 10,000 times on 10 threads.
     * Tests that the total word count is 100,000 proving that we have not dropped any values due to thread safety issues.
     */
    public void testForThreadSafetyOfWrites() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        IntStream.range(0, 100000).parallel()
            .forEach(next -> wordCountMetrics.registerWordOccurrenceOfLength(RandomUtils.nextInt(1, 20)));
        assertThat(wordCountMetrics.getTotalWordCount(), is(100000));
    }

    @Test
    public void hasDataShouldBeFalseForNewlyConstructedObject() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        assertThat(wordCountMetrics.hasData(), is(false));
    }

    @Test
    public void hasDataShouldBeTrueAfterWordLengthIsRegistered() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        wordCountMetrics.registerWordOccurrenceOfLength(10);
        assertThat(wordCountMetrics.hasData(), is(true));
    }

    @Test
    public void averageWordLengthShouldBeZeroForNewlyConstructedObject() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        assertThat(wordCountMetrics.getAverageWordLength().doubleValue(), is(0d));
    }

    @Test
    public void averageWordLengthShouldBeCorrect() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        wordCountMetrics.registerWordOccurrenceOfLength(10);
        wordCountMetrics.registerWordOccurrenceOfLength(10);
        wordCountMetrics.registerWordOccurrenceOfLength(4);
        assertThat(wordCountMetrics.getAverageWordLength().doubleValue(), is(8d));
    }

    @Test
    public void totalNumberOfCharactersInWordsShouldBeZeroForNewlyConstructedObject() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        assertThat(wordCountMetrics.getTotalNumberOfCharactersInWords(), is(0));
    }

    @Test
    public void totalNumberOfCharactersInWordsShouldBeCorrect() {
        final WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();
        assertThat(wordCountMetrics.getTotalNumberOfCharactersInWords(), is(40));
    }

    @Test
    public void totalWordCountShouldBeZeroForNewlyConstructedObject() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        assertThat(wordCountMetrics.getTotalWordCount(), is(0));
    }

    @Test
    public void totalWordCountShouldBeCorrect() {
        final WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();
        assertThat(wordCountMetrics.getTotalWordCount(), is(5));
    }

    @Test
    public void highestFrequencyOfAWordLengthShouldBeZeroForANewlyConstructedObject() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        assertThat( wordCountMetrics.getHighestFrequencyOfAWordLength(), is(0) );
    }

    @Test
    public void highestFrequencyOfAWordLengthShouldBeCorrect() {
        final WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();
        assertThat( wordCountMetrics.getHighestFrequencyOfAWordLength(), is(2) );
    }

    @Test
    public void wordLengthsHavingFrequencyOfShouldBeEmptyForNonMatch() {
        final WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();
        assertThat( wordCountMetrics.getWordLengthsHavingFrequencyOf(5), is(empty()) );
    }

    @Test
    public void wordLengthsHavingFrequencyOfShouldBeCorrect() {
        final WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();
        wordCountMetrics.registerWordOccurrenceOfLength(7);
        assertThat( wordCountMetrics.getWordLengthsHavingFrequencyOf(2), containsInAnyOrder(10, 7) );
    }

    private WordCountMetrics prePopulatedWordCountMetrics() {
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        wordCountMetrics.registerWordOccurrenceOfLength(10);
        wordCountMetrics.registerWordOccurrenceOfLength(10);
        wordCountMetrics.registerWordOccurrenceOfLength(4);
        wordCountMetrics.registerWordOccurrenceOfLength(9);
        wordCountMetrics.registerWordOccurrenceOfLength(7);
        return wordCountMetrics;
    }

}