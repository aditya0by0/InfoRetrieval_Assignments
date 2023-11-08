package com.P02;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

public class P02 {
    
    public static void main(String[] args) throws Exception {
        Directory directory = indexDocument();
        if (directory != null) {
        	printIndexedDocuments(directory);
        	System.out.println("------****--------------------");
        	findDocument(directory);
        }
    }

    public static Directory indexDocument() throws Exception {
        String[] documents = {
            "Today is sunny.",
            "She is a sunny girl.",
            "To be or not to be.",
            "She is in Berlin today.",
            "Sunny Berlin!",
            "Berlin is always exciting!"
        };

        Directory directory = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        for (int i = 0; i < documents.length; i++) {
            Document doc = new Document();
            String[] words = documents[i].split("[^a-zA-Z]+");
            
            for (String word:words) {
            	System.out.println(word);
            }

            for (String word : words) {
                Field textField = new TextField("content", word.toLowerCase(), Field.Store.YES);
                doc.add(textField);
            }

            indexWriter.addDocument(doc);
        }

        indexWriter.close();
        return directory;
    }

    public static void findDocument(Directory directory) throws Exception {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        TermQuery querySunny = new TermQuery(new Term("content", "sunny"));
        TermQuery queryExciting = new TermQuery(new Term("content", "exciting"));

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(querySunny, BooleanClause.Occur.SHOULD);
        builder.add(queryExciting, BooleanClause.Occur.SHOULD);

        BooleanQuery query = builder.build();

        System.out.println("Documents containing 'sunny' or 'exciting':");
        for (int i = 0; i < indexReader.maxDoc(); i++) {
            Document document = indexSearcher.doc(i);
            if (document != null && document.getField("content") != null) {
                System.out.println("Document " + i + ": " + document.getField("content").stringValue());
            }
        }
    }
    
    public static void printIndexedDocuments(Directory directory) throws Exception {
        IndexReader indexReader = DirectoryReader.open(directory);

        for (int i = 0; i < indexReader.maxDoc(); i++) {
            Document document = indexReader.document(i);
            System.out.println("Document " + i + " Fields:");
            for (IndexableField field : document.getFields()) {
                System.out.println(field.name() + ": " + document.get(field.name()));
            }
            System.out.println("-------------------");
        }
    }
}
