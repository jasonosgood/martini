package martini.runtime;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rits.cloning.Cloner;

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
		InputStream in = pageClass.getClassLoader().getResourceAsStream( resource );
		if( in == null )
		{
			throw new ServletException( "cannot find '" + resource + "'" );
		}
		
		ARONReader aron = new ARONReader();
		LabelNode rootNode = aron.read( in );
		if( rootNode != null )
		{
			Object temp = rootNode.find( "page" );		
			if( temp instanceof Page )
			{
				Page page = (Page) temp;
				page.populateForm();
				page.beforeHandle();
				
				HttpServletRequest request = new MockServletRequest();
				HttpServletResponse response = new MockServletResponse();

				page.handle( request, response );
				page.afterHandle();

//				Page model = (Page) temp;
//				Page page = (Page)pageClass.newInstance();
//				
//				setter( page, model );
//				page.setModel( model );
//				HttpServletResponse response = new MockServletResponse();
//				page.handle( null, response );
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
