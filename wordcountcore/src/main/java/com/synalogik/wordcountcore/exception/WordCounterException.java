package com.synalogik.wordcountcore.exception;

/**
 * A general exception thrown by the WordCounter API.
 */
public class WordCounterException extends RuntimeException {
    public WordCounterException(String message, Throwable cause) {
        super(message, cause);
    }
    public WordCounterException(String message) {
        super(message);
    }
}
