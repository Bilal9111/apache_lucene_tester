package ca.dougsparling.luceneblogpost;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;

public class SimpleFileIndexer {

    public static void main(String[] args) throws Exception {

        File indexDir = new File("C:/index/");
        File dataDir = new File("C:/programs/eclipse/workspace/");
        String suffix = "java";

        SimpleFileIndexer indexer = new SimpleFileIndexer();

        //int numIndex = indexer.index(indexDir, dataDir, suffix);

        //System.out.println("Total files indexed " + numIndex);

    }
}

    /*
    private int index(File indexDir, File dataDir, String suffix) throws Exception {

        IndexWriter indexWriter = new IndexWriter(
                FSDirectory.open(indexDir.toPath()),
                new SimpleAnalyzer(),
                true,
                IndexWriter.MAX_TERM_LENGTH);
        indexWriter.setUseCompoundFile(false);

        File[] files = dataDir.listFiles();
        File f = files[0];

        if (f.isHidden() || f.isDirectory() || !f.canRead() || !f.exists()) {
//                return;
        }
        if (suffix!=null && !f.getName().endsWith(suffix)) {
//                return;
        }
        System.out.println("Indexing file " + f.getCanonicalPath());

        Document doc = new Document();

        doc.add(new Field("contents", new FileReader(f), ));
        doc.add(new Field("filename", f.getCanonicalPath().toCharArray(), Field.Store.YES));

        doc.add(new TextField("KEY", "@#$%^&*(", Field.Store.YES));

        indexWriter.addDocument(doc);
        int numIndexed = indexWriter.maxDoc();
        indexWriter.optimize();
        indexWriter.close();

//            return numIndexed;

        Directory directory = FSDirectory.open(new Uri("indexDir").getPath());

        IndexSearcher searcher = new IndexSearcher(directory);
        QueryParser parser = new QueryParser(Version.LUCENE_30,
                "contents", new SimpleAnalyzer());
        Query query = parser.parse(queryStr);

        TopDocs topDocs = searcher.search(query, maxHits);

        ScoreDoc[] hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println(d.get("filename"));
        }

        System.out.println("Found " + hits.length);

    }

     */

// public static void main( String[] args ) throws Exception {
//  Analyzer analyzer = new WhitespaceAnalyzer(Version.LATEST);
//  QueryParser parser = new QueryParser(Version.LATEST,  "f", analyzer );
//  Query query = parser.parse( "a x:b" );
//  FieldQuery fieldQuery = new FieldQuery( query, true, false );

//  Directory dir = new RAMDirectory();
//  IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LATEST, analyzer));
//  Document doc = new Document();
//  FieldType ft = new FieldType(TextField.TYPE_STORED);
//  ft.setStoreTermVectors(true);
//  ft.setStoreTermVectorOffsets(true);
//  ft.setStoreTermVectorPositions(true);
//  doc.add( new Field( "f", ft, "a a a b b c a b b c d e f" ) );
//  doc.add( new Field( "f", ft, "b a b a f" ) );
//  writer.addDocument( doc );
//  writer.close();

//  IndexReader reader = IndexReader.open(dir1);
//  new FieldTermStack( reader, 0, "f", fieldQuery );
//  reader.close();
/*
    MultiFieldQueryParser(String[] fields, Analyzer analyzer)

*   Collator collator = Collator.getInstance(new Locale("tr", "TR"));
*   collator.setStrength(Collator.PRIMARY);
*   Analyzer analyzer = new CollationKeyAnalyzer(collator);
*   Path dirPath = Files.createTempDirectory("tempIndex");
*   Directory dir = FSDirectory.open(dirPath);
*   IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(analyzer));
*   Document doc = new Document();
*   doc.add(new TextField("contents", "DIGY", Field.Store.NO));
*   writer.addDocument(doc);
*   writer.close();
*   IndexReader ir = DirectoryReader.open(dir);
*   IndexSearcher is = new IndexSearcher(ir);
*   QueryParser parser = new QueryParser("contents", analyzer);
*   Query query = parser.parse("d\u0131gy");   // U+0131: dotless i
*   ScoreDoc[] result = is.search(query, null, 1000).scoreDocs;
*   assertEquals("The index Term should be included.", 1, resul


 */