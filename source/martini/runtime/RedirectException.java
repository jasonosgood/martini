package martini.runtime;

import martini.model.Page;

public class 
	RedirectException 
extends 
	Exception 
{
	public Page page;
	
	public RedirectException( Page p )
	{
		page = p;
	}	
}
