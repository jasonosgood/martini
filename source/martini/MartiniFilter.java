package martini;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import martini.model.Handler;
import martini.model.Page;
import martini.runtime.RedirectException;
import martini.runtime.Router;

public class 
	MartiniFilter 
implements 
	Filter 
{
	private static final long serialVersionUID = 1L;
	
//	public Flogger FILTER = null; 
	Router router = null;

	@Override
	public void init( FilterConfig config )
		throws ServletException
	{
//		FILTER = Flogger.newFlogger( this.getClass(), Subject.DEFAULT, Level.INFO ); 
//		FILTER.getSubject().addAdapter( new ConsoleAdapter() );
//		FILTER.log( "WhizzyFilter.init" );
		System.out.println( "MartiniFilter.init" );
		
		router = new Router();
		
		
//		dispatcher = new Dispatcher();
		try 
		{
			File dispatchFile = new File( "html/pagelist.txt" );
			router.load( dispatchFile );
		} 
		catch( Exception e ) 
		{
			e.printStackTrace();
			throw new ServletException( e );
		}
	}

	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
		throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String originalURI = httpRequest.getRequestURI();
		String uri = originalURI;
		
		Page page = router.getPage( uri );
		
		if( page == null )
		{
			System.out.printf( "request: %s forwarded\n", uri );
			chain.doFilter( request, response );
			return;
		}
		
		
		System.out.printf( "request: %s %s ACCEPTED\n", httpRequest.getMethod(), uri );
				
		
		httpResponse.setContentType( "text/html" ); // WTF? Why this one?
		
		httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		httpResponse.setDateHeader("Expires", 0); // Proxies.
		
		boolean done = false;
		while( !done )
		{
			try
			{
				page._request = httpRequest;
//			        char[] buffer = new char[request.getContentLength()];

				long start = System.currentTimeMillis();
				page.init( httpRequest, httpResponse );
				page.populateForm();
				Handler handler = page.getHandler();
				handler.setup();
				page.handle( httpRequest, httpResponse );
				long elapsed = System.currentTimeMillis() - start;
				page.setElapsed( elapsed );
				done = true;
			}
			catch( RedirectException e )
			{
				page = e.page;
			}
			catch( Exception e )
			{
				httpResponse.setStatus( HttpServletResponse.SC_BAD_REQUEST );
				response.setContentType( "text/plain" );
				PrintWriter writer = response.getWriter();
				writer.println( "HTTP Status: 400 Bad Request" );
				writer.println();
				writer.print( "error processing " + httpRequest.getMethod() + " " + httpRequest.getRequestURI() );
				if( httpRequest.getQueryString() != null )
				{
					writer.println( "?" + httpRequest.getQueryString() );
				}
				writer.println();
				writer.println();
				e.printStackTrace( writer );
	
				// TODO log it too
				System.out.print( "error processing " + httpRequest.getMethod() + " " + httpRequest.getRequestURI() );
				if( httpRequest.getQueryString() != null )
				{
					System.out.println( "?" + httpRequest.getQueryString() );
				}
				System.out.println();
				System.out.println();
				e.printStackTrace( System.out );
				done = true;
			}
			finally
			{
				// Close the page, just in case there was an exception
				// Allows developer to retry, debug, etc, without bouncing Martini
				page.close();
			}
		}
		return;
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
