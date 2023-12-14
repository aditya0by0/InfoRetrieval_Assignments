package asignment1;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class p04 {

    static Directory index = new RAMDirectory();

    public static double calculateEuclideanDistance(ArrayList<Double> vector1, ArrayList<Double> vector2) {
        // Method to compute the Euclidean distance between two vectors
        double distance = 0;
        assert (vector1.size() == vector2.size());
        for (int i = 0; i < vector1.size(); i++) {
            distance += Math.pow(vector1.get(i) - vector2.get(i), 2);
        }
        return Math.sqrt(distance);
    }

    public static double calculateDotProduct(ArrayList<Double> vector1, ArrayList<Double> vector2) {
        // Method to compute the dot product between two vectors
        double product = 0;
        assert (vector1.size() == vector2.size());
        for (int i = 0; i < vector1.size(); i++) {
            product += vector1.get(i) * vector2.get(i);
        }
        return product;
    }

    public static double calculateCosineSimilarity(ArrayList<Double> vector1, ArrayList<Double> vector2) {
        // Method to compute the cosine similarity between two vectors
        double dotProduct = 0;
        double magnitude1 = 0;
        double magnitude2 = 0;
        assert (vector1.size() == vector2.size());
        for (int i = 0; i < vector1.size(); i++) {
            magnitude1 += Math.pow(vector1.get(i), 2);
            magnitude2 += Math.pow(vector2.get(i), 2);
            dotProduct += vector1.get(i) * vector2.get(i);
        }
        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        return dotProduct / (magnitude1 * magnitude2);
    }

    private static void addDocumentToIndex(IndexWriter indexWriter, String content, String documentID) throws
    IOException {
		// Method to add a document to the IndexWriter
		Document document = new Document();
		
		FieldType fieldType =
		        new FieldType(); // Custom field type enabling term position tracking
		fieldType.setStored(true);
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		fieldType.setStoreTermVectorPositions(true);
		fieldType.setTokenized(true);
		fieldType.setStoreTermVectors(true);
		
		document.add(new StringField("documentID", documentID, Field.Store.YES));
		document.add(new Field("content", content, fieldType));
		indexWriter.addDocument(document);
	}

    public static TreeMap<String, ArrayList<Double>> generateTF_IDFMatrix(String text, Analyzer analyzer)
            throws IOException, ParseException {
        // Method to create a TF-IDF matrix from the provided documents using a custom Analyzer
        // Log base 10 is used to calculate IDF
        TreeMap<String, ArrayList<Double>> tfIdFMatrix = new TreeMap<>();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(index, config);

        String[] textSplit = text.split("[.!?]");
        int documentCount = 1;
        for (String temp : textSplit) {
            addDocumentToIndex(indexWriter, temp, "d" + documentCount);
            documentCount++;
        }
        indexWriter.close();

        ArrayList<String> tokens = new ArrayList<>();
        TokenStream tokenStream = analyzer.tokenStream(text, text);
        tokenStream.reset();

        while (tokenStream.incrementToken()) {
            CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
            tokens.add(attribute.toString());
        }
        tokenStream.close();

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        int totalDocuments = reader.numDocs();

        Term term;
        PostingsEnum docEnum;
        for (String token : tokens) {
            Query tempQuery = new QueryParser("content", analyzer).parse(token);
            term = new Term("content", token.toLowerCase());
            docEnum = MultiFields.getTermDocsEnum(reader, "content", term.bytes());
            ArrayList<Double> tfIdfWeights = new ArrayList<>(Collections.nCopies(totalDocuments, 0.0));
            double termIdf = Math.log10((double) totalDocuments / (double) searcher.count(tempQuery));
            for (int k = 0; k < searcher.count(tempQuery); k++) {
                docEnum.nextDoc();
                tfIdfWeights.add(docEnum.docID(), (double) docEnum.freq() * termIdf);
            }
            tfIdFMatrix.put(token, tfIdfWeights);
        }

        return tfIdFMatrix;
    }

    public static TreeMap<Float, String> tfIdfScoring(Query query) throws IOException {
        //function to score the documents on the query using TF_IDF similarity
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new ClassicSimilarity());
        TreeMap<Float, String> tfIdfScores = new TreeMap<>(Collections.reverseOrder());
        Scorer scorer = query.createWeight(searcher, true, 1).scorer(reader.leaves().get(0));
        DocIdSetIterator docIdSetIterator = scorer.iterator();

        while (docIdSetIterator.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            tfIdfScores.put(scorer.score(), reader.document(scorer.docID()).get("content"));
        }
        return tfIdfScores;
    }

    public static TreeMap<Float, String> bm25Scoring(Query query) throws IOException {
        //function to score the documents on the query using the recommended BM25 similarity
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        TreeMap<Float, String> bm25Scores = new TreeMap<>(Collections.reverseOrder());
        Scorer scorer = query.createWeight(searcher, true, 1).scorer(reader.leaves().get(0));
        DocIdSetIterator docIdSetIterator = scorer.iterator();

        while (docIdSetIterator.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            bm25Scores.put(scorer.score(), reader.document(scorer.docID()).get("content"));
        }
        return bm25Scores;
    }


    public static void main(String[] args) {
        try (Analyzer customAna = CustomAnalyzer.builder()
            .withTokenizer("standard")
            .addTokenFilter("lowercase")
            .build()) {
            //----------------------- Sub-question a) -----------------------
            String text =
                "Today is sunny. She is a sunny girl. To be or not to be. She is in Berlin today. " +
                    "Sunny Berlin! Berlin is always exciting!";
            System.out.println("------PART A------");
            System.out.println("TF-IDF matrix: \n");
            System.out.println("    [D1,      D2,       D3,       D4,       D5,       D6]\n");
            TreeMap<String, ArrayList<Double>> tdIdfMatrix = generateTF_IDFMatrix(text, customAna);
            tdIdfMatrix.forEach((k, v) -> System.out.println(k + " = " + v));

            ArrayList<Double> d1V = new ArrayList<>();
            ArrayList<Double> d2V = new ArrayList<>();

            tdIdfMatrix.forEach((k, v) -> d1V.add(v.get(0)));
            tdIdfMatrix.forEach((k, v) -> d2V.add(v.get(1)));

            System.out.println("\nVector representation of Document 1: " + d1V);
            System.out.println("Vector representation of Document 2: " + d2V);

            System.out.println("\nSimilarity between Document 1 and Document 2 using Euclidean distance = " +
                1 / (1 + calculateEuclideanDistance(d1V, d2V)));
            System.out.println(
                "Similarity between Document 1 and Document 2 using Dot product = " + calculateDotProduct(d1V, d2V));
            System.out.println(
                "Similarity between Document 1 and Document 2 using cosine similarity = " + calculateCosineSimilarity(d1V, d2V) + "\n");

          //----------------------- Sub-question b) -----------------------
            System.out.println("------PART B------");
            Query query = new QueryParser("content", customAna).parse("She is a sunny girl.");
            System.out.println("\nquery : 'She is a sunny girl.'");
            System.out.println("\nScores using Vector Space Model: ");
            tfIdfScoring(query).forEach((k, v) -> System.out.println(k + " : " + v));
            System.out.println("\nScores using BM25 Model :");
            bm25Scoring(query).forEach((k, v) -> System.out.println(k + " : " + v));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
