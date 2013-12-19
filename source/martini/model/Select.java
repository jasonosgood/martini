package martini.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;

import aron.ARONWriter;

public class 
	Select 
extends 
	ArrayList<Option> 
{
	private static final long serialVersionUID = 1L;

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
	
	public String toString()
	{
		return ARONWriter.toString( this );
	}
}
