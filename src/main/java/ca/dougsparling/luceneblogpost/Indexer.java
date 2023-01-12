package ca.dougsparling.luceneblogpost;


import java.io.*;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

    private IndexWriter writer;

    public Indexer(String indexDirectoryPath, String locale) throws IOException {
        Directory indexDirectory =
                FSDirectory.open(Paths.get(indexDirectoryPath));

        Analyzer analyzer = null;
        if (locale == "jp") analyzer = new JapaneseAnalyzer();
        else if (locale == "ar") analyzer = new ArabicAnalyzer();
        else analyzer = new DialogueAnalyzer();

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        writer = new IndexWriter(indexDirectory, iwc);
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private Document getDocument(File file, String topic, String locale) throws IOException {
        Document document = new Document();

        TextField contentField = new TextField("body", new FileReader(file));

        TextField fileNameField = new TextField("title", file.getName(),TextField.Store.YES);

        TextField topicPathField = new TextField("topic", topic,TextField.Store.YES);

        TextField localePathField = new TextField("locale", locale,TextField.Store.YES);

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            TextField contentStoreField = new TextField("bodystore",
                    everything,TextField.Store.YES);
            document.add(contentStoreField);
        }

        document.add(contentField);
        document.add(fileNameField);
        document.add(topicPathField);
        document.add(localePathField);

        return document;
    }

    private void indexFile(File file, String topic, String locale) throws IOException {
        System.out.println("Indexing "+file.getCanonicalPath());
        Document document = getDocument(file, topic, locale);
        writer.addDocument(document);
    }

    public int createIndex(String dataDirPath, FileFilter filter, String topic, String locale)
            throws IOException {
        File[] files = new File(dataDirPath).listFiles();

        for (File file : files) {
            if(!file.isDirectory()
                    && !file.isHidden()
                    && file.exists()
                    && file.canRead()
                    && filter.accept(file)
            ){
                indexFile(file, topic, locale);
            }
        }
        return writer.numRamDocs();
    }
}
