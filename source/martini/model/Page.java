package martini.model;

import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aron.ARONWriter;
import martini.HTMLBuilder;


public abstract class 
	Page
extends 
	HTMLBuilder
{
//	private String _uri = "/";
//	
//	public void setURI( String uri )
//	{
//		_uri = uri;
//	}
	
	public String getURI() 
	{ 
		return "/" + this.getClass().getCanonicalName().toString(); 
	}
	
	public abstract void setUrlParams( Map<String,String> params );
	
	public String getRequestParameter( Map<String,String[]> map, String key )
	{
		String result = "";
		if( map.containsKey( key ))
		{
			result = map.get( key )[0];
		}
		return result;
	}

	public boolean hasRequestParameter( Map<String,String[]> map, String key )
	{
		return map.containsKey( key );
	}
	
	// TODO: Is this generic type correct?
	private Handler<Page> _handler = new Handler<Page>();
	
	public void setHandler( Handler<Page> handler )
	{
		if( handler == null )
		{
			handler = new Handler<Page>();
		}
		_handler = handler;
	}
	
	public Handler<Page> getHandler()
	{
		return _handler;
	}
	
	public Map<String,String[]> init( HttpServletRequest request, HttpServletResponse response )
		throws ServletException, IOException
	{
		Map<String,String[]> result = null;
		
		String method = request.getMethod();
		switch( method )
		{
			case "GET":
			{
				Map<String,String[]> map = request.getParameterMap();
				if( map.size() > 0 )
				{
					result = map;
				}
				break;
			}
			case "POST":
			{
				try
				{
					Reader reader = null;
					StringBuilder sb = new StringBuilder();
					try
					{
						reader = request.getReader();
						int n;
						while ((n = reader.read()) != -1 )
						{
							sb.append( (char) n ); 
						}
						result = extractMap( sb.toString() );
					}
					finally
					{
						reader.close();
						System.out.println( sb.toString() );
						System.out.println(" -- ");
					}
				}
				catch( Exception e )
				{
					//
				}
				break;
			}
		}
		return result;
	}
	
	public HashMap<String,String[]> extractMap( String payload )
	{
		try
		{
			HashMap<String,String[]> map = new HashMap<String,String[]>();
			String[] stuff = payload.split( "&" );
			for( String item : stuff )
			{
				String[] pair = item.split( "=" );
				String key = pair[0];
				key = URLDecoder.decode( key, "UTF-8" ).trim();
				
				if( pair.length > 1 )
				{
					String value = URLDecoder.decode( pair[1], "UTF-8" );
//					System.out.printf( "\n%s = %s", key, value );
					String[] values = new String[]{ value };
					map.put( key, values );
				}
			}
			return map;
		}
		catch( Exception e )
		{
			return null;
		}
	}

	public abstract void render( HttpServletResponse response ) throws ServletException, IOException;

	/**
	 *  Generated subclass overrides template method this. Used to transfer URI's 
	 *  query parameters to the Page instance.
	 */
	public abstract void populateForm( Map<String,String[]> params );

	// Every HTML page has a title. 
	private String _title = null;
	
	public void setTitle( String title )
	{
		_title = title;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	// ID set by the Dispatcher
	private String _id = null;
	
	public void setID( String id )
	{
		_id = id;
	}
	
	public String getID()
	{
		return _id;
	}
	
	private long _elapsed = -0L;
	
	public void setElapsed( long elapsed )
	{
		_elapsed = elapsed;
	}
	
	public long getElapsed()
	{
		return _elapsed;
	}
	
//	public String toString()
//	{
//		StringBuilder sb = new StringBuilder();
//		sb.append( "uri: " );
//		sb.append( getURI() );
//		sb.append( "\nid: " );
//		sb.append( getID() );
//		sb.append( "\ntitle: " );
//		sb.append( getTitle() );
//		
//		toString( sb );
//		return sb.toString();
//	}
//
//	public abstract String toString( StringBuilder sb );
}
