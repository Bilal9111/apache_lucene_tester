package ca.dougsparling.luceneblogpost;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Recursively indexes a directory full of text files (or zip files containing
 * text files). The index is written to a directory, which will be overwritten if
 * necessary.
 */
public class LuceneIndexerApp {
	
	private final Path indexPath;

	public LuceneIndexerApp(Path indexPath) {
		this.indexPath = indexPath;
	}

	private void addToIndex(Path docPath) throws IOException, InterruptedException {
		Directory indexDir = FSDirectory.open(this.indexPath);
		
		Analyzer indexAnalyzer = new DialogueAnalyzer();
		
		IndexWriterConfig writerConfig = new IndexWriterConfig(indexAnalyzer);
		writerConfig.setOpenMode(OpenMode.CREATE);
		
		ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		try (IndexWriter writer = new IndexWriter(indexDir, writerConfig)) {
			AsyncWriteFileToIndexVisitor fileAsyncIndexer = new AsyncWriteFileToIndexVisitor(writer, threadPoolExecutor);
			Files.walkFileTree(docPath, fileAsyncIndexer);
			threadPoolExecutor.shutdown();
			threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
			writer.forceMerge(1, true);
		}
	}
	
	public static void main(String... args) throws IOException, ParseException, InterruptedException {

		System.err.println("working");
		new LuceneIndexerApp(Paths.get("D:\\eddress\\lucene\\lucene-testbed\\src\\main\\java\\ca\\dougsparling\\luceneblogpost\\storage\\index")).addToIndex(Paths.get("D:\\eddress\\lucene\\lucene-testbed\\src\\main\\java\\ca\\dougsparling\\luceneblogpost\\storage\\docs"));
	}
}
