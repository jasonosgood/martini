package martini.runtime;

import java.io.File;
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
	
	public void load( File dispatchFile )
		throws Exception
	{
		HashSet<String> loaded = new HashSet<String>();
		
		LineReader reader = new LineReader( dispatchFile );
		System.out.println( "loading " + dispatchFile.toString() );
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
			URL url = ClassLoader.getSystemClassLoader().getResource( pagearon );
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
					for( Page p : pageList )
					{
						if( p.getURI().equals( page.getURI() ))
						{
							unique = false;
							System.out.printf( "cannot add page %s, page %s already uses uri %s\n", pagearon, page.getClass().getName(), page.getURI() );
							break;
						}
					}
					
					if( unique )
					{
						pageList.add( page );
						loaded.add( pagearon );
						System.out.printf( "added page %s via uri %s\n", pagearon, page.getURI() );
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
