package com.synalogik.wordcountcli;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 * A simple integration test, proving that the various parts work together and are callable from Main.
 *
 * TODO : more testing could be applied to cover off all of the command line options
 */
public class MainTest {


    @Rule
    public final ExpectedSystemExit exitRule = ExpectedSystemExit.none();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();


    @Test
    public void simpleIntegrationTest() throws URISyntaxException {

        String pathToTextSource = getClass().getClassLoader().getResource("singleLine.txt").toURI().toString();
        String[] args = new String[] {pathToTextSource};

        exitRule.expectSystemExitWithStatus(0);

        try {
            Main.main(args);
        } catch (Exception e) {
            // ignore
        }

        String expectedStdOut =
                "Word count = 9" +
                        "Average word length = 4.556" +
                        "Number of words of length 1 is 1" +
                        "Number of words of length 2 is 1" +
                        "Number of words of length 3 is 1" +
                        "Number of words of length 4 is 2" +
                        "Number of words of length 5 is 2" +
                        "Number of words of length 7 is 1" +
                        "Number of words of length 10 is 1" +
                        "The most frequently occurring word length is 2, for word lengths of 4 & 5";

        String actualStdOut = systemOutRule.getLog().replaceAll("[\\n\\r]", "");

        assertEquals(expectedStdOut, actualStdOut);

    }


}