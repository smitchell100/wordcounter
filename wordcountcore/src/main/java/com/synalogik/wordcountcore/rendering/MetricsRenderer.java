package com.synalogik.wordcountcore.rendering;

import com.synalogik.wordcountcore.model.WordCountMetrics;

/**
 * Abstraction of a Renderer; responsible for displaying Word Count Metrics (results from running a word count analysis)
 * @see WordCountMetrics
 */
public interface MetricsRenderer {
    void renderMetrics(WordCountMetrics wordCountMetrics);
}