package martini.model;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import martini.HTMLBuilder;


public abstract class 
	Page
extends 
	HTMLBuilder
{
	public abstract String getURI();
	
	public abstract void setUrlParams( Map<String,String> params );
	
	public HttpServletRequest _request = null;
	
	public HttpServletRequest getRequest() 
	{
		return _request;
	}
	
	public String getRequestParameter( String key )
	{
		String result = getRequest().getParameter( key );
		return result != null ? result : "";
	}
	
	public HttpServletResponse _response = null;
	
	public HttpServletResponse getResponse()
	{
		return _response;
	}
	
	public void beforeHandle() throws Exception {}
	
	public void handle( HttpServletRequest request, HttpServletResponse response )
		throws ServletException, IOException
	{
		_request = request;
		_response = response;
	}
	
	public void afterHandle() throws Exception {}
	
	/**
	 *  Generated subclass overrides template method this. Used to transfer URI's 
	 *  query parameters to the Page instance.
	 */
	public void populateForm()
	{
	}
	
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
	
}
