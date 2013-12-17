package martini;

import static martini.Gleen.getMartiniID;
import static martini.Gleen.getMartiniOptional;
import static martini.util.Util.firstCharUpper;

import java.io.File;
import java.io.FileInputStream;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;

import lox.Content;
import lox.Document;
import lox.Element;
import lox.LOXHandler;
import lox.Stack;
import lox.Text;

// TODO Logging for found matches, to add with usage/debugging

public class 
	Gleen 
{
	public static void main( String[] args )
		throws Exception
	{
		Gleen gleen = new Gleen();
		gleen.go();
	}
	
	public void go()
		throws Exception
	{
		Parser tagsoup = new Parser();
		LOXHandler handler = new LOXHandler();
		tagsoup.setContentHandler( handler );
		
//		File sourceFile = new File( "./html/testify/Main.html" );
		File sourceFile = new File( "./html/testify/Bill.html" );
		FileInputStream in = new FileInputStream( sourceFile );
		InputSource inputSource = new InputSource( in );
		tagsoup.parse( inputSource );
		Document document = handler.getDocument();
		go( document );
	}
	
	public void go( Document document )
		throws Exception
	{
		Element title = document.findFirst( "html/head/title" );
		String text = title.getText();
		_builder.title = text;
		gleen( document.getRoot() );
	}
	
	ModelBuilder _builder = new ModelBuilder();
	Stack _stack = new Stack();
	
	public void gleen( Element parent )
	{
		_stack.push( parent );
		boolean recurse = true;

		if( _stack.match( "**/*[martini:optional]" ))
		{
			String optionID = firstCharUpper( getMartiniOptional( parent ));
			_builder.addOptionalElement( optionID );
		}
		
		if( _stack.match( "**/article[martini]" ))
		{
			String id = getMartiniID( parent );
			_builder.addArticle( parent, id );
		}
		else
		if( _stack.match( "**/table[martini]" ))
		{
			recurse = false;
			table( parent );
		}
		else
		if( _stack.match( "**/form[martini]" ))
		{
			recurse = false;

			String formID = getMartiniID( parent );
			_builder.addForm( formID );

			form( parent );
		}
		else
		if( _stack.match( "**/ul[martini]" ))
		{
			recurse = false;
			list( parent );
		}
		else
		if( _stack.match( "**/*[martini]" ))
		{
			recurse = false;
			property( parent );
			
		}
		
		if( recurse )
		{
			for( Content node : parent )
			{
				if( node instanceof Element )
				{
					Element element = (Element) node;
					gleen( element );
				}
			}
		}
		_stack.pop();
	}

	private void table( Element element ) 
	{
		String id = getMartiniID( element );
		_builder.addTable( id );
		
		for( Element row : element.find( "thead/tr" ))
		{
			_builder.addHeader();
			_builder.addRow();
			for( Element cell : row.find( "th[martini]" ))
			{
				String cellID = getMartiniID( cell );
				String value = cell.getText();
				_builder.addCell( cellID, value );
			}
			break; // We only want the first thead/tr found
		}
		
		for( Element tbody : element.find( "tbody" ))
		{
			_builder.addBody();
			for( Element row : tbody.find( "tr" ))
			{
				_builder.addRow();
				for( Element cell : row.find( "td[martini]" ) )
				{
					String cellID = getMartiniID( cell );
					String value = cell.getText();
					_builder.addCell( cellID, value );
				}
			}
			
			removeAllButFirst( tbody, "tr" );
			// Ignore extra TBODY elements
			break; 
		}
	}

	public void form( Element parent ) 
	{
		for( Content x : parent )
		{
			if( x instanceof Element )
			{
				Element child = (Element) x;
				if( "input".equalsIgnoreCase( child.name() )) 
				{
					String type = child.getAttributeValue( "type" );
					String name = child.getAttributeValue( "name" );
					switch( type )
					{
						case "text":
						case "hidden":
						case "password":
						{
							String value = child.getAttributeValue( "value" );
							if( name == null )
							{
								System.out.println( "ignoring <input>, missing \"name\" attribute\n" + child.toString() );
							}
//							else 
								if( value == null )
							{
								System.out.println( "ignoring <input>, missing \"value\" attribute\n" + child.toString() );
							}
//							else
							{
								_builder.addTextInput( type, name, value );
							}
							
							break;
						}
						case "checkbox":
						{
							boolean checked = child.hasAttribute( "checked" );
							_builder.addBooleanInput( type, name, checked );
							break;
						}
						case "submit":
						{
							_builder.addSubmitInput( type, name );
							break;
						}
						default:
							break;
					}
					continue;
				}
				
				if( "select".equalsIgnoreCase( child.name() )) 
				{
					String name = child.getAttributeValue( "name" );
					if( name == null )
					{
						System.out.println( "ignoring <select>, missing \"name\" attribute\n" + child.toString() );
					}
					else
					{
						_builder.addSelect( name );
						for( Element option : child.find( "option" ))
						{
							String text = option.getText();
							String value = option.getAttributeValue( "value" );
							boolean selected = option.hasAttribute( "selected" );
							_builder.addOption( value, text, selected );
						}
						
						removeAllButFirst( child, "option" );
					}
					continue;
				}
				
				if( "textarea".equalsIgnoreCase( child.name() )) 
				{
					String name = child.getAttributeValue( "name" );
					if( name == null )
					{
						System.out.println( "ignoring <textarea>, missing \"name\" attribute\n" + child.toString() );
					}
					else
					{
						StringBuilder sb = new StringBuilder();
						for( Content y : child )
						{
							sb.append( y.toString() );
						}
						_builder.addTextarea( name, sb.toString() );
					}
					continue;
				}
				
				
				form( child );
			}
		}
	}
	
	public void list( Element list )
	{
		String listID = getMartiniID( list );
		_builder.addList( listID );
		for( Element item : list.find( "li" ))
		{
			_builder.addListItem();
			
			for( Element div : item.find( "**/div[martini]" ))
			{
				String id = getMartiniID( div );
				// TODO Print error message
				if( id == null ) continue;
				String text = div.getText();
			
				_builder.addListItemParameter( ModelBuilder.ListItemParameter.Kind.DIV, id );
				_builder.itemParam.text = text;
			}
			
			for( Element span : item.find( "**/span[martini]" ))
			{
				String id = getMartiniID( span );
				// TODO Print error message
				if( id == null ) continue;
				String text = span.getText();
			
				_builder.addListItemParameter( ModelBuilder.ListItemParameter.Kind.SPAN, id );
				_builder.itemParam.text = text;
			}
			
			for( Element a : item.find( "**/a[martini]" ))
			{
				String id = getMartiniID( a );
				// TODO Print error message
				if( id == null ) continue;
				String href = a.getAttributeValue( "href" );
				String text = a.getText( false );
			
				_builder.addListItemParameter( ModelBuilder.ListItemParameter.Kind.A, id );
				_builder.itemParam.text = text;
				_builder.itemParam.href = href;
			}
			
			if( _builder.item.paramList.size() == 0 )
			{
				_builder.item.text = item.getText();
			}
		}
		
		removeAllButFirst( list, "li" );
	}
	
	public static String getMartiniID( Element element )
	{
		return element.getAttributeValue( "martini" );
	}
	
	public static String getMartiniOptional( Element element )
	{
		return element.getAttributeValue( "martini:optional" );
	}
	
	public void property( Element parent )
	{
		String id = getMartiniID( parent );
		String text = parent.getText();
		String href = parent.getAttributeValue( "href" );
		_builder.addProperty( id, text, href );
	}
	
	void removeAllButFirst( Element parent, String expr )
	{
		Element first = parent.findFirst( expr );
		parent.clear();
		parent.add( first );
	}
}
