package martini;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

	String contextPath = "";
	
	@Override
	public void init( FilterConfig config )
		throws ServletException
	{
		contextPath = config.getServletContext().getContextPath();
//		FILTER = Flogger.newFlogger( this.getClass(), Subject.DEFAULT, Level.INFO ); 
//		FILTER.getSubject().addAdapter( new ConsoleAdapter() );
//		FILTER.log( "WhizzyFilter.init" );
		System.out.println( "MartiniFilter.init" );
		
		router = new Router();
		
		try 
		{
			URL url = Thread.currentThread().getContextClassLoader().getResource( "pagelist.txt" );
			router.load( url );
		} 
		catch( Exception e ) 
		{
			e.printStackTrace();
			throw new ServletException( e );
		}
	}

	public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain )
		throws IOException, ServletException
	{
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		HttpSession session = request.getSession( true );
		boolean isNewSession = session.isNew();
		
		
		String originalURI = request.getRequestURI();
		String uri = originalURI;
		uri = uri.substring( contextPath.length() );
		
		Page page = router.getPage( uri );
		
		if( page == null )
		{
			System.out.printf( "request: %s forwarded\n", uri );
			chain.doFilter( servletRequest, servletResponse );
			return;
		}
		
		
		System.out.printf( "request: %s %s ACCEPTED\n", request.getMethod(), uri );
				
		
		response.setContentType( "text/html" ); // WTF? Why this one?
		
		response.setHeader( "Cache-Control", "no-cache, no-store, must-revalidate" ); // HTTP 1.1.
		response.setHeader( "Pragma", "no-cache" ); // HTTP 1.0.
		response.setDateHeader( "Expires", 0 ); // Proxies.
		
			try
			{
				long start = System.currentTimeMillis();
				
				Map<String,String[]> params = page.init( request, response );
				if( params != null && params.size() > 0 )
				{
					page.populateForm( params );
				}
				Handler<Page> handler = page.getHandler();
				String method = request.getMethod();
				switch( method )
				{
					case "GET":
						handler.GET( page, request, response );
						break;
					case "POST":
						handler.POST( page, request, response );
						break;
					default:
						throw new Exception( "unsuppported HTTP method" );
				}
				
				page.render( response );
				
				long elapsed = System.currentTimeMillis() - start;
				page.setElapsed( elapsed );
				
			}
			catch( RedirectException e )
			{
				int code = e.getCode();
				if( code > 299 && code < 400 )
				{
					response.setStatus( code );
					String location = e.getLocation();
					location = response.encodeRedirectURL( location );
					response.addHeader( "Location", location );
				}
			}
			catch( Exception e )
			{
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
				response.setContentType( "text/plain" );
				PrintWriter writer = response.getWriter();
				writer.println( "HTTP Status: 400 Bad Request" );
				writer.println();
				writer.print( "error processing " + request.getMethod() + " " + request.getRequestURI() );
				if( request.getQueryString() != null )
				{
					writer.println( "?" + request.getQueryString() );
				}
				writer.println();
				writer.println();
				e.printStackTrace( writer );
	
				// TODO log it too
				System.out.print( "error processing " + request.getMethod() + " " + request.getRequestURI() );
				if( request.getQueryString() != null )
				{
					System.out.println( "?" + request.getQueryString() );
				}
				System.out.println();
				System.out.println();
				e.printStackTrace( System.out );
				
			}
			finally
			{
				// Close the page, just in case there was an exception
				// Allows developer to retry, debug, etc, without bouncing Martini
				page.close();
			}
		return;
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
