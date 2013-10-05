/* Copyright 2007 Jason Aaron Osgood
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the 
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
   Boston, MA 02111-1307  USA
   
   Jason Osgood
   jason@jasonosgood.com
   http://code.google.com/p/lox/
   
*/package martini;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import lox.XMLWriter;


/**
 * @author Jason Aaron Osgood
 *
 */

public class
	HTMLBuilder
{
	private Writer _writer;
	private XMLWriter _xmlWriter;
	private java.util.Stack<String> _stack;
	
	private boolean _document = false;
	private boolean _doctype = false;
	private boolean _children = false;
	private boolean _tailed = false;

	public HTMLBuilder()
	{
		this( new PrintWriter( System.out ));
	}

	public HTMLBuilder( OutputStream out )
	{
		this( new PrintWriter( out ));
	}
	
	public HTMLBuilder( Writer writer )
	{
		setXMLWriter( writer );
	}
	
	public void setXMLWriter( Writer writer )
	{
		_writer = writer;
		_xmlWriter = new XMLWriter( writer );
//		_xmlWriter.setPretty( true );
		_stack = new java.util.Stack<String>();
	}
	
	public Writer getWriter()
	{
		return _writer;
	}
	
	public void document()
		throws IOException
	{
		if( !_document )
		{
			_xmlWriter.document();
			_document = true;
		}
		else
		{
			throw new IllegalStateException( "duplicate header" );
		}
	}


	public void doctype( String rootName, String systemID, String publicID )
		throws IOException
	{
		if( rootName == null )
		{
			throw new NullPointerException( "rootName" );
		}
		if( systemID == null )
		{
			throw new NullPointerException( "systemID" );
		}
		if( publicID == null )
		{
			throw new NullPointerException( "publicID" );
		}
		
		if( !_doctype )
		{
			if( !_document )
			{
				document();
			}
			
			_xmlWriter.doctype( rootName, systemID, publicID );
			_doctype = true;
		}
		else
		{
			throw new IllegalStateException( "duplicate doctype" );
		}
	}
	
	public void element( String name )
		throws IOException
	{
		if( name == null )
		{
			throw new NullPointerException( "name" );
		}
		
		if( !_stack.empty() )
		{
			_children = true;
		}
		bracket();
		_xmlWriter.elementStart( name );
		_stack.add( name );
		_children = false;
		_tailed = false;
//		// HTML DTD says these elements always require closing tags
//		if( "p".equalsIgnoreCase( name ) || "script".equalsIgnoreCase( name ))
//		{
//			_children = true;
////			_tailed = true;
//		}
	}
	
	public void hasChildren()
		throws IOException
	{
		_children = true;
		bracket();
	}
	
	public void element( String name, Object text )
		throws IOException
	{
		element( name );
		text( text );
		pop();
	}

	public void attribute( String name, Object value )
		throws IOException
	{
		if( name == null )
		{
			throw new NullPointerException( "name" );
		}
		
		if( !_tailed )
		{
			_xmlWriter.attribute( name, value );
		}
		else
		{
			throw new IllegalStateException( "cannot add Attribute to closed Element" );
		}
	}
	
	public void attribute( String name )
			throws IOException
		{
			if( name == null )
			{
				throw new NullPointerException( "name" );
			}
			
			if( !_tailed )
			{
				_xmlWriter.attribute( name );
			}
			else
			{
				throw new IllegalStateException( "cannot add Attribute to closed Element" );
			}
		}
		
	public void text( Object value )
		throws IOException
	{
		_children = true;
		bracket();
		String top = _stack.peek();
		// TODO: Add "style" and "pre"?
		boolean script = "script".equalsIgnoreCase( top );
		boolean style = "style".equalsIgnoreCase( top );
		_xmlWriter.text( value, !( script || style ) );
	}
	
	public void comment( String value )
		throws IOException
	{
		bracket();
		_xmlWriter.comment( value );
	}
	
	public void processingInstruction( String target, String data )
		throws IOException
	{
		if( target == null )
		{
			throw new NullPointerException( "target" );
		}
		
		bracket();
		_xmlWriter.pi( target, data );
	}
	
	public void cdata( String value )
		throws IOException
	{
		bracket();
		_xmlWriter.cdata( value );
	}
	
	public void whitespace( String value )
		throws IOException
	{
		_xmlWriter.whitespace( value );
	}
	
	private void bracket()
		throws IOException
	{
		if( !_stack.isEmpty() )
		{
			if( !_tailed )
			{
				_xmlWriter.elementStart( _children );
				_tailed = true;
			}
		}
	}
	
	public void pop()
		throws IOException
	{
		String top = _stack.pop();
		if( top != null )
		{
			// HTML DTD says these elements always require closing tags
			// TODO: Complete list of tags
			if( "p".equalsIgnoreCase( top ) || "script".equalsIgnoreCase( top ))
			{
				_children = true;
				bracket();
			}
			
			if( _children )
			{
				_xmlWriter.elementEnd( top );
			}
			else
			{
				_xmlWriter.elementStart( false );
				_tailed = true;
			}
			_children = true;
		}
		
		_xmlWriter.flush();
	}
	
	public void close()
		throws IOException
	{
		while( !_stack.empty() )
		{
			pop();
		}
		_xmlWriter.close();
		_document = false;
		_doctype = false;
		_children = false;
		_tailed = false;
	}
}
