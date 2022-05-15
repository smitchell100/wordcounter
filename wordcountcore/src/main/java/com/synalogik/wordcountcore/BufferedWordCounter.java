package com.synalogik.wordcountcore;

import com.synalogik.wordcountcore.exception.WordCounterException;
import com.synalogik.wordcountcore.model.WordCountMetrics;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import static java.lang.String.format;

/**
 * A buffered reader implementation of a WordCounter.
 *
 * @see WordCounter
 */
public class BufferedWordCounter implements WordCounter {
    
    /**
     * Analyse text sourced from the given URI path
     * @param pathToSource URI of text to process
     * @return WordCountMetrics of the full text analysis
     * @see WordCountMetrics
     */
    @Override
    public WordCountMetrics analyseText(final URI pathToSource) {
        if ( pathToSource == null ) {
            throw new WordCounterException("Invalid pathToSource; must be non-null");
        }
        return processText(pathToSource);
    }

    /**
     * Process given URI, analysing each line of text in parallel (for scalability)
     * @param pathToSource URI path to be analysed
     * @return WordCountMetrics relating to the given URI
     */
    private WordCountMetrics processText(final URI pathToSource) {

        final WordCountMetrics metrics = new WordCountMetrics();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(pathToSource.toURL().openStream()))) {
            br.lines().parallel().forEach(line -> analyseLineOfText(metrics, line));
        } catch (Exception e) {
            throw new WordCounterException(format("Failed to analyse given uri [%s]", pathToSource), e);
        }

        return metrics;
    }

}