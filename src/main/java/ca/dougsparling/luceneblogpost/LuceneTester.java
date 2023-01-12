package ca.dougsparling.luceneblogpost;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

public class LuceneTester {

    String indexDir = "D:\\eddress\\lucene\\lucene-testbed\\src\\main\\java\\ca\\dougsparling\\luceneblogpost\\storage\\index";
    String dataDir = "D:\\eddress\\lucene\\lucene-testbed\\src\\main\\java\\ca\\dougsparling\\luceneblogpost\\storage\\docs_ar";
    Indexer indexer;
    Searcher searcher;
    String topic = "oya"; // simulation of tenantUids/ operationUids
    String locale = "ar"; // jp, en, ar
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

    private Query buildQueryLatin(String queryText, String topic, String locale) throws IOException, ParseException {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();

        String[] terms = queryText.split("\\W+");
        for (String word : terms) {
            Term term = new Term("body", word);
            FuzzyQuery termQuery = new FuzzyQuery(term, 2);
            BooleanClause booleanClause = new BooleanClause(termQuery, BooleanClause.Occur.SHOULD);
            booleanQueryBuilder.add(booleanClause);
        }
        TermQuery topicQuery = new TermQuery(new Term("topic", topic));
        BooleanClause booleanClause = new BooleanClause(topicQuery, BooleanClause.Occur.SHOULD);
        booleanQueryBuilder.add(booleanClause);

        TermQuery localeQuery = new TermQuery(new Term("locale", locale));
        BooleanClause booleanClauseLocale = new BooleanClause(localeQuery, BooleanClause.Occur.MUST);
        booleanQueryBuilder.add(booleanClauseLocale);
        return booleanQueryBuilder.build();
    }

    private Query buildQueryArabic(String queryText, String topic, String locale) throws IOException, ParseException {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();

        String[] terms = queryText.split(" ");
        for (String word : terms) {
            Term term = new Term("body", word);
            FuzzyQuery termQuery = new FuzzyQuery(term, 2);
            BooleanClause booleanClause = new BooleanClause(termQuery, BooleanClause.Occur.SHOULD);
            booleanQueryBuilder.add(booleanClause);
        }
        TermQuery topicQuery = new TermQuery(new Term("topic", topic));
        BooleanClause booleanClause = new BooleanClause(topicQuery, BooleanClause.Occur.SHOULD);
        booleanQueryBuilder.add(booleanClause);

        TermQuery localeQuery = new TermQuery(new Term("locale", locale));
        BooleanClause booleanClauseLocale = new BooleanClause(localeQuery, BooleanClause.Occur.MUST);
        booleanQueryBuilder.add(booleanClauseLocale);
        return booleanQueryBuilder.build();
    }

    private Query buildQueryJap(String queryText, String topic, String locale) throws IOException, ParseException {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();

        Term term = new Term("body", queryText);
        FuzzyQuery termQuery = new FuzzyQuery(term, 2);
        BooleanClause booleanClause = new BooleanClause(termQuery, BooleanClause.Occur.SHOULD);
        booleanQueryBuilder.add(booleanClause);

        TermQuery topicQuery = new TermQuery(new Term("topic", topic));
        BooleanClause booleanClauseTopic = new BooleanClause(topicQuery, BooleanClause.Occur.MUST);
        booleanQueryBuilder.add(booleanClauseTopic);

        TermQuery localeQuery = new TermQuery(new Term("locale", locale));
        BooleanClause booleanClauseLocale = new BooleanClause(localeQuery, BooleanClause.Occur.MUST);
        booleanQueryBuilder.add(booleanClauseLocale);
        return booleanQueryBuilder.build();
    }

    private void loop() throws IOException, ParseException {
        String queryText = askForNextQuery();
        while(queryText != null) {

            long startTime = System.currentTimeMillis();

            Query query = null;
            if (locale == "jp") query = buildQueryJap(queryText, topic, locale);
            if (locale == "ar") query = buildQueryArabic(queryText, topic, locale);
            else query = buildQueryLatin(queryText, topic, locale);

            TopDocs hits = searcher.search(query);
            long endTime = System.currentTimeMillis();

            System.out.println(hits.totalHits +
                    " documents found. Time :" + (endTime - startTime));
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                System.out.println("--------------");
                System.out.println("File: "
                        + doc.get("title") + "\n" + doc.get("bodystore").strip());
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
