package com.synalogik.wordcountcli;

import com.synalogik.wordcountcore.MultiThreadedWordCounter;
import com.synalogik.wordcountcore.SingleThreadedWordCounter;
import com.synalogik.wordcountcore.WordCounter;
import com.synalogik.wordcountcore.model.WordCountMetrics;
import com.synalogik.wordcountcore.rendering.MetricsRenderer;
import com.synalogik.wordcountcore.rendering.WriterMetricsRenderer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.StringWriter;
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


    @Option(names = {"-t", "--threads"}, defaultValue = "1", description = "The number of worker threads used when analysing files (default is 1).")
    private int numberOfWorkerThreads;


    @Option(names = {"-q", "--queueSize"}, defaultValue = "100", description = "(Only applies when number of worker threads > 1).\nThe worker queue size used when streaming the given file (default is 100).")
    private int workerQueueSize;


    public static void main(String... args) throws Exception {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }


    public Integer call() throws Exception {

        WordCounter wordCounter;

        if ( numberOfWorkerThreads > 1 ) {
            wordCounter = new MultiThreadedWordCounter(numberOfWorkerThreads, workerQueueSize);
        } else {
            wordCounter = new SingleThreadedWordCounter();
        }

        WordCountMetrics metrics = wordCounter.analyseText(pathToTextSource);

        try {
            StringWriter sw = new StringWriter();
            MetricsRenderer renderer = new WriterMetricsRenderer(sw);
            renderer.renderMetrics(metrics);
            System.out.println(sw.toString());
        } catch ( Exception e ) {
            System.out.println("Failed. " + e.getMessage());
            return -1;
        }

        return 0;
    }

}