package com.synalogik.wordcountcore;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Responsible for splitting a given line of text into its constituent words.
 */
public class WordSplitter {

    /*
      Regex is used to split a sentence in to words, where the following characters are considered word delimiters:
        - space
        - exclamation
        - question mark
        - colon
        - semi-colon
        - comma
        - period

      Note: Commas and periods should not be considered as delimiters if they appear within a formatted number.  E.g.  3,500.75  would be treated as one word, and not split in to 3 words ["3", "500", "75"]
      However, a comma or a delimiter appearing directly before or after a number will be treated as a delimiter. E.g. ,3.500.75. would yield one word of  "3.500.75"

      TODO : consider storing this in a configuration file
     */
    public static final String REGEX_WORD_DELIMITERS = "[\\s!?:;]|(?<=\\D),|(?<=\\d),(?=\\D)|(?<=\\d)\\.(?=\\D)|(?<=\\D)\\.(?=\\D)|(?<=\\D)\\.|(?<=\\d),(?=$)|(?<=\\d)\\.(?=$)|(?<=^)\\.|(?<=^),";

    public static List<String> wordsFromString(final String textLine) {
        final List<String> words = new ArrayList<>();

        if (!isBlank(textLine)) {
            words.addAll(asList(splitString(textLine)));
        }

        words.removeIf(StringUtils::isEmpty);
        return words;
    }

    private static String[] splitString(final String textLine) {
        return textLine.split(REGEX_WORD_DELIMITERS);
    }

}