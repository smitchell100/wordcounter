package com.synalogik.wordcountcore;

import com.synalogik.wordcountcore.exception.WordCounterException;
import com.synalogik.wordcountcore.model.WordCountMetrics;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static java.lang.String.format;

/**
 * A single threaded implementation of a WordCounter.
 *
 * @see WordCounter
 */
public class SingleThreadedWordCounter implements WordCounter {


    /**
     * Analyse text sourced from the given URI path
     * @param pathToSource URI of text to process
     * @return WordCountMetrics of the full text analysis
     * @see WordCountMetrics
     */
    @Override
    public WordCountMetrics analyseText(URI pathToSource) {

        if ( pathToSource != null ) {

            return processText(pathToSource);
        }
        else {
            throw new WordCounterException("Invalid pathToSource; must be non-null");
        }

    }


    private WordCountMetrics processText(URI pathToSource) {

        WordCountMetrics metrics = new WordCountMetrics();

        try (Scanner scanner = new Scanner(pathToSource.toURL().openStream(), StandardCharsets.UTF_8.toString())) {

            while (scanner.hasNextLine()) {
                analyseLineOfText(metrics, scanner.nextLine());
            }

        } catch (Exception e) {
            throw new WordCounterException(format("Failed to analyse given uri [%s]", pathToSource), e);
        }

        return metrics;

    }


}