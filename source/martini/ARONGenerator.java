package martini;

// TODO: Fix tabs (spacing) now that Page is top (parent) element
// TODO: Add source and timestamp to generated header

import static martini.util.Util.firstCharLower;
import static martini.util.Util.escape;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;

import martini.ModelBuilder.BooleanInput;
import martini.ModelBuilder.Cell;
import martini.ModelBuilder.ListItem;
import martini.ModelBuilder.ListItemParameter;
import martini.ModelBuilder.Row;
import martini.ModelBuilder.TextInput;
import martini.ModelBuilder.Textarea;


public class 
	ARONGenerator 
{
	public void writeModelData( ModelBuilder builder, File source, File targetDir, String pkg, String page )
		throws IOException
	{
		File target = new File( targetDir, page + ".aron" );
		PrintWriter pw = new PrintWriter( target );
		
		println( pw, "# ARON 0.1" );
		println( pw );
		
		printf( pw, "import %s.%s", pkg, page );
		
		for( ModelBuilder.Article article : builder.articleList )
		{
			String articleName = page + article.id;
			printf( pw, "import %s.%sArticle", pkg, articleName );
		}
		
		for( ModelBuilder.Table table : builder.tableList )
		{
			String tableName = page + table.id;
			printf( pw, "import %s.%sTable", pkg, tableName );
			if( table.header != null )
			{
				printf( pw, "import %s.%sTableHeader", pkg, tableName );
			}
			printf( pw, "import %s.%sTableRow", pkg, tableName );
		}
		
		for( ModelBuilder.Form form : builder.formList )
		{
			String formName = page + form.id;
			printf( pw, "import %s.%sForm", pkg, formName );
			if( !form.selectList.isEmpty() )
			{
				printf( pw, "import martini.model.Select" );
				printf( pw, "import martini.model.OptGroup" );
				printf( pw, "import martini.model.Option" );
			}
		}
		
		for( ModelBuilder.List list : builder.rootList.children )
		{
			listImport( pkg, page, pw, list );
		}
		
		pw.println();

		printf( pw, "page:%s", page );
		printParenLeft( pw );
		
		if( builder.title != null )
		{
			printf( pw, "title \"%s\"", builder.title );
		}
		
		for( ModelBuilder.Property property : builder.propertyList )
		{
			printf( pw, "%s \"%s\"", property.id, property.text );
		}

		for( ModelBuilder.Article article : builder.articleList )
		{
			String articleCls = page + article.id + "Article";
			String articleProp = firstCharLower( article.id ) + "Article";
			// TODO: ARON requires parens like "prop Class ()", fix that 
			printf( pw, "%s %s ()", articleProp, articleCls );
		}
		
		for( ModelBuilder.Table table : builder.tableList )
		{
			String tableCls = page + table.id + "Table";
			String tableProp = firstCharLower( table.id ) + "Table";
			printf( pw, "%s %s", tableProp, tableCls );
			printParenLeft( pw );
	
			if( table.header != null )
			{
				String headerCls = tableCls + "Header";
				printf( pw, "header %s ()", headerCls );
			}
			
			printf( pw, "rowList [" );

			for( Row row : table.body.rowList )
			{
				String rowCls = tableCls + "Row";
				printf( pw, rowCls );
				printParenLeft( pw );
				for( Cell cell : row.cellList )
				{
					String key = firstCharLower( cell.id );
					if( cell.value == null ) cell.value = ""; 
					printf( pw, "%s \"%s\"", key, cell.value.trim() );
				}
				printParenRight( pw );
			}
			
			printBracketRight( pw );
			printParenRight( pw );
		}
		
		for( ModelBuilder.Form form : builder.formList )
		{
			String tableCls = page + form.id + "Form";
			String tableProp = firstCharLower( form.id ) + "Form";
			printf( pw, "%s %s", tableProp, tableCls );
			printParenLeft( pw );
			for( ModelBuilder.Input input : form.inputList )
			{
				String key = firstCharLower( input.name );
				if( input instanceof TextInput )
				{
					TextInput text = (TextInput) input;
					String value = text.value;
					if( value == null ) value = "";
					printf( pw, "%s \"%s\"", key, value.trim() );
				}
				else
				if( input instanceof BooleanInput )
				{
					BooleanInput temp = (BooleanInput) input;
					printf( pw, "%s %s", key, temp.value );
				}
			}
	
			for( ModelBuilder.Select select : form.selectList )
			{
				String selectProp = firstCharLower( select.name );
				printf( pw, "%s Select", selectProp );
				printParenLeft( pw );
				printf( pw, "children" );
				printBracketLeft( pw );
				for( ModelBuilder.OptionChild child : select.children )
				{
					if( child instanceof ModelBuilder.Option )
					{
						ModelBuilder.Option option = (ModelBuilder.Option) child;
						printf( pw, "Option" );
						printParenLeft( pw );
						if( option.value != null )
						{
	//						String value = firstCharLower( option.value );
							String value = option.value;
							printf( pw, "value \"%s\"", value );
						}
						String text = option.text;
						if( text == null ) text = "";
						printf( pw, "text \"%s\"", text.trim() );
						
						if( option.selected )
						{
							printf( pw, "selected true" );
						}
						
						if( !option.attribs.isEmpty() )
						{
							printf( pw, "attributes" );
							printSquiggleLeft( pw );
							for( Entry<String, Object> entry : option.attribs.entrySet() )
							{
								printf( pw, "\"%s\" \"%s\"", entry.getKey(), entry.getValue() );
							}
							printSquiggleRight( pw );
						}
						printParenRight( pw );
					}
					else if( child instanceof ModelBuilder.OptGroup )
					{
						ModelBuilder.OptGroup optgroup = (ModelBuilder.OptGroup) child;
						printf( pw, "OptGroup" );
						printParenLeft( pw );
						printf( pw, "label \"%s\"", optgroup.label );
						printf( pw, "children" );
						printBracketLeft( pw );
						for( ModelBuilder.OptionChild grandchild : optgroup.children )
						{
							ModelBuilder.Option option = (ModelBuilder.Option) grandchild;
							printf( pw, "Option" );
							printParenLeft( pw );
							if( option.value != null )
							{
		//						String value = firstCharLower( option.value );
								String value = option.value;
								printf( pw, "value \"%s\"", value );
							}
							String text = option.text;
							if( text == null ) text = "";
							printf( pw, "text \"%s\"", text.trim() );
							
							if( option.selected )
							{
								printf( pw, "selected true" );
							}
							
							if( !option.attribs.isEmpty() )
							{
								printf( pw, "attributes" );
								printSquiggleLeft( pw );
								for( Entry<String, Object> entry : option.attribs.entrySet() )
								{
									printf( pw, "\"%s\" \"%s\"", entry.getKey(), entry.getValue() );
								}
								printSquiggleRight( pw );
							}
							
							printParenRight( pw );
						}
						printBracketRight( pw );

						printParenRight( pw );
					}

				}
				printBracketRight( pw );
				printParenRight( pw );
			}
	
			for( Textarea textarea : form.textareaList )
			{
				String key = firstCharLower( textarea.name );
				String value = textarea.value;
				if( value == null ) value = "";
				// Literal text, do not trim textarea values
				value = escape( value );
				printf( pw, "%s \"%s\"", key, value );
			}
	
			printParenRight( pw );
		}
		
		for( ModelBuilder.List list : builder.rootList.children )
		{
			listData( page, pw, list );
		}
		
		printParenRight( pw );
		pw.close();
	}
	
	public void listImport( String pkg, String page, PrintWriter pw, ModelBuilder.List parent )
		throws IOException
	{
		String listName = page + parent.id;
		printf( pw, "import %s.%sItem", pkg, listName );
		
		for( ModelBuilder.List child : parent.children )
		{
			listImport( pkg, page, pw, child );
			break;
		}
	}

	public void listData( String page, PrintWriter pw, ModelBuilder.List list )
		throws IOException
	{
//		printParenLeft( pw );
		String listCls = page + list.id;
		String itemCls = listCls + "Item";
		String listProp = firstCharLower( list.id );
		printf( pw, listProp );
		printBracketLeft( pw );
		for( ListItem item : list.itemList )
		{
			printf( pw, itemCls );
			printParenLeft( pw );
			for( ListItemParameter param : item.paramList )
			{
				String key = firstCharLower( param.id );
				printf( pw, "%s \"%s\"", key, param.text.trim() );
				if( param.kind == ModelBuilder.ListItemParameter.Kind.A && param.href != null )
				{
					printf( pw, "%s \"%s\"", key + "Href", param.href );
				}
			}
			if( item.text != null )
			{
				printf( pw, "text \"%s\"", item.text.trim() );
				
			}
			for( ModelBuilder.List child : list.children )
			{
				listData( page, pw, child );
				break;
			}
			printParenRight( pw );
		}
		printBracketRight( pw );
		
		
//		printParenRight( pw );
	}
	
	int tabs = 0;
	void indent( PrintWriter pw )
		throws IOException
	{
		for( int nth = 0; nth < tabs; nth++ )
		{
			pw.write( '\t' );
		}
	}
	
	void print( PrintWriter pw, String str )
		throws IOException
	{
		pw.write( str );
	}
	
	void print( PrintWriter pw, char c )
		throws IOException
	{
		pw.write( c );
	}
	
	void println( PrintWriter pw )
		throws IOException
	{
		pw.write( '\n' );
	}
	
	void println( PrintWriter pw, String str )
		throws IOException
	{
		indent( pw );
		pw.write( str );
		println( pw );
	}
	
	void printf( PrintWriter pw, String str, Object... args )
		throws IOException
	{
		indent( pw );
		pw.printf( str, args );
		println( pw );
	}
	
	void printParenLeft( PrintWriter pw )
		throws IOException
	{
		indent( pw );
		pw.write( '(' );
		pw.write( '\n' );
		tabs++;
	}

	void printParenRight( PrintWriter pw )
		throws IOException
	{
		tabs--;
		indent( pw );
		pw.write( ')' );
		pw.write( '\n' );
	}
	
	void printBracketLeft( PrintWriter pw )
		throws IOException
	{
		indent( pw );
		pw.write( '[' );
		pw.write( '\n' );
		tabs++;
	}
	
	void printBracketRight( PrintWriter pw )
		throws IOException
	{
		tabs--;
		indent( pw );
		pw.write( ']' );
		pw.write( '\n' );
	}
	
	void printSquiggleLeft( PrintWriter pw )
		throws IOException
	{
		indent( pw );
		pw.write( '{' );
		pw.write( '\n' );
		tabs++;
			}
	
	void printSquiggleRight( PrintWriter pw )
		throws IOException
	{
		tabs--;
		indent( pw );
		pw.write( '}' );
		pw.write( '\n' );
	}
	
}
