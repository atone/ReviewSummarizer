package edu.tsinghua.rs.summarizer;

import edu.tsinghua.rs.data.Phrase;

import java.util.Set;

/**
 * Created by atone on 15/4/28.
 */
public interface Summarizer {
    public Set<Phrase> summarize();
}
