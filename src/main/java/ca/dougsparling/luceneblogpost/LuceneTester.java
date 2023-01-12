package ca.dougsparling.luceneblogpost;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Filter;

import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.queries.spans.SpanNearQuery;
import org.apache.lucene.queries.spans.SpanQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.*;

public class LuceneTester {

    String indexDir = "D:\\eddress\\lucene\\lucene-testbed\\src\\main\\java\\ca\\dougsparling\\luceneblogpost\\storage\\index";
    String dataDir = "D:\\eddress\\lucene\\lucene-testbed\\src\\main\\java\\ca\\dougsparling\\luceneblogpost\\storage\\docs_ar";
    Indexer indexer;
    Searcher searcher;
    String topic = "oya"; // simulation of tenantUids/ operationUids
    String locale = "ar"; // ja, en, ar
    private Scanner stdin = new Scanner(System.in);

    public static void main(String[] args) {
        LuceneTester tester;
        try {
            tester = new LuceneTester();
            tester.createIndex();
            tester.search();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void createIndex() throws IOException {
        indexer = new Indexer(indexDir, locale);
        int numIndexed;
        long startTime = System.currentTimeMillis();

        numIndexed = indexer.createIndex(dataDir, new TextFileFilter(), topic, locale);
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed+" File indexed, time taken: "
                +(endTime-startTime)+" ms");
    }

    public class TextFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".txt");
        }
    }

    private void search() throws IOException, ParseException {
        searcher = new Searcher(indexDir);
        loop();
    }


    // Does not work very well. The parser method is better.
    private Query buildQueryLatin_FuzzySearchMethod(String queryText, String topic, String locale) throws IOException, ParseException {
        // Filter
        BooleanQuery.Builder filterQueryBuilder = new BooleanQuery.Builder();

        TermQuery topicQuery = new TermQuery(new Term("topic", topic));
        BooleanClause booleanClauseTopic = new BooleanClause(topicQuery, BooleanClause.Occur.FILTER);
        filterQueryBuilder.add(booleanClauseTopic);

        TermQuery localeQuery = new TermQuery(new Term("locale", locale));
        BooleanClause booleanClauseLocale = new BooleanClause(localeQuery, BooleanClause.Occur.FILTER);
        filterQueryBuilder.add(booleanClauseLocale);

        // Parse
        String[] terms = queryText.split(" ");
        SpanQuery[] spanQueries = new SpanQuery[terms.length];
        int count = 0;
        for (String word : terms) {
            FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("body", word), 2);
            spanQueries[count] = new SpanMultiTermQueryWrapper<>(fuzzyQuery); count += 1;
        }

        SpanNearQuery spanFuzzyQuery = new SpanNearQuery(spanQueries, 0, true);

        filterQueryBuilder.add(new BooleanClause(spanFuzzyQuery, BooleanClause.Occur.SHOULD));


        return filterQueryBuilder.build();
    }

    private Query buildQueryLatin_ParserMethod(String queryText, String topic, String locale) throws IOException, ParseException {
        // Filter
        BooleanQuery.Builder filterQueryBuilder = new BooleanQuery.Builder();

        TermQuery topicQuery = new TermQuery(new Term("topic", topic));
        BooleanClause booleanClauseTopic = new BooleanClause(topicQuery, BooleanClause.Occur.FILTER);
        filterQueryBuilder.add(booleanClauseTopic);

        TermQuery localeQuery = new TermQuery(new Term("locale", locale));
        BooleanClause booleanClauseLocale = new BooleanClause(localeQuery, BooleanClause.Occur.FILTER);
        filterQueryBuilder.add(booleanClauseLocale);

        // Parse
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"body"}, new DialogueAnalyzer());
        //ComplexPhraseQueryParser parser = new ComplexPhraseQueryParser("body", new DialogueAnalyzer()); // This works well with fuzzy ~ as well
        parser.setDefaultOperator(QueryParser.Operator.OR);
        Query parseQuery = parser.parse(queryText);



        BooleanClause parseBooleanClause = new BooleanClause(parseQuery, BooleanClause.Occur.SHOULD);
        filterQueryBuilder.add(parseBooleanClause);


        return filterQueryBuilder.build();
    }



    private Query buildQueryArabic(String queryText, String topic, String locale) throws IOException, ParseException {
        // Filter
        BooleanQuery.Builder filterQueryBuilder = new BooleanQuery.Builder();

        TermQuery topicQuery = new TermQuery(new Term("topic", topic));
        BooleanClause booleanClauseTopic = new BooleanClause(topicQuery, BooleanClause.Occur.FILTER);
        filterQueryBuilder.add(booleanClauseTopic);

        TermQuery localeQuery = new TermQuery(new Term("locale", locale));
        BooleanClause booleanClauseLocale = new BooleanClause(localeQuery, BooleanClause.Occur.FILTER);
        filterQueryBuilder.add(booleanClauseLocale);

        // Parse
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"body"}, new ArabicAnalyzer());
        parser.setDefaultOperator(QueryParser.Operator.OR);
        Query parseQuery = parser.parse(queryText);

        BooleanClause parseBooleanClause = new BooleanClause(parseQuery, BooleanClause.Occur.SHOULD);
        filterQueryBuilder.add(parseBooleanClause);

        return filterQueryBuilder.build();

    }

    private Query buildQueryJap(String queryText, String topic, String locale) throws IOException, ParseException {

        // Filter
        BooleanQuery.Builder filterQueryBuilder = new BooleanQuery.Builder();

        TermQuery topicQuery = new TermQuery(new Term("topic", topic));
        BooleanClause booleanClauseTopic = new BooleanClause(topicQuery, BooleanClause.Occur.FILTER);
        filterQueryBuilder.add(booleanClauseTopic);

        TermQuery localeQuery = new TermQuery(new Term("locale", locale));
        BooleanClause booleanClauseLocale = new BooleanClause(localeQuery, BooleanClause.Occur.FILTER);
        filterQueryBuilder.add(booleanClauseLocale);

        // Parse
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"body"}, new JapaneseAnalyzer());
        parser.setDefaultOperator(QueryParser.Operator.OR);

        Query parseQuery = parser.parse(queryText);
        BooleanClause parseBooleanClause = new BooleanClause(parseQuery, BooleanClause.Occur.SHOULD);
        filterQueryBuilder.add(parseBooleanClause);


        return filterQueryBuilder.build();
    }

    private void loop() throws IOException, ParseException {
        String queryText = askForNextQuery();
        while(queryText != null) {

            long startTime = System.currentTimeMillis();

            Query query = null;
            if (locale == "jp") query = buildQueryJap(queryText, topic, locale);
            else if (locale == "ar") query = buildQueryArabic(queryText, topic, locale);
            else query = buildQueryLatin_ParserMethod(queryText, topic, locale);

            TopDocs hits = searcher.search(query);
            long endTime = System.currentTimeMillis();

            System.out.println(hits.totalHits +
                    " documents found. Time :" + (endTime - startTime));
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                System.out.println("--------------");
                System.out.println("File: "
                        + doc.get("title") + scoreDoc.score +  "\n" + doc.get("bodystore").strip());
            }

            queryText = askForNextQuery();
        }
    }

    private String askForNextQuery() {
        System.out.print("Query: ");

        String queryText = stdin.nextLine();

        if (queryText.isEmpty()) {
            return null;
        }

        return queryText;
    }
}
