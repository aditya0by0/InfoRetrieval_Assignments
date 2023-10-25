package asignment1;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

public class IRassignment1 {
	static Analyzer analyzer;
    public static void whitespaceanalyzer(Analyzer analyzer, String input) throws IOException
    {
    	analyzer = new WhitespaceAnalyzer();
    	TokenStream ts = analyzer.tokenStream("field", input);
        CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

       ts.reset(); 
        while (ts.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }

        ts.end();
        ts.close();
    }
    
    public static void standardanalyzer(Analyzer analyzer, String input) throws IOException
    {
    	analyzer = new StandardAnalyzer();
    	TokenStream ts = analyzer.tokenStream("field", input);
        CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

        ts.reset();
        while (ts.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }

        ts.end();
        ts.close();
    }
    
    public static void main(String[] args) throws IOException {
        String input = "Today is sunny. She is a sunny girl. To be or not to be. She is in Berlin today.\r\n"
        		+ "Sunny Berlin! Berlin is always exciting!\r\n"
        		+ "";
        System.out.println("tokens generated using whitespace tokenizer:");
        whitespaceanalyzer(analyzer,input);
        System.out.println("\ntokens generated using standard tokenizer:");
        standardanalyzer(analyzer,input);
         }
}
