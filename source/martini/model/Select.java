package martini.model;

import java.util.ArrayList;

import aron.ARONWriter;

public class 
	Select 
{
	private static final long serialVersionUID = 1L;
	
	private String _name;
	
	public void setName( String name )
	{
		_name = name;
	}
	
	public String getName() { return _name; }
	
	public String toString()
	{
		return ARONWriter.toString( this );
	}

	protected ArrayList _children = new ArrayList();
	
	public void setChildren( ArrayList children )
	{
		_children = children;
	}

	public ArrayList getChildren()
	{
		return _children;
	}
	
	public Option getOption( String value )
	{
		if( value == null )
		{
			throw new NullPointerException( "value" );
		}
		
		for( Object child : _children )
		{
			if( child instanceof OptGroup )
			{
				for( Option grandchild : ((OptGroup) child).getChildren() )
				{
					Option option = (Option) grandchild;
					if( value.equals( option.getValue() ))
					{
						return option;
					}
				}
			}
			else
			{
				Option option = (Option) child;
				if( value.equals( option.getValue() ))
				{
					return option;
				}
			}
		}
		
		String msg = String.format( "Option '%s' not found", value );
		throw new IllegalArgumentException( msg );
	}

	public void setValue( String[] values )
	{
		if( values == null ) return;
		for( String value : values )
		{
			setValue( value );
		}		
	}
	
	public void setValue( String value )
	{
		if( value == null )
		{
			throw new NullPointerException( "value" );
		}
		
		for( Object child : _children )
		{
			if( child instanceof OptGroup )
			{
				for( Option grandchild : ((OptGroup) child).getChildren() )
				{
					Option option = (Option) grandchild;
					if( value.equals( option.getValue() ))
					{
						option.setSelected( true );
					}
				}
			}
			else
			{
				Option option = (Option) child;
				if( value.equals( option.getValue() ))
				{
					option.setSelected( true );
				}
			}
		}
		
//		String msg = String.format( "Option '%s' not found", value );
//		throw new IllegalArgumentException( msg );
	}

	public String getValue() 
	{
		for( Object child : _children )
		{
			if( child instanceof OptGroup )
			{
				for( Option grandchild : ((OptGroup) child).getChildren() )
				{
					if( grandchild.getSelected() ) { return grandchild.getValue(); }
				}
			}
			else
			{
				Option option = (Option) child;
				if( option.getSelected() ) { return option.getValue(); }
			}
		}
		return null;
	}
	
}
