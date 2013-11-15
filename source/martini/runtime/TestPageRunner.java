package martini.runtime;

import java.net.URL;

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
				
				page.init( request, response );
				page.populateForm();
				Handler handler = page.getHandler();
				handler.setup();
				page.render( response );
				
				long elapsed = System.currentTimeMillis() - start;
				page.setElapsed( elapsed );

			}
		}
	}

//	public static void setter( Page page, Model model ) 
//		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
//	{
//		for( Method method : page.getClass().getMethods() )
//		{
//			if( method.getName().equals( "setModel" ))
//			{
//				for( Class<?> oof : method.getParameterTypes() )
//				{
//					if( oof.isAssignableFrom( model.getClass() ) )
//					{
//						Object result = method.invoke( page, model );
//						return;
//					}
//					break;
//				}
//			}
//		}
//		
//		throw new NoSuchMethodException( page.getClass().getName() + ".setModel(" + model.getClass().getName() + ")" );
//	}


}
