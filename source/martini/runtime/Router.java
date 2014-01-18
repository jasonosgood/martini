package martini.runtime;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.rits.cloning.Cloner;

import aron.ARONReader;
import aron.LabelNode;
import martini.model.Page;
import martini.util.LineReader;

public class 
	Router 
{
	ArrayList<Page> pageList = new ArrayList<Page>();

	public void load( URL url )
		throws Exception
	{
		System.out.println( "Router loading " + url.toString() );
		InputStream in = url.openStream();
		load( in );
	}
	
//	public void load( File dispatchFile )
	public void load( InputStream in )
		throws Exception
	{
		HashSet<String> loaded = new HashSet<String>();
		
		LineReader reader = new LineReader( in );
//		System.out.println( "loading " + dispatchFile.toString() );
		for( String pagearon : reader )
		{
			int x = pagearon.indexOf( '#' );
			if( x > -1 )
			{
				pagearon = pagearon.substring( 0, x );
			}
			pagearon = pagearon.replaceAll( "\\s+", " " );
			pagearon = pagearon.trim();
			if( pagearon.length() == 0 ) continue;

			if( loaded.contains( pagearon ))
			{
				System.out.printf( "page declaration file %s already loaded, skipping\n", pagearon );
				continue;
			}
			
			LabelNode rootNode = null;
			URL url = Thread.currentThread().getContextClassLoader().getResource( pagearon );
			if( url != null )
			{
				ARONReader aron = new ARONReader();
				rootNode = aron.read( url );
			}
			else
			{
				File file = new File( pagearon );
				if( !file.exists() )
				{
					System.out.printf( "cannot find page declaration file %s, skipping\n", pagearon );
					continue;
				}
				if( !file.isFile() )
				{
					System.out.printf( "%s is not a file\n", pagearon );
					continue;
				}
				if( !file.canRead() )
				{
					System.out.printf( "cannot read page declaration file %s, skipping\n", pagearon );
					continue;
				}
			
				ARONReader aron = new ARONReader();
				rootNode = aron.read( file );
			}
				
			if( rootNode != null )
			{
				Object temp = rootNode.find( "page" );
				if( temp == null )
				{
					System.out.printf( "label 'page' not found in declaration file %s\n", pagearon );
					continue;
				}
				if( temp instanceof Page )
				{
					Page page = (Page) temp;
					boolean unique = true;
					for( Page loadedPage : pageList )
					{
						if( loadedPage.getURI().equals( page.getURI() ))
						{
							unique = false;
							String loadedPageName = loadedPage.getClass().getName().replace( '.', '/' );
							System.out.printf( "cannot load page %s, page %s already uses uri \"%s\"\n", pagearon, loadedPageName, loadedPage.getURI() );
							break;
						}
					}
					
					if( unique )
					{
						pageList.add( page );
						loaded.add( pagearon );
						System.out.printf( "added route \"%s\" for page %s\n", page.getURI(), pagearon );
					}
				}
			}
		}
	}

	public Page getPage( String uri )
	{
		Map<String,String> params = new HashMap<String,String>();
		for( Page page : pageList )
		{
			if( match( uri, page, params ))
			{
				// TODO Is Cloner threadsafe?
				Cloner cloner = new Cloner();
				Page clone = cloner.deepClone( page );
				clone.setUrlParams( params );
				return clone;
			}
		}
		
		return null;
	}

	public boolean match( String request, Page page, Map<String,String> params )
	{
		String target = page.getURI();
		if( request.equals( target )) return true;
		
		String[] requestList = request.split( "/" );
		String[] targetList = target.split( "/" );
		
		if( requestList.length != targetList.length ) return false;
		
		for( int n = 0; n < requestList.length; n++ )
		{
			String a = targetList[n];
			String b = requestList[n];
			
			if( a.equals( b )) continue;
			
			if( a.charAt( 0 ) == '{' && a.charAt( a.length() - 1 ) == '}' )
			{
				String key = a.substring( 1, a.length() - 1 );
				params.put( key, b );
				continue;
			}
			
			// If match fails, clear params
			params.clear();
			return false;
		}
		
		return true;
	}
	
}
