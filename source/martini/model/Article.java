package martini.model;

import java.io.IOException;

import martini.HTMLBuilder;


public class 
	Article<P extends Page>
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

	public void write( HTMLBuilder builder )
		throws IOException
	{
		builder.element( "div" );
		builder.text( "{{ default text}}" );
		builder.pop();
	}
}
