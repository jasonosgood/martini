package martini.model;

import java.util.ArrayList;

public class 
	Select 
extends 
	ArrayList<Option> 
{
	// TODO: Fix aron's reflection so this helper method isn't needed
	public void addOption( Option option )
	{
		add( option );
	}
	
	public void setValue( String value ) 
	{
		if( value == null || value.length() == 0 ) return;
		for( Option option : this )
		{
			String temp = null;
			if( option.hasValue() )
			{
				temp = option.getValue();
			}
			else
			{
				temp = option.getText();
			}
				
			boolean selected = value.equals( temp );
			
			option.setSelected( selected );
		}
	}
	
	public String getValue() 
	{
		for( Option option : this )
		{
			if( option.getSelected() )
			{
				return option.getValue();
			}
		}
		return null;
	}
}
