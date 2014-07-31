package martini.model;

import java.util.HashMap;

public class 
	Option 
//implements
//	SelectItem
{
	private String _value = null;
	
	public void setValue( String value )
	{
		_value = value;
	}
	
	public boolean hasValue()
	{
		return _value != null;
	}
	
	public String getValue()
	{
		return hasValue() ? _value : getText();
	}
	
	private String _text = null;
	
	public void setText( String text )
	{
		_text = text;
	}
	
	public String getText()
	{
		return _text;
	}
	
	private boolean _selected = false;
	
	public void setSelected( boolean selected )
	{
		_selected = selected;
	}
	
	public boolean getSelected()
	{
		return _selected;
	}
	
	private HashMap<String,Object> _attributes = new HashMap<>();
	
	public void setAttributes( HashMap<String,Object> attributes )
	{
		_attributes = attributes;
	}
	
	public HashMap<String,Object> getAttributes()
	{
		return _attributes;
	}
	
	public String toString()
	{
		return String.format( "text: %s value: %s selected: %b", _text, _value, _selected );
	}
	
}
