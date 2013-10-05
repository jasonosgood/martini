package martini;

//TODO: Add source and timestamp to generated header

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lox.Attribute;
import lox.CDATA;
import lox.Comment;
import lox.Content;
import lox.DocType;
import lox.Document;
import lox.Element;
import lox.ProcessingInstruction;
import lox.Stack;
import lox.Text;
import lox.Whitespace;

import static martini.util.Util.firstCharLower;
import static martini.util.Util.firstCharUpper;
import static martini.util.Util.hasText;
import static martini.Gleen.getMartiniID;
import static martini.Gleen.getMartiniOptional;

public class PageGenerator 
{
	PrintWriter _writerX = null;
	ModelBuilder _builder = null;
	
	public PageGenerator( PrintWriter writer, ModelBuilder builder )
	{
		_writerX = writer;
		_builder = builder;
	}

	String sourceFileName = null;
	
	
	private String _packageName = null;
	public void setPackageName( String packageName )
	{
		_packageName = packageName;
	}
	
	private String _className = null;
	public void setClassName( String className )
	{
		_className = className;
	}
	
	// Used to record which forms need to be prepopulated
	static class Form
	{
//		enum Kind { input, select };
		String id;
		String name;
//		Kind kind;
		String type;
	}
	
	ArrayList<Form> _formList = new ArrayList<Form>();
	
	void addForm( String id, String name, String type )
	{
		Form form = new Form();
		form.id = id;
		form.name = name;
		form.type = type;
//		form.kind = kind;
		_formList.add( form );
	}
	
	public void document( Document document )
		throws IOException
	{
		if( _packageName != null )
		{
			println( "package " + _packageName + ";" );
		}
		println( "// Generated from " + sourceFileName + " -- DO NOT MODIFY" );
		println();
		println( "import martini.model.Page;" );
		println( "import martini.HTMLBuilder;" );
		println( "import martini.model.*;" );
		println( "import static martini.util.Util.hasText;" );
		println( "import java.util.List;" );
		println( "import java.util.ArrayList;" );
		println( "import java.util.Map;" );
		println( "import javax.servlet.ServletException;" );
		println( "import javax.servlet.http.HttpServletRequest;" );
		println( "import javax.servlet.http.HttpServletResponse;" );
		println( "import java.io.IOException;" );
		println();
		println( "public class " + _className + " extends Page" );
		println( "{" );
		println();
		tabs++;
		
		printf( "@Override" );
		printf( "public String getURI() { return \"%s\"; };", _builder.uri  );
		println();
		
		for( String param : _builder.urlParamList )
		{
			String accessor = firstCharUpper( param );
			String variable = firstCharLower( param );
			printf( "private String _%sParam = null;", variable );
			printf( "public String get%sParam() { return _%sParam; }", accessor, variable );
			printf( "public void set%sParam( String %sParam ) {", accessor, variable );
			printf( "	_%sParam = %sParam;", variable, variable );
			printf( "}" );
			println();
		}

		printf( "@Override" );
		printf( "public void setUrlParams( Map<String,String> params ) {" );
		for( String param : _builder.urlParamList )
		{
			String accessor = firstCharUpper( param );
			printf( "	set%sParam( params.get( \"%s\" ));", accessor, param );
		}
		printf( "}" );
		println();

		for( ModelBuilder.Property property : _builder.propertyList )
		{
			String accessor = firstCharUpper( property.id );
			String variable = firstCharLower( property.id );
			printf( "private Object _%s = null;", variable );
			printf( "public Object get%s() { return _%s; }", accessor, variable );
			printf( "public void set%s( Object %s ) {", accessor, variable );
			printf( "	_%s = %s;", variable, variable );
			printf( "}" );
			println();
			if( property.href != null ) 
			{
				printf( "	private Object _%sHref = null;", variable );
				printf( "	public Object get%sHref() { return _%sHref; }", accessor, variable );
				printf( "	public void set%sHref( Object %sHref ) {", accessor, variable );
				printf( "		_%sHref = %sHref;", variable, variable );
				printf( "	}" );
				println();
			}
		}
		
		for( ModelBuilder.Table table : _builder.tableList )
		{
			String id = table.id + "Table";
			
			String clazz = _className + id;
			String accessor = id;
			String variable = firstCharLower( id );
			printf( "	private %s _%s = null;", clazz, variable );
			printf( "	public %s get%s() { return _%s; }", clazz, accessor, variable );
			printf( "	public void set%s( %s %s ) {", accessor, clazz, variable );
			printf( "		_%s = %s;", variable, variable );
			printf( "		_%s.setPage( this );", variable );
			printf( "	}" );
			println();
		}
		
		for( ModelBuilder.Form form : _builder.formList )
		{
			String id = form.id + "Form";
			
			String clazz = _className + id;
			String accessor = id;
			String variable = firstCharLower( id );
			printf( "	private %s _%s = null;", clazz, variable );
			printf( "	public %s get%s() { return _%s; }", clazz, accessor, variable );
			printf( "	public void set%s( %s %s ) {", accessor, clazz, variable );
			printf( "		_%s = %s;", variable, variable );
			printf( "		_%s.setPage( this );", variable );
			printf( "	}" );
			println();
		}
		
		for( ModelBuilder.List list : _builder.listList )
		{
			String id = list.id;
			
			String clazz = "List<" + _className + id + "Item>";
			String method = id;
			String variable = firstCharLower( id );
			printf( "	private %s _%s = new Array%s();", clazz, variable, clazz );
			printf( "	public %s get%s() { return _%s; }", clazz, method, variable );
			printf( "	public void set%s( %s %s ) { _%s = %s; }", method, clazz, variable, variable, variable );
			println();
		}
		
		for( ModelBuilder.Article article : _builder.articleList )
		{
			String id = article.id + "Article";
			
			String clazz = _className + id;
			String accessor = id;
			String variable = firstCharLower( id );
			printf( "	private %s _%s = null;", clazz, variable );
			printf( "	public %s get%s() { return _%s; }", clazz, accessor, variable );
			printf( "	public void set%s( %s %s ) {", accessor, clazz, variable );
			printf( "		_%s = %s;", variable, variable );
			printf( "		_%s.setPage( this );", variable );
			printf( "	}" );
			println();
		}
		
		for( String id : _builder.optionalElementList )
		{
			String accessor = id;
			String variable = firstCharLower( id );
			printf( "private boolean _%s = true;", variable );
			printf( "public void set%s( boolean %s ) { _%s = %s; }", accessor, variable, variable, variable );
			printf( "public boolean get%s() { return _%s; }", accessor, variable );
			println();
		}
		
		println( "public void handle( HttpServletRequest request, HttpServletResponse response )" );
		println( "\tthrows ServletException, IOException " );
		println( "{" );
		tabs++;
		println( "super.handle( request, response );" );
		println( "setXMLWriter( response.getWriter() );" );
		println( "document();" );
		
		doctype( document.doctype() );
		
		for( Content node : document )
		{
			dispatch( node );
		}
		println( "close();" );
		tabs--;
		println( "}" );
		
		println( "@Override");
		println( "public void populateForm()" );
		println( "{" );
		tabs++;
		println( "if( !hasParameters() ) return;" );
		if( !_formList.isEmpty() )
		{
			for( Form form : _formList ) 
			{
				String method = firstCharUpper( form.name );
				switch( form.type )
				{
					case "text":
					{
						println( "get" + form.id + "Form().set" + method + "( getRequestParameter( \"" + form.name + "\" )); " );
						break;
					}
					case "checkbox":
					case "radio":
					{
						println( "get" + form.id + "Form().set" + method + "( hasRequestParameter( \"" + form.name + "\" )); " );
						break;
					}
					case "select":
					{
						println( "get" + form.id + "Form().get" + method + "().setValue( getRequestParameter( \"" + form.name + "\" )); " );
						break;
					}
					default:
						break;
				}
			}
		}
		tabs--;
		println( "}" );
		
		tabs--;
		println( "}" );
		
		
		_writerX.flush();
		_writerX.close();
		
	}

	public void doctype( DocType doctype  )
		throws IOException
	{
		if( doctype == null ) return;
		println( "doctype( \"" + doctype.rootName() + "\", \"" + doctype.systemID() + "\", \"" + doctype.publicID() + "\" );");
	}

	public void cdata( CDATA cdata )
		throws IOException
	{
		
	}
	

	public void comment( Comment comment )
		throws IOException
	{
		
	}
	
	static enum Kind
	{
		A,
		DIV,
		FORM,
		INPUT,
		LI,
		SPAN, 
		TABLE, 
		THEAD, 
		TBODY, 
		TR, 
		TH, 
		TD,
		UL,
		
//		COMMENT,
		DONTCARE 
	}

	public Kind kind( String name )
	{
		Kind result = Kind.DONTCARE;
		try
		{
			result = Kind.valueOf( name.toUpperCase() );
		}
		catch( Exception e ) {
//			e.printStackTrace();
		}
		return result;
	}
	
	Stack _stack = new Stack();
	
	public void element( Element element )
		throws IOException
	{
		
		Kind current = Kind.DONTCARE;
		
		_stack.push( element );
		
		boolean optional = false;

		if( _stack.match( "**/*[martini:optional]" ))
		{
			optional = true;
			String optionID = firstCharUpper( getMartiniOptional( element ));
			println( "if( get" + optionID + "() )" );
			println( "{" );
			tabs++;
		}
		
		println( "element( \"" + element.name() +  "\" );" );

		for( Attribute attribute : element.attributes() )
		{
			attribute( attribute );
		}
		
		if( _stack.match( "**/article[martini]" ))
		{
			String articleID = firstCharUpper( getMartiniID( element ));
			println( "{" );
			tabs++;
			println( "Article article = get" + articleID + "Article();" );
			println( "HTMLBuilder builder = new HTMLBuilder( response.getWriter() );" );
			println( "article.write( builder );" );
			tabs--;
			println( "}" );
//			println( "pop();" );
			_stack.pop();
			println( "pop();" );
			
			// Short circuit, early return, don't recurse through children
			return;
		}
		
		if( _stack.match( "**/table[martini]/tbody" ))
		{
			String tableID = firstCharUpper( getMartiniID( _stack.peek( 1 ) ));
			String chain = _className + tableID + "TableRow row : get" + tableID + "Table()";
			println( "for( " + chain + " )" );
			println( "{" );
			tabs++;
			current = Kind.TBODY;
		}
		else
		if( _stack.match( "**/ul[martini]" ))
		{
			String listID = firstCharUpper( getMartiniID( element ));
			String chain = _className + listID + "Item item : get" + listID + "()";
			println( "for( " + chain + " )" );
			println( "{" );
			tabs++;
			current = Kind.UL;
		}
		else
		if( _stack.match( "**/form[martini]/**/select[name]" ))
		{
			Element peek = _stack.peek( "form" );
			String formID = firstCharUpper( getMartiniID( peek ));
			String name = element.getAttributeValue( "name" );
			addForm( formID, name, "select" );
			name = firstCharUpper( name );
			String chain = "Option option : get" + formID + "Form().get" + name + "()";
			println( "for( " + chain + " )" );
			println( "{" );
			tabs++;
			
			println( "element( \"option\" );" );
			println( "String value = option.getValue();" );
			println( "if( hasText( value ))" );
			println( "{" );
			println( "\tattribute( \"value\", value );" );
			println( "}" );
			println( "if( option.getSelected() )" );
			println( "{" );
			println( "\tattribute( \"selected\", \"selected\" );" );
			println( "}" );
			println( "text( option.getText() );" );
			println( "pop();" );

			current = Kind.FORM;
		}
		else
		if( _stack.match( "**/form[martini]/**/input[name]" ))
		{
			Element input = _stack.peek();
			String type = input.getAttributeValue( "type" );
			String name = input.getAttributeValue( "name" );
			
			Element form = _stack.peek( "form" );
			String formID = firstCharUpper( getMartiniID( form ));
			
			addForm( formID, name, type );
			
			if( "checkbox".equals( type ))
			{
				// TODO: Remember state for radio and checkbox
				name = firstCharUpper( name );
				String chain = "get" + formID + "Form().get" + name + "()";
				
				println( "if( " + chain + " ) {" );
				println( "\tattribute( \"checked\" );" );
				println( "}" );
			}
		}
		
		for( Content content : element )
		{
			if( content instanceof Element )
			{
				String name = ((Element) content).name();
				if( "option".equals( name )) continue;
			}
			dispatch( content );
		}
		
		switch( current )
		{
			case TBODY:
			case UL:
			case FORM:
				tabs--;
				println( "}");
				break;
			default:
				break;
		}
		_stack.pop();
		println( "pop();" );
		if( optional )
		{
			println( "}" );
			tabs--;
		}
	}
	
	public void attribute( Attribute attribute )
		throws IOException
	{
		String key = attribute.key();
		String value = attribute.value().toString();
		String chain = null;

		if( _stack.match( "**/form[martini]/**/input[name]"))
		{
			// Get current element (parent to this attribute)
			Element element = _stack.peek();
			// TODO handle missing input type
			String type = element.getAttributeValue( "type", "text" ).toLowerCase();
			switch( type )
			{
				case "text":
				{
					if( "value".equalsIgnoreCase( key ))
					{
						Element peek = _stack.peek( "form" );
						String formID = firstCharUpper( getMartiniID( peek ));
						String name = element.getAttributeValue( "name" );
						name = firstCharUpper( name );
						chain = "get" + formID + "Form().get" + name + "()";
					}
					break;
				}
				case "checkbox":
				{
					// do not output, checked attribute is handled elsewhere
					if( "checked".equalsIgnoreCase( key )) return;
					break;
				}
				case "submit":
				{
					// do nothing
					break;
				}
				default:
				{
					break;
				}
			}
		}
		else
		if( _stack.match( "**/form[martini]/**/select[name]/option" ))
		{
			if( "value".equalsIgnoreCase( key ))
			{
				chain = "option.getValue()";
			}
			else
			if( "selected".equalsIgnoreCase( key ))
			{
				chain = "option.getSelected()";
			}
		}
		else
		if( _stack.match( "**/ul[martini]/li/**/a[martini]" ) && "href".equalsIgnoreCase( key ))
		{
			String fieldID = firstCharUpper( getMartiniID( _stack.peek( 0 ) ));
			chain = "item.get" + fieldID + "Href()";
		}
		else
		if( _stack.match( "**/a[martini]" ) && "href".equalsIgnoreCase( key ))
		{
			String fieldID = firstCharUpper( getMartiniID( _stack.peek( 0 ) ));
			chain = "get" + fieldID + "Href()";
		}
		
		if( chain != null )
		{
			println( "attribute( \"" + key + "\", " + chain + " );" );
		}
		else
		{
			println( "attribute( \"" + key + "\", \"" + value.toString() + "\" );" );
		}
	}
	

	public void text( Text text )
		throws IOException
	{
		String chain = null;
		
		if( _stack.match( "html/head/title" ))
		{
			chain = "getTitle()";
		}
		else
		if( _stack.match( "**/table[martini]/thead/tr/th[martini]" ))
		{
			String tableID = firstCharUpper( getMartiniID( _stack.peek( 3 )) );
			String fieldID = firstCharUpper( getMartiniID( _stack.peek( 0 )) );
			chain = "get" + tableID + "Table().getHeader().get" + fieldID + "()"; 
		}
		else
		if( _stack.match( "**/table[martini]/tbody/tr/td[martini]" ))
		{
			String fieldID = firstCharUpper( getMartiniID( _stack.peek( 0 )) );
			chain = "row.get" + fieldID + "()";
		}
		else
		if( 
			_stack.match( "**/ul[martini]/li/**/a[martini]" ) ||
			_stack.match( "**/ul[martini]/li/**/div[martini]" ) ||
			_stack.match( "**/ul[martini]/li/**/span[martini]" )
		)
		{
			String fieldID = firstCharUpper( getMartiniID( _stack.peek( 0 ) ) );
			chain = "item.get" + fieldID + "()";
		}
		else
		if( _stack.match( "**/ul[martini]/li" ))
		{
			Element li = _stack.peek();
			List<Element> children = li.find( "*" );
			if( children.isEmpty() )
			{
				chain = "item.getText()";
			}
		}
		else
		if( _stack.match( "**/form[martini]/**/select[name]/option" ))
		{
			chain = "option.getText()";
		}
		
		else
		if( _stack.match( "**/*[martini]" ))
		{
			Element property = _stack.peek();
			boolean yikes = property.hasChildren();
			List<Element> children = property.find( "*" );
			if( children.isEmpty() )
			{
				String id = firstCharUpper( getMartiniID( property ) );
				chain = "get" + id + "()";
			}
		}
		
		if( chain != null )
		{
			println( "text( " + chain + " );" );
		}
		else
		{
			// TODO escape for Java code generation 
			String value = text.value().toString();
			value = escape( value );
			println( "text( \"" + value + "\" );" );
		}
	}
	

	public void whitespace( Whitespace whitespace )
		throws IOException
	{
		String escaped = escape( whitespace.value() );
		println( "text( \"" + escaped + "\" );" );
		
	}
	
	public void pi( ProcessingInstruction pi )
		throws IOException
	{
		
	}
	
	public void dispatch( Content node ) 
		throws IOException 
	{
		if( node instanceof CDATA )
		{
			cdata( (CDATA) node );
		}
		else
		if( node instanceof Comment )
		{
			comment( (Comment) node );
		}
		else
		if( node instanceof Element )
		{
			element( (Element) node );
		}
		else
		if( node instanceof ProcessingInstruction )
		{
			pi( (ProcessingInstruction) node );
		}
		else
		if( node instanceof Text )
		{
			text( (Text) node );
		}
		else
		if( node instanceof Whitespace )
		{
			whitespace( (Whitespace) node );
		}
	}

	
	int tabs = 0;
	void indent()
		throws IOException
	{
		for( int nth = 0; nth < tabs; nth++ )
		{
			_writerX.write( '\t' );
		}
	}
	
	void print( String str )
		throws IOException
	{
		_writerX.write( str );
	}
	
	void print( char c )
		throws IOException
	{
		_writerX.write( c );
	}
	
	void println()
		throws IOException
	{
		_writerX.write( '\n' );
	}
	
	void println( String str )
		throws IOException
	{
		indent();
		_writerX.write( str );
		println();
	}
	
	void printf( String str, Object... args )
		throws IOException
	{
		indent();
		_writerX.printf( str, args );
		println();
	}
		
	public String escape( String text )
	{
		StringBuilder sb = new StringBuilder( text.length() + 100 );
		char[] ca = text.toCharArray();
		int size = ca.length;
		for( int nth = 0; nth < size; nth++ )
		{
			char c = ca[nth];
			switch( c )
			{
				case '\t':
					sb.append( "\\t" );
					break;
					
				case '\n':
					sb.append( "\\n" );
					break;
				
				case '\r':
					sb.append( "\\r" );
					break;
					
				case '"':
					sb.append( "\\\"" );
					break;
					
				default:
					sb.append( c );
					break;
			}
		}
		return sb.toString();
	}

}
