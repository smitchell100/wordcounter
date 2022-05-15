package com.synalogik.wordcountcore;

import com.synalogik.wordcountcore.exception.WordCounterException;
import com.synalogik.wordcountcore.model.WordCountMetrics;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class BufferedWordCounterImplTest {

    @Test
    public void correctlyProcessesSingleLineFile() throws URISyntaxException, MalformedURLException {
        final BufferedWordCounter wordCounter = new BufferedWordCounter();
        final WordCountMetrics wordCountMetrics = wordCounter.analyseText(getClass().getClassLoader().getResource("singleLine.txt").toURI());
        assertThat(wordCountMetrics.getTotalWordCount(), is (9) );
    }

    @Test
    public void nullUriFailsWithWordCounterException() {
        final WordCounter wordCounter = new BufferedWordCounter();
        try {
            wordCounter.analyseText(null);
            fail("expected WordCounterException");
        } catch ( WordCounterException e ) {
            assertThat(e.getMessage(), is("Invalid pathToSource; must be non-null"));
        }
    }

    @Test
    public void incorrectUriFailsWithWordCounterException() throws URISyntaxException {
        WordCounter wordCounter = new BufferedWordCounter();
        try {
            wordCounter.analyseText(new URI("file:///./file_does_not_exist.txt"));
            fail("expected WordCounterException");
        } catch ( WordCounterException e ) {
            assertThat(e.getMessage(), is("Failed to analyse given uri [file:///./file_does_not_exist.txt]"));
        }
    }

}