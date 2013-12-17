package martini.runtime;

import javax.servlet.http.HttpServletResponse;


public class 
	RedirectException 
extends 
	Exception 
{
	private static final long serialVersionUID = 1L;
	
	private int _code = HttpServletResponse.SC_FOUND;
	
	public int getCode() { return _code; }
	
	private String _location;
	
	public String getLocation() { return _location; }
	
	public RedirectException( String location )
	{
		_location = location;
	}
	
	public RedirectException( int status, String location )
	{
		_code = status;
		_location = location;
	}
}
