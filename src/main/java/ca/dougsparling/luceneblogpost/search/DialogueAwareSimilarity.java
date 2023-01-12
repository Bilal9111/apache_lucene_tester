package ca.dougsparling.luceneblogpost.search;
/*
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;

public final class DialogueAwareSimilarity extends Similarity {
	
	@Override
	public float scorePayload(int doc, int start, int end, BytesRef payload) {
		if (payload.bytes[payload.offset] == 0) {
			return 0.0f;
		}
		return super.scorePayload(doc, start, end, payload);
	}
}

 */