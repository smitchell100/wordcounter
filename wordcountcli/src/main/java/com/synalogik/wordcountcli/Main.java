package com.synalogik.wordcountcli;

import com.synalogik.wordcountcore.rendering.OutputStreamMetricsRenderer;
import com.synalogik.wordcountcore.*;
import com.synalogik.wordcountcore.model.WordCountMetrics;
import com.synalogik.wordcountcore.rendering.MetricsRenderer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import java.net.URI;
import java.util.concurrent.Callable;

/**
 * Provides a command line interface.
 * Picocli library was used to simplify command line handling.
 */
@Command(description = "Displays word count metrics for a given text source.",
        name = "wordMetrics", mixinStandardHelpOptions = true)
public class Main implements Callable<Integer> {

    @Parameters(index = "0", arity = "1", description = "The path to the text source.")
    private URI pathToTextSource;

    public static void main(String... args) throws Exception {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    public Integer call() throws Exception {
        final WordCountMetrics metrics = new BufferedWordCounter().analyseText(pathToTextSource);

        try {
            final MetricsRenderer renderer = new OutputStreamMetricsRenderer(System.out);
            renderer.renderMetrics(metrics);
        } catch ( Exception e ) {
            System.out.println("Failed. " + e.getMessage());
            return -1;
        }

        return 0;
    }

}