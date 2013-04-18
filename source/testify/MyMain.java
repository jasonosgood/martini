package testify;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class
	MyMain
extends 
	Main
{
	int hitsPerPage = 10;
	int numTotalHits = 0;
	
	@Override
	public void beforeHandle() 
	{
		String startText = getSearchForm().getStart().toString().trim();
		int start = 0;
		if( startText.length() > 0 ) 
		{
			try
			{
				start = Integer.valueOf( startText );
			}
			catch( NumberFormatException e )
			{
				e.printStackTrace();
			}
		}		
		String queryString = getSearchForm().getQ().toString().trim();
		if( queryString.length() == 0 ) return;
		try
		{
			String index = "index";
			String field = "contents";
			
			File file = new File( index );
			IndexReader reader = IndexReader.open( FSDirectory.open( file ));
			
			IndexSearcher searcher = new IndexSearcher( reader);
			Analyzer analyzer = new StandardAnalyzer( Version.LUCENE_35 );

//			QueryParser parser = new QueryParser( Version.LUCENE_35, field, analyzer );
			String[] fieldList = { "contents", "billID", "legalTitle", "longDesc", "billNumber", "biennium" };
			MultiFieldQueryParser parser = new MultiFieldQueryParser( Version.LUCENE_35, fieldList, analyzer );
			
			Query query = parser.parse(queryString);
			
			System.out.println("Searching for: " + query.toString(field));
	
//			long now = System.currentTimeMillis();
			TopDocs results = searcher.search( query, null, 100 );
	
			numTotalHits = results.totalHits;
//			long end = System.currentTimeMillis();
//			float elapsed = (float)( end - now );
			
			StringBuilder sb = new StringBuilder();
			Formatter formatter = new Formatter( sb, Locale.US );
			
			formatter.format( "%,d results", numTotalHits );
			setMetrics( sb.toString() );
	       
			List<MainResultsItem> list = getResults();
			for( int nth = 0; nth < results.scoreDocs.length; nth++ )
			{
				if( nth < start ) continue;
				if( nth == start + hitsPerPage ) break;
				ScoreDoc scoreDoc = results.scoreDocs[nth];
				Document doc = searcher.doc( scoreDoc.doc );
				String biennium = doc.get( "biennium" );
				String billID = doc.get( "billID" );
				String billNumber = doc.get( "billNumber" );
				String desc = doc.get( "longDesc" );
				String legal = doc.get( "legalTitle" );
				MainResultsItem item = new MainResultsItem();
				item.setBiennium( biennium );
				item.setBillID( billID );
				item.setDesc( desc );
				item.setLegal( legal );
				item.setTitleHref( "bill/"+ biennium + "/" + billNumber );
				list.add( item );
			}
	
			searcher.close();
			reader.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public List<MainPagesItem> getPages()
	{
		List<MainPagesItem> list = new ArrayList<MainPagesItem>();
		String startText = getSearchForm().getStart().toString().trim();
		int start = 0;
		if( startText.length() > 0 ) 
		{
			try
			{
				start = Integer.valueOf( startText );
			}
			catch( NumberFormatException e )
			{
				e.printStackTrace();
			}
		}
		// Round page offsets to multiples of hitsPerPage
		start = ( start % hitsPerPage ) * hitsPerPage;
		
		int begin = Math.max( 0, start - ( hitsPerPage * 3 ));
		String queryString = getSearchForm().getQ().toString().trim();
		int pages = numTotalHits / hitsPerPage;
		
		for( int nth = 0; nth < pages; nth++ )
		{
			MainPagesItem item = new MainPagesItem();
			item.setNth( Integer.toString( nth + 1 ));
			list.add( item );
			
			item.setNthHref( "?q=" + queryString + "&start=" + ( nth * hitsPerPage ));
		}
		return list;
	}
}
