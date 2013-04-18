package martini.model;

public class 
	Option 
{
	private String _value = null;
	
	public void setValue( String value )
	{
		_value = value;
	}
	
	public String getValue()
	{
		return _value;
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
	
}
