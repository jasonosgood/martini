package martini;

// TODO: Fix tabs (spacing) now that Page is top (parent) element
// TODO: Add source and timestamp to generated header

import static martini.util.Util.firstCharLower;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import martini.ModelBuilder.BooleanInput;
import martini.ModelBuilder.Cell;
import martini.ModelBuilder.ListItem;
import martini.ModelBuilder.ListItemParameter;
import martini.ModelBuilder.Row;
import martini.ModelBuilder.TextInput;


public class 
	ARONGenerator 
{
	public void writeModelData( ModelBuilder builder, File source, File targetDir, String pkg, String page )
		throws IOException
	{
		File target = new File( targetDir, page + ".aron" );
		PrintWriter pw = new PrintWriter( target );
		
		pw.println( "# ARON 0.1" );
		pw.println();
		
		pw.printf( "import %s.%s\n", pkg, page );
		
		for( ModelBuilder.Article article : builder.articleList )
		{
			String articleName = page + article.id;
			pw.printf( "import %s.%sArticle\n", pkg, articleName );
		}
		
		for( ModelBuilder.Table table : builder.tableList )
		{
			String tableName = page + table.id;
			pw.printf( "import %s.%sTable\n", pkg, tableName );
			if( table.header != null )
			{
				pw.printf( "import %s.%sTableHeader\n", pkg, tableName );
			}
			pw.printf( "import %s.%sTableRow\n", pkg, tableName );
		}
		
		for( ModelBuilder.Form form : builder.formList )
		{
			String formName = page + form.id;
			pw.printf( "import %s.%sForm\n", pkg, formName );
			if( !form.selectList.isEmpty() )
			{
				pw.printf( "import martini.model.Select\n" );
				pw.printf( "import martini.model.Option\n" );
			}
		}
		
		for( ModelBuilder.List list : builder.listList )
		{
			String listName = page + list.id;
			pw.printf( "import %s.%sItem\n", pkg, listName );
			
		}
		
		pw.println();

		pw.printf( "page:%s\n", page );
		pw.printf( "(\n" );
		
		if( builder.title != null )
		{
			pw.printf( "\ttitle \"%s\"\n", builder.title );
		}
		
		for( ModelBuilder.Property property : builder.propertyList )
		{
			pw.printf( "\t%s \"%s\"\n", property.id, property.text );
		}

		for( ModelBuilder.Article article : builder.articleList )
		{
			String articleCls = page + article.id + "Article";
			String articleProp = firstCharLower( article.id ) + "Article";
			// TODO: ARON requires parens like "prop Class ()", fix that 
			pw.printf( "\t%s %s ()\n", articleProp, articleCls );
		}
		
		for( ModelBuilder.Table table : builder.tableList )
		{
			String tableCls = page + table.id + "Table";
			String tableProp = firstCharLower( table.id ) + "Table";
			pw.printf( "\t%s %s\n", tableProp, tableCls );
			pw.printf( "\t(\n" );
	
			if( table.header != null )
			{
				String headerCls = tableCls + "Header";
				pw.printf( "\t\theader %s ()\n", headerCls );
			}
			
			pw.printf( "\t\trowList [\n" );
			for( Row row : table.body.rowList )
			{
				String rowCls = tableCls + "Row";
				pw.printf( "\t\t\t%s\n", rowCls );
				pw.printf( "\t\t\t(\n" );
				for( Cell cell : row.cellList )
				{
					String key = firstCharLower( cell.id );
					if( cell.value == null ) cell.value = ""; 
					pw.printf( "\t\t\t\t%s \"%s\"\n", key, cell.value.trim() );
				}
				pw.printf( "\t\t\t)\n" );
			}
			
			pw.printf( "\t\t]\n" );
			pw.printf( "\t)\n" );
		}
		
		for( ModelBuilder.Form form : builder.formList )
		{
			String tableCls = page + form.id + "Form";
			String tableProp = firstCharLower( form.id ) + "Form";
			pw.printf( "\t%s %s\n", tableProp, tableCls );
			pw.printf( "\t(\n" );
			for( ModelBuilder.Input input : form.inputList )
			{
				String key = firstCharLower( input.name );
				if( input instanceof TextInput )
				{
					TextInput text = (TextInput) input;
					String value = text.value;
					if( value == null ) value = "";
					pw.printf( "\t\t%s \"%s\"\n", key, value.trim() );
				}
				else
				if( input instanceof BooleanInput )
				{
					BooleanInput temp = (BooleanInput) input;
					pw.printf( "\t\t%s %s\n", key, temp.value );
				}
			}
	
			for( ModelBuilder.Select select : form.selectList )
			{
				String selectProp = firstCharLower( select.name );
				pw.printf( "\t\t%s \n", selectProp );
				pw.printf( "\t\t[\n" );
				for( ModelBuilder.Option option : select.optionList )
				{
					pw.printf( "\t\t\tOption\n" );
					pw.printf( "\t\t\t(\n" );
					if( option.value != null )
					{
						String value = firstCharLower( option.value );
//						if( value == null ) value = "";
						pw.printf( "\t\t\t\tvalue \"%s\"\n", value );
					}
					String text = option.text;
					if( text == null ) text = "";
					pw.printf( "\t\t\t\ttext \"%s\"\n", text.trim() );
					
//					String selected = option.selected ? "true" : "false";
//					pw.printf( "\t\t\t\t\tselected %s\n", selected );
					if( option.selected )
					{
						pw.printf( "\t\t\t\tselected true\n" );
					}
					pw.printf( "\t\t\t)\n" );
				}
				pw.printf( "\t\t]\n" );
			}
	
			pw.printf( "\t)\n" );
		}
		
		for( ModelBuilder.List list : builder.listList )
		{
			String listCls = page + list.id;
			String itemCls = listCls + "Item";
			String listProp = firstCharLower( list.id );
			pw.printf( "\t%s\n", listProp );
			pw.printf( "\t[\n" );
			for( ListItem item : list.itemList )
			{
				pw.printf( "\t\t%s\n", itemCls );
				pw.printf( "\t\t(\n" );
				for( ListItemParameter param : item.paramList )
				{
					String key = firstCharLower( param.id );
					pw.printf( "\t\t\t%s \"%s\"\n", key, param.text.trim() );
					if( param.kind == ModelBuilder.ListItemParameter.Kind.A && param.href != null )
					{
						pw.printf( "\t\t\t%s \"%s\"\n", key + "Href", param.href );
					}
				}
				if( item.text != null )
				{
					pw.printf( "\t\t\ttext \"%s\"\n", item.text.trim() );
					
				}
				pw.printf( "\t\t)\n" );
			}
			pw.printf( "\t]\n" );
			
			
		}
		
		pw.printf( ")\n" );
		pw.close();
	}

}
