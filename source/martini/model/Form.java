package martini.model;


public class 
	Form<P extends Page> 
{
	private P _page = null;
	
	public void setPage( P page )
	{
		_page = page;
	}
	
	public P getPage()
	{
		return _page;
	}

//	public Form() {}
//	
//	public Form( HttpServletRequest request )
//	{
//		
//	}
	
//	private String _method = "GET";
//	
//	public void setMethod( String method )
//	{
//		_method = method;
//	}
//	
//	public String getMethod()
//	{
//		return _method;
//	}
	
}
