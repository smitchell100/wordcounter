package com.synalogik.wordcountcore.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;


/**
 * Testing that the WordCountMetrics class behaves as expected.
 * Testing that the class is thread safe, and that the methods that return the metrics are accurate.
 */
public class WordCountMetricsTest {


    @Test
    public void ensureReadOnlyMapIsReturned() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();
        Map<Integer, Integer> mapFrequencyOfWordsGroupedByWordLength = wordCountMetrics.getMapFrequencyOfWordsGroupedByWordLength();
        try {
            mapFrequencyOfWordsGroupedByWordLength.put(100, 10);
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }

    }


    @Test
    public void allMetricsArePresentInReturnedMap() {

        WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();
        Map<Integer, Integer> mapFrequencyOfWordsGroupedByWordLength = wordCountMetrics.getMapFrequencyOfWordsGroupedByWordLength();
        assertThat(mapFrequencyOfWordsGroupedByWordLength.keySet(), containsInAnyOrder(10, 4, 9, 7));
        assertThat(mapFrequencyOfWordsGroupedByWordLength.get(10), is(2));
        assertThat(mapFrequencyOfWordsGroupedByWordLength.get(4), is(1));
        assertThat(mapFrequencyOfWordsGroupedByWordLength.get(9), is(1));
        assertThat(mapFrequencyOfWordsGroupedByWordLength.get(7), is(1));

    }


    @Test
    public void noMetricsArePresentInReturnedMapForNewlyCreatedObject() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();
        Map<Integer, Integer> mapFrequencyOfWordsGroupedByWordLength = wordCountMetrics.getMapFrequencyOfWordsGroupedByWordLength();
        assertThat(mapFrequencyOfWordsGroupedByWordLength.keySet(), empty());

    }


    @Test
    /**
     * Call registerWordOccurenceOfLength 10,000 times on 10 threads.
     * Tests that the total word count is 100,000 proving that we have not dropped any values due to thread safety issues.
     */
    public void testForThreadSafetyOfWrites() {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        List<Future> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            futures.add(executorService.submit(() ->
            {
                for (int a = 0; a < 10000; a++) {
                    wordCountMetrics.registerWordOccurenceOfLength(10);
                }
            }));
        }

        futures.forEach(nextFuture -> {
            try {
                nextFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                // ignore
            }
        });

        executorService.shutdown();

        assertThat(wordCountMetrics.getTotalWordCount(), is(100000));

    }


    @Test
    public void hasDataShouldBeFalseForNewlyConstructedObject() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        assertThat(wordCountMetrics.hasData(), is(false));

    }


    @Test
    public void hasDataShouldBeTrueAfterWordLengthIsRegistered() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        wordCountMetrics.registerWordOccurenceOfLength(10);

        assertThat(wordCountMetrics.hasData(), is(true));

    }


    @Test
    public void averageWordLengthShouldBeZeroForNewlyConstructedObject() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        assertThat(wordCountMetrics.getAverageWordLength().doubleValue(), is(0d));

    }


    @Test
    public void averageWordLengthShouldBeCorrect() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        wordCountMetrics.registerWordOccurenceOfLength(10);
        wordCountMetrics.registerWordOccurenceOfLength(10);
        wordCountMetrics.registerWordOccurenceOfLength(4);

        assertThat(wordCountMetrics.getAverageWordLength().doubleValue(), is(8d));

    }


    @Test
    public void totalNumberOfCharactersInWordsShouldBeZeroForNewlyConstructedObject() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        assertThat(wordCountMetrics.getTotalNumberOfCharactersInWords(), is(0));

    }


    @Test
    public void totalNumberOfCharactersInWordsShouldBeCorrect() {

        WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();

        assertThat(wordCountMetrics.getTotalNumberOfCharactersInWords(), is(40));

    }


    @Test
    public void totalWordCountShouldBeZeroForNewlyConstructedObject() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        assertThat(wordCountMetrics.getTotalWordCount(), is(0));

    }


    @Test
    public void totalWordCountShouldBeCorrect() {

        WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();

        assertThat(wordCountMetrics.getTotalWordCount(), is(5));

    }


    @Test
    public void highestFrequencyOfAWordLengthShouldBeZeroForANewlyConstructedObject() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        assertThat( wordCountMetrics.getHighestFrequencyOfAWordLength().isPresent(), is(false) );

    }


    @Test
    public void highestFrequencyOfAWordLengthShouldBeCorrect() {

        WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();

        assertThat( wordCountMetrics.getHighestFrequencyOfAWordLength().isPresent(), is(true) );
        assertThat( wordCountMetrics.getHighestFrequencyOfAWordLength().getAsInt(), is(2) );

    }


    @Test
    public void wordLengthsHavingFrequencyOfShouldBeEmptyForNonMatch() {

        WordCountMetrics wordCountMetrics = prePopulatedWordCountMetrics();

        assertThat( wordCountMetrics.getWordLengthsHavingFrequencyOf(5), is(empty()) );

    }


    @Test
    public void wordLengthsHavingFrequencyOfShouldBeCorrect() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        wordCountMetrics.registerWordOccurenceOfLength(10);
        wordCountMetrics.registerWordOccurenceOfLength(10);
        wordCountMetrics.registerWordOccurenceOfLength(4);
        wordCountMetrics.registerWordOccurenceOfLength(9);
        wordCountMetrics.registerWordOccurenceOfLength(7);
        wordCountMetrics.registerWordOccurenceOfLength(7);

        assertThat( wordCountMetrics.getWordLengthsHavingFrequencyOf(2), containsInAnyOrder(10, 7) );

    }


    private WordCountMetrics prePopulatedWordCountMetrics() {

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        wordCountMetrics.registerWordOccurenceOfLength(10);
        wordCountMetrics.registerWordOccurenceOfLength(10);
        wordCountMetrics.registerWordOccurenceOfLength(4);
        wordCountMetrics.registerWordOccurenceOfLength(9);
        wordCountMetrics.registerWordOccurenceOfLength(7);

        return wordCountMetrics;

    }


}