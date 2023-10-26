package com.programming01;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.CharArraySet;

public class Programming01 {
	
	private static final String TEXT = "Today is sunny. She is a sunny girl. To be or not to be. She is in Berlin today. Sunny Berlin! Berlin is always exciting!";

	public static void main(String[] args) throws IOException {
		// Call to the Tokenizer - As per Programming Assignment 1 - sub-question 'a'
		System.out.println("------------------- Task P01 - a   -------------------");
		tokenizerTaskA();

		// Call to the Analyser - As per Programming Assignment 1 - sub-question 'c'
		System.out.println("------------------- Task P01 - b   -------------------");
		stopWord(); // call stopWord Function
		
		// Call to the Analyser - As per Programming Assignment 1 - sub-question 'c'
		System.out.println("------------------- Task P01 - c   -------------------");
    		analyser();
        
    }
    
    public static void analyser() throws IOException{
        // Get working dir path & append stopwords file path to it 
    	Path currentDirectory = Paths.get(System.getProperty("user.dir"));
        Path stopWordFilePath = currentDirectory.resolve("src").resolve("resource");

    	// Analyser with respective processing steps as mentioned in subquestion 'c'
        CustomAnalyzer customAnalyzer = CustomAnalyzer.builder(stopWordFilePath)
        		.withTokenizer("standard")
        		.addTokenFilter("lowercase")
        		.addTokenFilter("stop", "ignoreCase", "false", "format", "wordset", "words", "stopwords.txt")
        		.addTokenFilter(PorterStemFilterFactory.class)
        		.build();
        
        // Print the tokens
        try {
            TokenStream tokenStream = customAnalyzer.tokenStream("text", new StringReader(TEXT));
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            System.out.println("Tokens after pre-processing:");
            while (tokenStream.incrementToken()) {
                System.out.println(charTermAttribute.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    }
    
    public static void whitespaceanalyzer(Analyzer analyzer, String input) throws IOException {
        TokenStream ts = analyzer.tokenStream("field", input);
        CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

        ts.reset();
        while (ts.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }

        ts.end();
        ts.close();
    }

    public static void standardanalyzer(Analyzer analyzer, String input) throws IOException {
        TokenStream ts = analyzer.tokenStream("field", input);
        CharTermAttribute charTermAttribute = ts.addAttribute(CharTermAttribute.class);

        ts.reset();
        while (ts.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }

        ts.end();
        ts.close();
    }
    
    public static void tokenizerTaskA() throws IOException {
        Analyzer whitespaceAnalyzer = new WhitespaceAnalyzer();
        Analyzer standardAnalyzer = new StandardAnalyzer();

        System.out.println("Tokens generated using whitespace tokenizer:");
        whitespaceanalyzer(whitespaceAnalyzer, TEXT);
        System.out.println("\nTokens generated using standard tokenizer:");
        standardanalyzer(standardAnalyzer, TEXT);
    }

	private static void stopWord() {
	try {
		System.out.println("Stopword eliminator");
		List<String> stopWords=Arrays.asList("was", "is", "in", "to", "be");
		CharArraySet stopSet=new CharArraySet(stopWords, true);
		Analyzer analyzer= new StandardAnalyzer(stopSet);
		List<String> result = new ArrayList<String>();
		TokenStream stream  = analyzer.tokenStream("",TEXT);
		stream.reset();
		while (stream.incrementToken()) {
			result.add(stream.addAttribute(CharTermAttribute.class).toString());
		}
		System.out.println(result);
	} catch (IOException e) {
		// not thrown b/c we're using a string reader...
		throw new RuntimeException(e);
	}

	
}

}



