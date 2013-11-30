package testify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import martini.HTMLBuilder;
import martini.runtime.MarkdownRender;


public class 
	MyBillContentsArticle 
extends 
	BillContentsArticle
{
	private String _root = null;
	
	public void setRoot( String root ) 
		throws FileNotFoundException
	{
		if( root == null )
		{
			throw new NullPointerException( "root" );
		}
		File temp = new File( root );
		if( !temp.exists() )
		{
			throw new FileNotFoundException( root );
		}
		_root = root;
	}
	
	public String getRoot()
	{
		return _root;
	}
	
	public void write( HTMLBuilder builder )
		throws IOException
	{
		Bill page = getPage();
		String biennium = page.getBienniumParam();
		String billNumber = page.getBillNumberParam();
		String filename = getRoot() + billNumber +".txt";
		File source = new File( filename );
		if( source.exists() )
		{
			System.out.println( "found it!" );
			// TODO: verify file exists.
			
			MarkdownRender render = new MarkdownRender();
			render.renderBill( source, builder );
		}
		// TODO: throw exception when broken

	}

}
