package testify.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class 
	SearchLegislation 
{

	public static void main(String[] args) 
		throws Exception 
	{
		SearchLegislation ugh = new SearchLegislation();
		ugh.search();
	}
	
	public SearchLegislation() {}
	
	public void search()
		throws Exception
	{

		String index = "index";
		String field = "contents";
//		String queryString = "health billNumber:1946";
//		String queryString = "health";
//		String queryString = "HB 1000";
		String queryString = "schools";
		int hitsPerPage = 10;

		File file = new File( index );
		IndexReader reader = IndexReader.open( FSDirectory.open( file ));
		
		IndexSearcher searcher = new IndexSearcher( reader );
		Analyzer analyzer = new StandardAnalyzer( Version.LUCENE_35 );

//		QueryParser parser = new QueryParser( Version.LUCENE_35, field, analyzer );
		String[] fieldList = { "contents", "billID", "legalTitle", "longDesc", "billNumber", "biennium" };
		MultiFieldQueryParser parser = new MultiFieldQueryParser( Version.LUCENE_35, fieldList, analyzer );
		
		Query query = parser.parse(queryString);
		
		System.out.println("Searching for: " + query.toString(field));

		Date start = new Date();
		TopDocs results = searcher.search(query, null, 100);

		int numTotalHits = results.totalHits;
//		System.out.println(numTotalHits + " total matching documents");
		Date end = new Date();
		System.out.println("Time: " + (end.getTime() - start.getTime()) + "ms");
		int count = 0;
		for( ScoreDoc scoreDoc: results.scoreDocs )
		{
			Document doc = searcher.doc( scoreDoc.doc );
			String biennium = doc.get( "biennium" );
			String billID = doc.get( "billID" );
			String billNumber = doc.get( "billNumber" );
			String shortDesc = doc.get( "shortDesc" );
			String path = doc.get( "path");
			System.out.printf( "%s %s %s %s\n", biennium, billID, billNumber, shortDesc, path );
			count++;
			if( count == 10 ) break;
		}

		searcher.close();
		reader.close();
	}

}
