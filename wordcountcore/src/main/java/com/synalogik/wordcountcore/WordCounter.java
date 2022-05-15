package com.synalogik.wordcountcore;

import com.synalogik.wordcountcore.model.WordCountMetrics;
import java.net.URI;

import static com.synalogik.wordcountcore.WordSplitter.wordsFromString;

/**
 * Abstraction of a word counter.
 */
public interface WordCounter {

    /**
     * A WordCountMetrics object should be returned containing metrics about the words counted, for a given text file URI.
     */
    WordCountMetrics analyseText(URI pathToSource);

    /**
     * Default logic for analysing a String, i.e. splitting out words, and for each word recording word length details in the given {@link WordCountMetrics}
     * @param metrics WordCountMetrics object with which to track word lengths.
     * @param lineOfText String to analyse
     */
    default void analyseLineOfText(WordCountMetrics metrics, String lineOfText) {
        if ( lineOfText != null ) {
            wordsFromString(lineOfText)
                    .forEach(word -> metrics.registerWordOccurrenceOfLength(word.length()));
        }
    }

}