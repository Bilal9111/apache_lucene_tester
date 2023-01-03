package ca.dougsparling.luceneblogpost;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneTester {

    String indexDir = "D:\\eddress\\lucene\\lucene-testbed\\src\\main\\java\\ca\\dougsparling\\luceneblogpost\\storage\\index";
    String dataDir = "D:\\eddress\\lucene\\lucene-testbed\\src\\main\\java\\ca\\dougsparling\\luceneblogpost\\storage\\docs";
    Indexer indexer;
    Searcher searcher;
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
            e.printStackTrace();
        }
    }

    private void createIndex() throws IOException {
        indexer = new Indexer(indexDir);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
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

    private void loop() throws IOException, ParseException {
        String queryText = askForNextQuery();
        while(queryText != null) {

            long startTime = System.currentTimeMillis();
            TopDocs hits = searcher.search(queryText);
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
