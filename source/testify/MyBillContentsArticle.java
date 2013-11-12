package testify;

import java.io.File;
import java.io.IOException;

import martini.HTMLBuilder;
import martini.runtime.MarkdownRender;


public class 
	MyBillContentsArticle 
extends 
	BillContentsArticle
{
	public void write( HTMLBuilder builder )
		throws IOException
	{
		Bill page = getPage();
		String biennium = page.getBienniumParam();
		String billNumber = page.getBillNumberParam();
//		File source = new File( "./../testify.old/data/" + biennium + "/" + billNumber +".txt" );
		File source = new File( "/Users/jasonosgood/Desktop/testify-data/text" + "/" + billNumber +".txt" );
		if( source.exists() )
		{
			System.out.println( "found it!" );
			// TODO: verify file exists.
			
			MarkdownRender render = new MarkdownRender();
//		HTMLBuilder builder = new HTMLBuilder( System.out );
			render.renderBill( source, builder );
		}
		// TODO: throw exception when broken

	}

}
