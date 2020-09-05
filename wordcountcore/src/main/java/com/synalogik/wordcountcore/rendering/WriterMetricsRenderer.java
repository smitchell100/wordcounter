package com.synalogik.wordcountcore.rendering;

import com.synalogik.wordcountcore.exception.WordCounterException;
import com.synalogik.wordcountcore.model.WordCountMetrics;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 * A renderer that prints gathered metrics to a Writer.
 */
public class WriterMetricsRenderer implements MetricsRenderer {


    private final Writer writer;


    public WriterMetricsRenderer(Writer writer) {
        this.writer = writer;
    }


    @Override
    public void renderMetrics(WordCountMetrics wordCountMetrics) {

        StringBuilder sb = new StringBuilder();

        if ( wordCountMetrics != null && wordCountMetrics.hasData() ) {

            generateSummary(wordCountMetrics, sb);

        }
        else {
            sb.append("Word count metrics are unavailable.\n");
        }

        try {
            writer.write(sb.toString());
            writer.flush();
        } catch (IOException e) {
            throw new WordCounterException("Exception occured when writing summary", e);
        }

    }


    private void generateSummary(WordCountMetrics wordCountMetrics, StringBuilder sb) {

        sb.append(format("Word count = %d\n", wordCountMetrics.getTotalWordCount()));
        sb.append(new DecimalFormat("Average word length = #.###\n").format(wordCountMetrics.getAverageWordLength()));

        Map<Integer, Integer> countsByLength = wordCountMetrics.getMapFrequencyOfWordsGroupedByWordLength();

        countsByLength.keySet()
                .stream()
                .sorted()
                .forEach(wordLength -> sb.append(format("Number of words of length %d is %d\n", wordLength, countsByLength.get(wordLength))));

        int highestFrequencyOfAWordLength = wordCountMetrics.getHighestFrequencyOfAWordLength().getAsInt();

        sb.append(
                format("The most frequently occurring word length is %d, for word lengths of %s\n",
                        highestFrequencyOfAWordLength,
                        wordLengthsHavingHighestFrequency(wordCountMetrics, highestFrequencyOfAWordLength)));

    }


    private String wordLengthsHavingHighestFrequency(WordCountMetrics wordCountMetrics, int highestFrequencyOfAWordLength) {

        return wordCountMetrics.getWordLengthsHavingFrequencyOf(highestFrequencyOfAWordLength)
                .stream()
                .map(String::valueOf)
                .collect(joining(" & "));

    }


}