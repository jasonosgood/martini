package martini.model;

import java.util.ArrayList;

public class 
	OptGroup 
//implements
//	SelectItem
{
	private static final long serialVersionUID = 1L;
	
	private String _label = null;
	
	public void setLabel( String label )
	{
		_label = label;
	}
	
	public boolean hasLabel()
	{
		return _label != null;
	}
	
	public String getLabel()
	{
		return _label;
	}
	
//	// TODO: Fix aron's reflection so this helper method isn't needed
//	public void addSelectItem( SelectItem selectItem )
//	{
//		add( selectItem );
//	}
	
//	protected ArrayList<SelectItem> _children = new ArrayList<SelectItem>();
//	
//	public void setChildren( ArrayList<SelectItem> children )
//	{
//		_children = children;
//	}
//
//	public ArrayList<SelectItem> getChildren()
//	{
//		return _children;
//	}
	
	protected ArrayList<Option> _children = new ArrayList<Option>();
	
	public void setChildren( ArrayList<Option> children )
	{
		_children = children;
	}

	public ArrayList<Option> getChildren()
	{
		return _children;
	}

	
//	public void setValue( String value ) 
//	{
//		if( value == null || value.length() == 0 ) return;
//		for( SelectItem item : _children )
//		{
//			if( item instanceof OptGroup )
//			{
//				((OptGroup) item).setValue( value );
//			}
//			else
//			{
//				Option option = (Option) item;
//				String temp = null;
//				if( option.hasValue() )
//				{
//					temp = option.getValue();
//				}
//				else
//				{
//					temp = option.getText();
//				}
//					
//				boolean selected = value.equals( temp );
//				
//				option.setSelected( selected );
//			}
//		}
//	}
//	
//	public String getValue() 
//	{
//		for( SelectItem item : _children )
//		{
//			if( item instanceof OptGroup )
//			{
//				return ((OptGroup) item).getValue();
//			}
//			else
//			{
//				Option option = (Option) item;
//				if( option.getSelected() )
//				{
//					return option.getValue();
//				}
//			}
//		}		
//		return null;
//	}
//	
	
	public String toString()
	{
		return String.format( "optgroup label: %s", _label );
	}
	
}
