package com.synalogik.wordcountcore;

import com.synalogik.wordcountcore.exception.WordCounterException;
import com.synalogik.wordcountcore.model.WordCountMetrics;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * A multi-threaded implementation of a Word Counter.
 *
 * Follows a producer - consumer pattern.
 * The main thread iterates line by line through the text, adding each line of text into a work queue.
 * A pool of threads then consumes text lines from the work queue; each line is split in to words with the word lengths registered in a [thread safe] WordCountMetrics object.
 * The WordCountMetrics object is then returned.
 *
 * @see WordCounter
 */
public class MultiThreadedWordCounter implements WordCounter {


    private final int numberOfWorkerThreads;
    private final int workerQueueSize;

    private final ExecutorService executorService;

    private final List<Future> workerFutures = new ArrayList<>();

    /*
    The work queue. Contains lines of text for processing by the worker threads.
    Lines of text are added to the queue by the main thread and worker threads consume them.
    */
    private final ArrayBlockingQueue<String> workQueue;

    /*
    A flag used to communicate to the worker threads that the main thread has completed its iteration through the text.
    When this is true, the workers can terminate when the work queue is empty.
    */
    private AtomicBoolean hasFinishedReadingFile = new AtomicBoolean();


    public MultiThreadedWordCounter(int numberOfWorkerThreads, int workerQueueSize) {
        workQueue = new ArrayBlockingQueue<>(workerQueueSize);
        executorService = Executors.newFixedThreadPool(numberOfWorkerThreads);
        this.numberOfWorkerThreads = numberOfWorkerThreads;
        this.workerQueueSize = workerQueueSize;
    }


    /**
     * constructor defaults number of threads and queue size
     */
    public MultiThreadedWordCounter() {
        this(3, 100);
    }


    /**
     * Analyse text sourced from the given URI path
     * @param pathToSource URI of text to process
     * @return WordCountMetrics of the full text analysis
     * @see WordCountMetrics
     */
    @Override
    public WordCountMetrics analyseText(URI pathToSource) {

        if (pathToSource != null) {

            return processText(pathToSource);

        } else {
            throw new WordCounterException("Invalid pathToSource; must be non-null");
        }

    }


    private WordCountMetrics processText(URI pathToSource) {

        hasFinishedReadingFile.set(false);

        WordCountMetrics metrics = new WordCountMetrics();

        startWorkers(metrics, numberOfWorkerThreads);


        try (Scanner scanner = new Scanner(pathToSource.toURL().openStream(), StandardCharsets.UTF_8.toString())) {

            while (scanner.hasNextLine()) {
                workQueue.put(scanner.nextLine());
            }

            hasFinishedReadingFile.set(true);

            waitForWorkersToComplete();

            executorService.shutdown();

        } catch (Exception e) {
            throw new WordCounterException(format("Failed to analyse given uri [%s]", pathToSource), e);
        }

        return metrics;

    }


    private void waitForWorkersToComplete() {
        workerFutures.forEach(
                next -> {
                    try {
                        next.get();
                    } catch (InterruptedException | ExecutionException e) {
                        // ignore
                    }
                }
        );
    }


    private void startWorkers(WordCountMetrics metrics, int numberOfWorkerThreads) {

        for (int i = 0; i < numberOfWorkerThreads; i++) {

            workerFutures.add(executorService.submit(() -> {

                while (true) {

                    String lineOfTextToAnalyse = takeTextLineFromWorkQueue();

                    if (lineOfTextToAnalyse == null && hasFinishedReadingFile.get()) {
                        // no more lines of text in the queue, and file read has been flagged as complete, so close down this worker
                        return;
                    }

                    analyseLineOfText(metrics, lineOfTextToAnalyse);

                }

            }));

        }

    }


    private String takeTextLineFromWorkQueue() {

        String lineOfTextToAnalyse = null;
        try {
            lineOfTextToAnalyse = workQueue.poll(500, MILLISECONDS);
        } catch (InterruptedException e) {
            // ignore; poll timed out
        }
        return lineOfTextToAnalyse;
    }


}