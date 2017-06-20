package service.impl;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import service.Analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AnalyzerImpl implements Analyzer {

    @Override
    public Float getCoeffFromCommentOrPost(String text) {
        InputStream inputStream = AnalyzerImpl.class.getClassLoader().getResourceAsStream("dictionary.txt");
        try {
            return matchCommentAgainstDictionary(text, inputStream);
        } catch (IOException e) {
            return 0.0f;
        }
    }

    private static float matchCommentAgainstDictionary(String text, InputStream dictionary) throws IOException {
        try (Directory directory = new RAMDirectory()) {
            org.apache.lucene.analysis.Analyzer analyzer = new RussianAnalyzer();

            // index comment
            try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
                Document doc = new Document();
                doc.add(new Field("comment", text, TextField.TYPE_STORED));
                writer.addDocument(doc);
            }

            // match against dictionary
            try (DirectoryReader reader = DirectoryReader.open(directory)) {

                Query query = buildQuery(tokenize(analyzer, dictionary));

                return new IndexSearcher(reader).search(query, 1).getMaxScore();
            }
        }
    }

    private static Query buildQuery(List<String> tokens) {
        BooleanQuery.setMaxClauseCount(tokens.size() * 2);

        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        tokens.forEach(token -> {
            queryBuilder
                    .add(new FuzzyQuery(new Term("comment", token), 1), BooleanClause.Occur.SHOULD)
                    .add(new WildcardQuery(new Term("comment", "*"+token+"*")), BooleanClause.Occur.SHOULD);
        });

        return queryBuilder.build();
    }

    private static List<String> tokenize(org.apache.lucene.analysis.Analyzer analyzer, InputStream stream) {
        try (TokenStream tokenStream = analyzer.tokenStream(null,  new InputStreamReader(stream))) {
            List<String> result = new ArrayList<>();
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                result.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
            }
            return result;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
