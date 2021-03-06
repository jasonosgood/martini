package martini;

import static martini.util.Util.firstCharUpper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import aron.ARONWriter;
import lox.Element;

public class ModelBuilder 
{
	String uri = null;
	
	ArrayList<String> urlParamList = new ArrayList<String>();
	
	public void addURLParam( String param )
		throws Exception
	{
		if( urlParamList.contains( param ))
		{
			throw new Exception( "parameter already defined: " + param );
		}
		urlParamList.add( param ); 
	}
	
	// Meta data
	String title = null;
	
	
	static class Table
	{
		String id;
		Header header;
		Body body;
	}

	static class Header
	{
		ArrayList<Row> rowList = new ArrayList<Row>();
	}
	
	static class Body extends Header
	{
	}
	
	static class Row
	{
		ArrayList<Cell> cellList = new ArrayList<Cell>();
	}
	
	static class Cell
	{
		String id;
		String value;
	}
	
	ArrayList<Table> tableList = new ArrayList<Table>();
	
	Table table;
	Header ugh;
	Row row;
	Cell cell;
	
	public void addTable( String id )
	{
		table = new Table();
		table.id = firstCharUpper( id.trim() );
		tableList.add( table );
	}
	
	public void addHeader()
	{
		table.header = new Header();
		ugh = table.header;
	}

	public void addBody()
	{
		table.body = new Body();
		ugh = table.body;
	}
	
	public void addRow()
	{
		row = new Row();
		ugh.rowList.add( row );
	}
	
	public void addCell( String id, String value )
	{
		cell = new Cell();
		cell.id = firstCharUpper( id.trim() );
		cell.value = value;
		row.cellList.add( cell );
	}
	
	
	static class Form
	{
		String id;
		ArrayList<Input> inputList = new ArrayList<Input>();
		ArrayList<Select> selectList = new ArrayList<Select>();
		ArrayList<Textarea> textareaList = new ArrayList<Textarea>();
	}
	
	static abstract class Input
	{
		String type;
		String name;
	}
	
	static class SubmitInput extends Input
	{
	}
	
	static class TextInput extends Input
	{
		String value;
	}
	
	static class BooleanInput extends Input
	{
		boolean value;
	}
	
	static interface OptionChild {}
	
	static class Option implements OptionChild
	{
		public String value;
		public String text;
		public boolean selected;
		public HashMap<String,Object> attribs = new HashMap<>();
		public String toString() {
			return "option value: " + value + " selected: " + selected + " text: " + text;
		}
	}
	
	static class OptGroup implements OptionChild
	{
		public String label;
		public ArrayList<OptionChild> children = new ArrayList<OptionChild>();
		public String toString() {
			return "optgroup label: " + label + " " + children.toString();
		}
	}
	
	static class Select extends OptGroup
	{
		public String name;
		public String toString() {
//			return "select name: " + name + " " + children.toString();
			return ARONWriter.toString( this );
		}
	}
	
	static class Textarea
	{
		String name;
		String value;
	}
	
	ArrayList<Form> formList = new ArrayList<Form>();
	
	Form form;
	Input input;
	Select select;
	Stack<OptGroup> optGroupStack = new Stack<OptGroup>();
	Option option;
	Textarea textarea;
	
	public void addForm( String id )
	{
		form = new Form();
		form.id = firstCharUpper( id.trim() );
		formList.add( form );
	}
	
	public void addSubmitInput( String type, String name )
	{
		SubmitInput temp = new SubmitInput();
		temp.type = type;
		if( name != null )
		{
			temp.name = firstCharUpper( name.trim() );
		}
		input = temp;
		form.inputList.add( input );
	}
	
	public void addTextInput( String type, String name, String value )
	{
		TextInput temp = new TextInput();
		temp.type = type;
		temp.name = firstCharUpper( name.trim() );
		temp.value = value;
		input = temp;
		form.inputList.add( input );
	}
	
	public void addBooleanInput( String type, String name, boolean value )
	{
		BooleanInput temp = new BooleanInput();
		temp.type = type;
		temp.name = firstCharUpper( name.trim() );
		temp.value = value;
		input = temp;
		form.inputList.add( input );
	}
	
	public void addSelect( String name )
	{
		select = new Select();
		select.name = firstCharUpper( name.trim() );
		form.selectList.add( select );
		optGroupStack.clear();
		optGroupStack.push( select );
	}
	
	public void pushOptGroup( String label )
	{
		OptGroup optGroup = new OptGroup();
		optGroup.label = label.trim();
		
		OptGroup top = optGroupStack.peek();
		top.children.add( optGroup );
		optGroupStack.push( optGroup );
	}
	
	public void popOptGroup()
	{
		optGroupStack.pop();
	}
	
	public void addOption( String value, String text, boolean selected, HashMap<String,Object> attribs )
	{
		option = new Option();
		option.value = value;
		option.text = text;
		option.selected = selected;
		option.attribs = attribs;
		OptGroup top = optGroupStack.peek();
		top.children.add( option );
//		select.optionList.add( option );
	}

	public void addTextarea( String name, String value )
	{
		textarea = new Textarea();
		textarea.name = firstCharUpper( name.trim() );
		textarea.value = value;
		form.textareaList.add( textarea );
	}
	
	static class List
	{
		String id;
		ArrayList<List> children = new ArrayList<List>();
		ArrayList<ListItem> itemList = new ArrayList<ListItem>();
	}
	
	static class ListItem
	{
		// Only used when there's no other subelements representing parameters, eg <ul whiz:id="ugh"><li>Text</li></ul>
		String text = null;
		ArrayList<ListItemParameter> paramList = new ArrayList<ListItemParameter>();
	}
	
	static class ListItemParameter
	{
		static enum Kind { DIV, SPAN, A };
		Kind kind;
		String id;
		String text;
		String href;
	}
	
//	ArrayList<List> listList = new ArrayList<List>();
//	List list;
	List rootList = new List();
	Stack<List> listStack = new Stack<List>();
	ListItem item;
	ListItemParameter itemParam;
	
	public ModelBuilder()
	{
		listStack.push( rootList );
	}
	
    public void pushList( String id )
    {
    	List child = new List();
    	child.id = firstCharUpper( id.trim() );
    	
    	List parent = listStack.peek();
    	parent.children.add( child );
    	listStack.push( child );
    }
    
    public void popList( String id )
    	throws IllegalArgumentException
    {
    	List parent = listStack.pop();
    	String parentID = parent.id;
		if( !parentID.equalsIgnoreCase( id ))
    	{
    		String msg = String.format( "stack's list ids don't match, expected %s, actual %s", id, parentID );
			throw new IllegalArgumentException( msg );
    	}
    }
    
    
    public void addListItem()
    {
    	item = new ListItem();
    	List parent = listStack.peek();
    	parent.itemList.add( item );
    }
    
    public void addListItemParameter( ListItemParameter.Kind kind, String id )
    {
    	itemParam = new ListItemParameter();
    	itemParam.kind = kind;
    	itemParam.id = firstCharUpper( id.trim() );
    	item.paramList.add( itemParam );
    }

    static class Article
    {
    	Element element;
    	String id;
    }
    
    ArrayList<Article> articleList = new ArrayList<Article>();

    public void addArticle( Element element, String id ) 
	{
		Article article = new Article();
		article.element = element;
		article.id = firstCharUpper( id.trim() );
		articleList.add( article );
	}

	static class Property
	{
		String id;
		String text;
		String href;
	}
	
	ArrayList<Property> propertyList = new ArrayList<Property>();
	
	public void addProperty( String id, String text, String href )
	{
		Property property = new Property();
		property.id = id.trim();
		property.text = text;
		property.href = href;
		propertyList.add( property );
	}
	
	ArrayList<String> optionalElementList = new ArrayList<String>();
	
	public void addOptionalElement( String id )
	{
		optionalElementList.add( id );
	}
    
}
