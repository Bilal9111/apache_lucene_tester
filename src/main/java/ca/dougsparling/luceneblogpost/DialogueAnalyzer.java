package ca.dougsparling.luceneblogpost;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;

import ca.dougsparling.luceneblogpost.filter.DialoguePayloadTokenFilter;
import ca.dougsparling.luceneblogpost.filter.QuotationTokenFilter;
import ca.dougsparling.luceneblogpost.tokenizer.QuotationTokenizer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;

import static org.apache.lucene.analysis.en.EnglishAnalyzer.ENGLISH_STOP_WORDS_SET;

public class DialogueAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		
		StandardTokenizer tokenizer = new StandardTokenizer();
		//TokenFilter filter = new QuotationTokenFilter(tokenizer);

		TokenFilter filter = new LowerCaseFilter(tokenizer);
		filter = new StopFilter(filter, ENGLISH_STOP_WORDS_SET);
		filter = new DialoguePayloadTokenFilter(filter);
		filter = new ASCIIFoldingFilter(filter); //ICUFoldingFilter

		return new TokenStreamComponents(tokenizer, filter);
	}
}
