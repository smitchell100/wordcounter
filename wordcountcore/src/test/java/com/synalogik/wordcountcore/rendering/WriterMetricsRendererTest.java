package com.synalogik.wordcountcore.rendering;

import com.synalogik.wordcountcore.model.WordCountMetrics;
import org.junit.Test;

import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class WriterMetricsRendererTest {


    @Test
    public void nullWordCountMetricsShouldNotErrorButIncludeSuitableMessage() {

        StringWriter sw = new StringWriter();
        WriterMetricsRenderer writerMetricsRenderer = new WriterMetricsRenderer(sw);

        writerMetricsRenderer.renderMetrics(null);

        assertThat(sw.toString(), is ("Word count metrics are unavailable.\n"));

    }


    @Test
    public void emptyWordCountMetricsShouldNotErrorButIncludeSuitableMessage() {

        StringWriter sw = new StringWriter();
        WriterMetricsRenderer writerMetricsRenderer = new WriterMetricsRenderer(sw);

        writerMetricsRenderer.renderMetrics(new WordCountMetrics());

        assertThat(sw.toString(), is ("Word count metrics are unavailable.\n"));

    }


    @Test
    public void correctSummaryForSingleWord() {

        // given

        StringWriter sw = new StringWriter();
        WriterMetricsRenderer writerMetricsRenderer = new WriterMetricsRenderer(sw);

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        wordCountMetrics.registerWordOccurenceOfLength(10);


        // when

        writerMetricsRenderer.renderMetrics(wordCountMetrics);


        // then

        String expectedSummary =
                "Word count = 1" +
                "Average word length = 10" +
                "Number of words of length 10 is 1" +
                "The most frequently occurring word length is 1, for word lengths of 10";
        String actualSummary = sw.toString().replaceAll("[\\n\\r]", "");

        assertThat(actualSummary, equalTo (expectedSummary));

    }


    @Test
    public void correctSummaryForMultipleWords() {

        // given

        StringWriter sw = new StringWriter();
        WriterMetricsRenderer writerMetricsRenderer = new WriterMetricsRenderer(sw);

        WordCountMetrics wordCountMetrics = new WordCountMetrics();

        wordCountMetrics.registerWordOccurenceOfLength(10);
        wordCountMetrics.registerWordOccurenceOfLength(5);
        wordCountMetrics.registerWordOccurenceOfLength(10);
        wordCountMetrics.registerWordOccurenceOfLength(5);
        wordCountMetrics.registerWordOccurenceOfLength(4);


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
        String actualSummary = sw.toString().replaceAll("[\\n\\r]", "");

        assertThat(actualSummary, equalTo (expectedSummary));

    }






}