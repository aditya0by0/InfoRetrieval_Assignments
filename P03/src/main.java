import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.FixedShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

public class main {

    public static void main(String[] args) throws IOException, ParseException {

        // +=====================================+
        // |               PART 1                |
        // +=====================================+

        Analyzer BiWordAnalyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String string) {
                StandardTokenizer tokenizer = new StandardTokenizer();
                TokenStream stream = new StandardFilter(tokenizer);
                stream = new FixedShingleFilter(stream, 2);
                return new TokenStreamComponents(tokenizer, stream);
            }
        };

        Directory directory = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(BiWordAnalyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        Document document = new Document();

        String text = "Today is sunny. She is a sunny girl. To be or not to be. She is in Berlin today. Sunny Berlin! Berlin is always exciting!";

        FieldType fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        fieldType.setStored(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorPositions(true);

        document.add(new Field("Main", text, fieldType));

        indexWriter.addDocument(document);
        indexWriter.close();

        // Print out the BiWords tokens
        TokenStream tokenStream = BiWordAnalyzer.tokenStream("Main", text);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        
        System.out.println("|==== Part A Result ====|");
        try
        {
            tokenStream.reset();
            while (tokenStream.incrementToken())
            {	
                String term = charTermAttribute.toString();
                System.out.println(term);
            }
             tokenStream.end();
        } finally {
            tokenStream.end();
            tokenStream.close();
        }

        // +=====================================+
        // |               PART 2                |
        // +=====================================+

        Directory directory2 = new RAMDirectory();

        IndexWriterConfig config2 = new IndexWriterConfig(BiWordAnalyzer);
        IndexWriter indexWriter2 = new IndexWriter(directory2, config2);
        Document document2 = new Document();

        String text2 = "York University in York of New";

        FieldType fieldType2 = new FieldType();
        fieldType2.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        fieldType2.setStored(true);
        fieldType2.setStoreTermVectors(true);
        fieldType2.setStoreTermVectorPositions(true);

        document2.add(new Field("Main", text2, fieldType2));

        indexWriter2.addDocument(document2);
        indexWriter2.close();

        String inputquery = "New York University";
        DirectoryReader directoryReader = DirectoryReader.open(directory2);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        QueryParser parser = new QueryParser("Main", BiWordAnalyzer);
        Query query = parser.parse(inputquery);
        TopDocs topDocs = indexSearcher.search(query, 1000);
        ScoreDoc[] hits = topDocs.scoreDocs;

        System.out.println("\n|==== Part B Result ====|");
        

        if(hits.length > 0)
        {
        	System.out.println("Input query: " + inputquery);
            for(int i = 0; i < hits.length; i++)
            {
                System.out.println("Found Document ID: " + hits[i].doc);
                Document hitDoc = indexSearcher.doc(hits[i].doc);
                System.out.println("Found Document text: " + hitDoc.get("Main"));
            }
        }
    }
}
