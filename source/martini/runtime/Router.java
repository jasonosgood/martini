package martini.runtime;

import java.io.File;
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
		System.out.printf( "loading dispatch.txt\n" );
		for( String filename : reader )
		{
			int x = filename.indexOf( '#' );
			if( x > -1 )
			{
				filename = filename.substring( 0, x );
			}
			filename = filename.replaceAll( "\\s+", " " );
			filename = filename.trim();
			if( filename.length() == 0 ) continue;
			File file = new File( filename );
			if( !file.exists() )
			{
				System.out.printf( "cannot find page declaration file %s, skipping\n", filename );
				continue;
			}
			if( !file.isFile() )
			{
				System.out.printf( "%s is not a file\n", filename );
				continue;
			}
			if( !file.canRead() )
			{
				System.out.printf( "cannot read page declaration file %s, skipping\n", filename );
				continue;
			}
			
			if( loaded.contains( filename ))
			{
				System.out.printf( "page declaration file %s already loaded, skipping\n", filename );
				continue;
			}
			
			
			ARONReader aron = new ARONReader();
			LabelNode rootNode = aron.read( file );
			if( rootNode != null )
			{
				Object temp = rootNode.find( "page" );
				if( temp == null )
				{
					System.out.printf( "label 'page' not found in declaration file %s\n", filename );
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
							System.out.printf( "cannot add page %s, page %s already uses uri %s\n", filename, page.getClass().getName(), page.getURI() );
							break;
						}
					}
					
					if( unique )
					{
						pageList.add( page );
						loaded.add( filename );
						System.out.printf( "added page %s via uri %s\n", filename, page.getURI() );
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
