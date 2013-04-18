package martini;

import static martini.util.Util.firstCharUpper;

import java.util.ArrayList;

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
		table.id = firstCharUpper( id );
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
		cell.id = firstCharUpper( id );
		cell.value = value;
		row.cellList.add( cell );
	}
	
	
	static class Form
	{
		String id;
		ArrayList<Input> inputList = new ArrayList<Input>();
		ArrayList<Select> selectList = new ArrayList<Select>();
	}
	
	static class Input
	{
		String type;
		String name;
		String value;
	}
	
	static class Select
	{
		String name;
		ArrayList<Option> optionList = new ArrayList<Option>();
	}
	
	static class Option
	{
		String value;
		String text;
		boolean selected;
	}
	
	ArrayList<Form> formList = new ArrayList<Form>();
	
	Form form;
	Input input;
	Select select;
	Option option;
	
	public void addForm( String id )
	{
		form = new Form();
		form.id = firstCharUpper( id );
		formList.add( form );
	}
	
	public void addInput( String type, String name, String value )
	{
		input = new Input();
		input.type = type;
		input.name = firstCharUpper( name );
		input.value = value;
		form.inputList.add( input );
	}
	
	public void addSelect( String name )
	{
		select = new Select();
		select.name = firstCharUpper( name );
		form.selectList.add( select );
	}
	
	public void addOption( String value, String text, boolean selected )
	{
		option = new Option();
		option.value = value;
		option.text = text;
		option.selected = selected;
		select.optionList.add( option );
	}
	
	
	static class List
	{
		String id;
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
	
	ArrayList<List> listList = new ArrayList<List>();
	List list;
	ListItem item;
	ListItemParameter itemParam;
	
    public void addList( String id )
    {
    	list = new List();
    	list.id = firstCharUpper( id );
    	listList.add( list );
    }
    
    public void addListItem()
    {
    	item = new ListItem();
    	list.itemList.add( item );
    }
    
    public void addListItemParameter( ListItemParameter.Kind kind, String id )
    {
    	itemParam = new ListItemParameter();
    	itemParam.kind = kind;
    	itemParam.id = firstCharUpper( id );
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
		article.id = firstCharUpper( id );
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
		property.id = id;
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
