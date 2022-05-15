package com.synalogik.wordcountcli.rendering;

import com.synalogik.wordcountcore.model.WordCountMetrics;
import com.synalogik.wordcountcore.rendering.OutputStreamMetricsRenderer;
import org.junit.Test;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class OutputStreamMetricsRendererTest {

    @Test
    public void nullWordCountMetricsShouldNotErrorButIncludeSuitableMessage() {
        //given
        final OutputStream sw = new ByteArrayOutputStream();
        final OutputStreamMetricsRenderer writerMetricsRenderer = new OutputStreamMetricsRenderer(sw);

        // when
        writerMetricsRenderer.renderMetrics(null);

        // then
        assertThat(sw.toString(), is ("Word count metrics are unavailable.\n"));
    }

    @Test
    public void emptyWordCountMetricsShouldNotErrorButIncludeSuitableMessage() {
        // given
        final OutputStream sw = new ByteArrayOutputStream();
        final OutputStreamMetricsRenderer writerMetricsRenderer = new OutputStreamMetricsRenderer(sw);

        // when
        writerMetricsRenderer.renderMetrics(new WordCountMetrics());

        // then
        assertThat(sw.toString(), is ("Word count metrics are unavailable.\n"));
    }

    @Test
    public void correctSummaryForSingleWord() {
        // given
        final OutputStream sw = new ByteArrayOutputStream();
        final OutputStreamMetricsRenderer writerMetricsRenderer = new OutputStreamMetricsRenderer(sw);
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        wordCountMetrics.registerWordOccurrenceOfLength(10);

        // when
        writerMetricsRenderer.renderMetrics(wordCountMetrics);

        // then
        final String expectedSummary =
                "Word count = 1" +
                "Average word length = 10" +
                "Number of words of length 10 is 1" +
                "The most frequently occurring word length is 1, for word lengths of 10";
        final String actualSummary = sw.toString().replaceAll("[\\n\\r]", "");
        assertThat(actualSummary, equalTo (expectedSummary));
    }

    @Test
    public void correctSummaryForMultipleWords() {
        // given
        final OutputStream sw = new ByteArrayOutputStream();
        final OutputStreamMetricsRenderer writerMetricsRenderer = new OutputStreamMetricsRenderer(sw);
        final WordCountMetrics wordCountMetrics = new WordCountMetrics();
        wordCountMetrics.registerWordOccurrenceOfLength(10);
        wordCountMetrics.registerWordOccurrenceOfLength(5);
        wordCountMetrics.registerWordOccurrenceOfLength(10);
        wordCountMetrics.registerWordOccurrenceOfLength(5);
        wordCountMetrics.registerWordOccurrenceOfLength(4);

        // when
        writerMetricsRenderer.renderMetrics(wordCountMetrics);

        // then
        String expectedSummary =
                "Word count = 5" +
                "Average word length = 6.8" +
                "Number of words of length 4 is 1" +
                "Number of words of length 5 is 2" +
                "Number of words of length 10 is 2" +
                "The most frequently occurring word length is 2, for word lengths of 5 & 10";
        final String actualSummary = sw.toString().replaceAll("[\\n\\r]", "");
        assertThat(actualSummary, equalTo (expectedSummary));
    }

}