package service;

public interface Analyzer {

    /**
     * @return coefficient by given text.
     */
    Float getCoeffFromCommentOrPost(String text);
}
