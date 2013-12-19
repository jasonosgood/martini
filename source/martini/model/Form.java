package martini.model;

import aron.ARONWriter;


public class 
	Form<P extends Page> 
{
//	private P _page = null;
//	
//	public void setPage( P page )
//	{
//		_page = page;
//	}
//	
//	public P getPage()
//	{
//		return _page;
//	}
	
	public String toString()
	{
		return ARONWriter.toString( this );
	}
}
