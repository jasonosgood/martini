package martini.runtime;

public class 
	NotFoundException 
extends 
	Exception 
{
	public NotFoundException( Throwable t )
	{
		super( t );
	}
	
	public NotFoundException( String msg )
	{
		super( msg );
	}
	
}
