package martini.util;

import java.net.URL;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import martini.model.Handler;
import martini.model.Page;
import aron.ARONReader;
import aron.LabelNode;


public class 
	TestPageRunner 
{
	public static void main( String[] args ) 
		throws Exception
	{
//		Class<?> pageClass = Class.forName( "testify.Main" );
		String name = args[0];
		Class<?> pageClass = Class.forName( name );
		
		String resource = pageClass.getName().replace( '.', '/' ).concat( ".aron" );
		URL url = pageClass.getClassLoader().getResource( resource );
		if( url == null )
		{
			throw new ServletException( "cannot find '" + resource + "'" );
		}
		
		ARONReader aron = new ARONReader();
		LabelNode rootNode = aron.read( url );
		if( rootNode != null )
		{
			Object temp = rootNode.find( "page" );		
			if( temp instanceof Page )
			{
				Page page = (Page) temp;
				
				HttpServletRequest request = new MockServletRequest();
				HttpServletResponse response = new MockServletResponse();

				
				long start = System.currentTimeMillis();
				
				Map<String,String[]> params = page.init( request, response );
				if( params != null && params.size() > 0 )
				{
					page.populateForm( params );
				}
				Handler handler = page.getHandler();
				handler.GET( page, request, response );
				page.render( response );
				
				long elapsed = System.currentTimeMillis() - start;
				page.setElapsed( elapsed );

			}
		}
	}
}
