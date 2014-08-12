package martini.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class 
	Handler<P extends Page> 
{
	public void GET( P page, HttpServletRequest request, HttpServletResponse response ) throws Exception {}
	
	public void POST( P page, HttpServletRequest request, HttpServletResponse response ) throws Exception {}
}
