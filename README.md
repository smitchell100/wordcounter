## Implementation Notes

1. Java8 was assumed
2. I wanted to include a command line interface
3. The maven project is split into 2 modules (separation of concerns):
    1. wordcountcore : The core of the implementation; programmers API.
    2. wordcountcli : Command line interface.
4. The word counter reads from a given URI. This allows it to support a variety of sources such as web addresses and local file system.
5. Logging omitted for brevity, but would be added in real world setting.
6. In real world setting the POM would be configured for deployment to repository.
7. I did consider using SpringBoot, but instead opted for plain Java. SpringBoot didn't seem to add much in this example.
8. A buffered reader with parallel stream is used for scalability (reading a whole file in to memory / single threading would not scale).
9. A regex is used to split lines of text into words. I'm happy the regex works, but might like to explore if there's a way to simplify it. Also, I might consider storing it in a properties file.
10. I have introduced some abstractions, which are probably overkill for this when there's only 1 version of a word counter / renderer. It was more to show that coding to interfaces is good practise.

### What defines a word?

A word is any number of characters that appear between word delimiters.
I used the following characters as word delimiters:
 - space
 - exclamation
 - question mark
 - colon
 - semi-colon
 - comma
 - period

Note: Commas and periods are a special case. They shouldn't be considered as delimiters if they appear within a formatted number. E.g. "3,500.75" should be treated as a single word, and not split into 3 words ["3", "500", "75""]. However, a comma or period appearing at the beginning or end of a number should be treated as a delimiter; e.g. ",3,500.75." should yield one word of "3,500.75" and not ",3,500.75.".

More refinements could be made. For example, the removal of speech marks and other characters which might be incorrectly included within words.

### Installation Instructions

1. Clone the repository https://github.com/smitchell100/wordcounter.git
2. Run the following command from the project root folder: `mvn package`

### Programming API

To use the word counter API, add the following dependency to your pom:

```
<dependency>
  <groupId>com.synalogik</groupId>
  <artifactId>wordcount-core</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

#### example code

```
import com.synalogik.wordcountcore.WordCounter;
import com.synalogik.wordcountcore.BufferedWordCounter;
import com.synalogik.wordcountcore.model.WordCountMetrics;
import java.net.URI;
.
.
.
URI pathToTextSource = new URI("https://janelwashere.com/files/bible_daily.txt");
WordCounter wordCounter = new BufferedWordCounter();
WordCountMetrics wordCountMetrics = wordCounter.analyseText(pathToTextSource);
```

Because the WordCounter interface takes a URI path to the text file, local file system files can also be used in addition to the web url example given. E.g. ```URI pathToTextSource = new URI("file:///path/to/file/filename.txt");```

The example returns a **WordCountMetrics** object, offering the following methods from which word count metrics can be retrieved:

| Method | Description |
| ----------- | ----------- |
| boolean **hasData()** | Was any data captured? |
| Double **getAverageWordLength()** | The average word length across the whole text |
| int **getTotalNumberOfCharactersInWords()** | Total number of characters in all words scanned |
| int **getTotalWordCount()** | Total number of words in whole text |
| int **getHighestFrequencyOfAWordLength()** | Which word length occured most in whole text |
| List<Integer> **getWordLengthsHavingFrequencyOf(int frequency)** | Which word lengths occurred a given number of times |
| Map<Integer, Integer> **getMapFrequencyOfWordsGroupedByWordLength()** | Get (immutable) map of word lengths to frequency |


#### Rendering the results

The api also provides a renderer allowing a WordCountMetrics object to be output to an OutputStream, e.g. to write the metrics to System.out:

```
import com.synalogik.wordcountcore.rendering.OutputStreamMetricsRenderer;
.
.
.
new OutputStreamMetricsRenderer(System.out).renderMetrics(wordCountMetrics);
```


### Command Line Interface

A command line interface is provided, which can be run from the project root folder as follows:

```java -jar wordcountcli\target\wordcount-cli-1.0-SNAPSHOT-jar-with-dependencies.jar```

#### Example usage against a web hosted text file

```java -jar wordcountcli\target\wordcount-cli-1.0-SNAPSHOT-jar-with-dependencies.jar https://janelwashere.com/files/bible_daily.txt```


#### Example usage against a local text file

```java -jar wordcountcli\target\wordcount-cli-1.0-SNAPSHOT-jar-with-dependencies.jar file:///path/to/file/filename.txt```
