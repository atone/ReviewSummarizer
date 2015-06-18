package summarizer.summarizer;

import summarizer.model.Phrase;

import java.util.Set;

/**
 * Created by atone on 15/4/28.
 * This is the summarizer interface.
 */
public interface Summarizer {
    // this abstract method should return the summarize result,
    // i.e., the set of the chosen phrases.
    Set<Phrase> summarize();
}
