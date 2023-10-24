package com.programming01;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilterFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;


public class Programming01 {
	
	private static final String TEXT = "Today is sunny. She is a sunny girl. To be or not to be. She is in Berlin today. Sunny Berlin! Berlin is always exciting!";
    
	public static void main(String[] args) throws IOException {
        // Call to the Analyser - As per Programming Assignment 1 - sub-question 'c'
    	analyser();
        
    }
    
    public static void analyser() throws IOException{
    	
    	// Analyser with respective processing steps as mentioned in subquestion 'c'
        CustomAnalyzer customAnalyzer = CustomAnalyzer.builder(Paths.get("C:/Users/HP/eclipse-workspace/P01/src/resource/"))
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
}



