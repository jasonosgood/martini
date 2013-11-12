package martini;

import static martini.util.Util.firstCharLower;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import lox.Document;
import lox.LOXHandler;
import martini.model.Page;
import martini.util.LineReader;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;

import aron.ARONReader;
import aron.ARONWriter;

public class ModelGenerator 
{

	public static void main( String[] args ) 
		throws Exception
	{
		ModelGenerator main = new ModelGenerator();
		main.go();
	}
	
	public void go()
		throws Exception
	{
		
		File sourceDir = new File( "./html" );
//		File sourceDir = new File( "./html/testify" );
//		File sourceDir = new File( "./demo/test" );
//		File targetDir = new File( "./generated/java" );
		File targetDir = new File( "./generated/java" );
		Stack<String> path = new Stack<String>();
		crawl( sourceDir, targetDir, path );
		
	}
	
	FileFilter htmlFilter = new FileFilter() 
	{
		public boolean accept( File file )
		{
			if( file.isHidden() ) return false;
			if( file.isDirectory() ) return false;
			String name = file.getName();
			if( name.startsWith( "." )) return false;
			if( !name.endsWith( ".html" )) return false;
			return true;
		}
	};
	
	FileFilter dirFilter = new FileFilter() 
	{
		public boolean accept( File file )
		{
			if( file.isHidden() ) return false;
			if( file.getName().startsWith( "." )) return false;
			return file.isDirectory();
		}
	};
	
	public String join( List<String> list, String sep )
	{
		StringBuilder sb = new StringBuilder();
		for( String item : list )
		{
			if( sb.length() > 0 ) sb.append( sep );
			sb.append( item );
		}
		return sb.toString();
	}
	
	public String getFileName( File file )
	{
		String name = file.getName();
		int i = name.indexOf( '.' );
		if( i > -1 )
		{
			name = name.substring( 0, i );
		}
		return name;
	}


	// TODO: Create command line option for this? eg. for a clean build operation
	private boolean _alwaysOverwrite = true;
	
	static enum HttpMethod{ GET, POST, DELETE, UPDATE }
	
	public void crawl( File sourceRoot, File targetRoot, Stack<String> path )
	{
		targetRoot.mkdirs();
		String pkg = join( path, "." );
		File[] sourceList = sourceRoot.listFiles( htmlFilter );
		for( File sourceFile : sourceList )
		{
			try
			{
				// TODO: Sanity check filename suitable for class name
				String name = getFileName( sourceFile );
				File targetFile = new File( targetRoot, name + ".java" );
				long a = sourceFile.lastModified();
				long b = targetFile.lastModified();
				if( _alwaysOverwrite || !targetFile.exists() || a > b )
				{
					Parser tagsoup = new Parser();
					LOXHandler handler = new LOXHandler();
					tagsoup.setContentHandler( handler );
					
					System.out.println( "generating " + targetFile + " (" + sourceFile + ")" );
					FileInputStream in = new FileInputStream( sourceFile );
					InputSource inputSource = new InputSource( in );
					tagsoup.parse( inputSource );
					Document doc = handler.getDocument();
					
					Gleen gleen = new Gleen();
					gleen.go( doc );
					ModelBuilder builder = gleen._builder; 
					
					File requestFile = new File( sourceRoot, name + ".request.http" );
					if( requestFile.exists() )
					{
						LineReader reader = new LineReader( requestFile );
						for( String line : reader )
						{
							line = line.trim();
							if( line.length() == 0 ) break;
							line = line.replaceAll( "\\s+", " " );
							Iterator<String> data = Arrays.asList( line.split( " " )).iterator();
							
							if( !data.hasNext() )
							{
								// empty line, skip it
								break;
							}

							String ugh = data.next();
							HttpMethod method = HttpMethod.valueOf( ugh );
							if( method == null )
							{
								System.out.printf( "HTTP method '%s' not supported in line '%s'\n", ugh, line );
								break;
							}
							
							if( !data.hasNext() )
							{
								System.out.printf( "route '%s' is missing a URL\n" );
								break;
							}
							
							String uri = data.next();
							builder.uri = uri;
							for( String atom : uri.split( "/" ))
							{
								// Is this an error? eg "/abc//xyz"
								if( atom.length() == 0 ) continue;
								if( atom.charAt( 0 ) == '{' && atom.charAt( atom.length() - 1 ) == '}' )
								{
									atom = atom.substring( 1, atom.length() - 1 );
									builder.addURLParam( atom );
								}
							}
						}						
					}
					
					writePage( builder, doc, targetFile, pkg, name );
					writeModels( builder, targetRoot, pkg, name );
					ARONGenerator aron = new ARONGenerator();
					aron.writeModelData( builder, sourceFile, targetRoot, pkg, name );
//					ARONWriter writer = new ARONWriter( System.out );
//					writer.write( builder );
					
				}
				else
				{
					System.out.println( targetFile + " is current" );
				}
			}
			catch( Exception e )
			{
				System.out.println( "error processing : " + sourceFile );
				e.printStackTrace();
			}
		}
		
		File[] childDirList = sourceRoot.listFiles( dirFilter );
		for( File child : childDirList )
		{
			String name = child.getName();
			path.push( name );
			File targetDir = new File( targetRoot, name );
			crawl( child, targetDir, path );
			path.pop();
		}
	}
	
	public void writePage( ModelBuilder builder, Document doc, File target, String pkg, String cls )
		throws Exception
	{
		PrintWriter writer = new PrintWriter( target );
		PageGenerator coder = new PageGenerator( writer, builder );
		coder.setPackageName( pkg );
		coder.setClassName( cls );
		coder.document( doc );
	}
	
	public void writeModels( ModelBuilder builder, File targetDir, String pkg, String page )
		throws IOException
	{
//		writePageModel( targetDir, builder, pkg, page );
		
		for( ModelBuilder.Table table : builder.tableList )
		{
			writeTableModel( targetDir, table, pkg, page );
			writeTableHeaderModel( targetDir, table, pkg, page );
			writeTableRowModel( targetDir, table, pkg, page );
		}
		
		for( ModelBuilder.Form form : builder.formList )
		{
			writeFormModel( targetDir, form, pkg, page );
		}
		
		for( ModelBuilder.List list : builder.listList )
		{
			writeItemModel( targetDir, list, pkg, page );
		}
		
		for( ModelBuilder.Article article : builder.articleList )
		{
			writeArticleModel( targetDir, article, pkg, page );
		}
	}

	public String getModelImport()
	{
		// Future proofing, for when this class gets renamed
		return martini.model.Table.class.getPackage().getName();
	}
	
	public void writePageModel( File targetDir, ModelBuilder builder, String pkg, String page )
		throws IOException
	{
		String cls = page + "Model";
		
		File target = new File( targetDir, cls + ".java" );
		PrintWriter pw = new PrintWriter( target );
		if( pkg != null )
		{
			pw.printf( "package %s;\n", pkg );
		}
		
		pw.println();
		pw.printf( "import %s.*;\n", getModelImport() );
		pw.printf( "import java.util.List;\n" );
		pw.printf( "import java.util.ArrayList;\n" );
		pw.println();
		
		pw.printf( "public class\n" );
		pw.printf( "	%s\n", cls ); 
		pw.printf( "extends \n" );
		pw.printf( "	Model\n" ); 
		pw.printf( "{\n" );
		
		for( ModelBuilder.Table table : builder.tableList )
		{
			String id = table.id + "Table";
			
			String clazz = page + id;
			String accessor = id;
			String variable = firstCharLower( id );
			pw.printf( "	private %s _%s = null;\n", clazz, variable );
			pw.printf( "	public %s get%s() { return _%s; }\n", clazz, accessor, variable );
			pw.printf( "	public void set%s( %s %s ) { \n", accessor, clazz, variable );
			pw.printf( "		_%s = %s;\n", variable, variable );
			pw.printf( "		_%s.setModel( this );\n", variable );
			pw.printf( "	}\n" );
			pw.println();
		}
		
		for( ModelBuilder.Form form : builder.formList )
		{
			String id = form.id + "Form";
			
			String clazz = page + id;
			String accessor = id;
			String variable = firstCharLower( id );
			pw.printf( "	private %s _%s = null;\n", clazz, variable );
			pw.printf( "	public %s get%s() { return _%s; }\n", clazz, accessor, variable );
			pw.printf( "	public void set%s( %s %s ) { \n", accessor, clazz, variable );
			pw.printf( "		_%s = %s;\n", variable, variable );
			pw.printf( "		_%s.setModel( this );\n", variable );
			pw.printf( "	}\n" );
			pw.println();
		}
		
		for( ModelBuilder.List list : builder.listList )
		{
			String id = list.id;
			
			String clazz = "List<" + page + id + "Item>";
			String method = id;
			String variable = firstCharLower( id );
			pw.printf( "	private %s _%s = new Array%s();\n", clazz, variable, clazz );
			pw.printf( "	public %s get%s() { return _%s; }\n", clazz, method, variable );
			pw.printf( "	public void set%s( %s %s ) { _%s = %s; }\n", method, clazz, variable, variable, variable );
			pw.println();
		}
		
		for( ModelBuilder.Article article : builder.articleList )
		{
			String id = article.id + "Article";
			
			String clazz = page + id;
			String accessor = id;
			String variable = firstCharLower( id );
			pw.printf( "	private %s _%s = null;\n", clazz, variable );
			pw.printf( "	public %s get%s() { return _%s; }\n", clazz, accessor, variable );
			pw.printf( "	public void set%s( %s %s ) { \n", accessor, clazz, variable );
			pw.printf( "		_%s = %s;\n", variable, variable );
			pw.printf( "		_%s.setModel( this );\n", variable );
			pw.printf( "	}\n" );
			pw.println();
		}
		
		pw.printf( "}\n" );
		pw.close();
	}

	public void writeTableModel( File targetDir, ModelBuilder.Table table, String pkg, String page )
		throws IOException
	{
		String tableCls = page + table.id + "Table";
		String rowCls = tableCls + "Row";

		ModelBuilder.Header header = table.header;
		String headerCls = ( header != null ? tableCls : "Table" ) + "Header";
		
		File target = new File( targetDir, tableCls + ".java" );
		PrintWriter pw = new PrintWriter( target );
		if( pkg != null )
		{
			pw.printf( "package %s;\n", pkg );
		}
		
		pw.println();
		pw.printf( "import %s.*;\n", getModelImport() );
		pw.println();
		
		pw.printf( "public class \n" );
		pw.printf( "	%s\n", tableCls ); 
		pw.printf( "extends \n" );
		pw.printf( "	Table<%s, %s, %s>\n", page, headerCls, rowCls ); 
		pw.printf( "{\n" );
		pw.printf( "}\n" );
		pw.close();
	}
	
	public void writeTableHeaderModel( File targetDir, ModelBuilder.Table table, String pkg, String page )
		throws IOException
	{
		ModelBuilder.Header header = table.header;
		if( header == null ) return;
		
		if( header.rowList.size() == 0 ) return;
		ModelBuilder.Row row = header.rowList.get( 0 );
		
		String cls = page + table.id + "TableHeader";
		File target = new File( targetDir, cls + ".java" );
		PrintWriter pw = new PrintWriter( target );
		if( pkg != null )
		{
			pw.printf( "package %s;\n", pkg );
		}
		
		pw.println();
		pw.printf( "import %s.*;\n", getModelImport() );
		pw.println();
		
		pw.printf( "public class \n" );
		pw.printf( "	%s\n", cls ); 
		pw.printf( "extends \n" );
		pw.printf( "	TableHeader\n", cls, cls ); 
		pw.printf( "{\n" );
		for( ModelBuilder.Cell cell : row.cellList )
		{
			writeAccessor( pw, cell.id );
		}
		pw.printf( "}\n" );
		pw.close();
	}
	
	public void writeTableRowModel( File targetDir, ModelBuilder.Table table, String pkg, String page )
		throws IOException
	{
		ModelBuilder.Body body = table.body;
		if( body == null ) return;

		if( body.rowList.size() == 0 ) return;
		ModelBuilder.Row row = body.rowList.get( 0 );
		
		String cls = page + table.id + "TableRow";
		File target = new File( targetDir, cls + ".java" );
		PrintWriter pw = new PrintWriter( target );
		if( pkg != null )
		{
			pw.printf( "package %s;\n", pkg );
		}
		
		pw.println();
		pw.printf( "import %s.*;\n", getModelImport() );
		pw.println();
		
		pw.printf( "public class \n" );
		pw.printf( "	%s\n", cls ); 
		pw.printf( "extends \n" );
		pw.printf( "	TableRow\n", cls, cls ); 
		pw.printf( "{\n" );
		for( ModelBuilder.Cell cell : row.cellList )
		{
			writeAccessor( pw, cell.id  );
		}
		pw.printf( "}\n" );
		pw.close();
	}
	
	public void writeFormModel( File targetDir, ModelBuilder.Form form, String pkg, String page )
		throws IOException
	{
		String formCls = page + form.id + "Form";
		
		File target = new File( targetDir, formCls + ".java" );
		PrintWriter pw = new PrintWriter( target );
		if( pkg != null )
		{
			pw.printf( "package %s;\n", pkg );
		}
		
		pw.println();
		pw.printf( "import %s.*;\n", getModelImport() );
		pw.println();
		
		pw.printf( "public class \n" );
		pw.printf( "	%s\n", formCls ); 
		pw.printf( "extends \n" );
		pw.printf( "	Form<%s>\n", page ); 
		pw.printf( "{\n" );
		// TODO: preserve original order of elements
		for( ModelBuilder.Input input : form.inputList )
		{
			if( !"submit".equals( input.type ) && input.name != null )
			{
				if( "checkbox".equals( input.type ))
				{
					writeAccessorBoolean( pw, input.name );
				}
				else
				{
					writeAccessor( pw, input.name );
				}
			}
		}
		for( ModelBuilder.Select select : form.selectList )
		{
			writeAccessor( pw, "Select", select.name );
		}
		for( ModelBuilder.Textarea textarea : form.textareaList )
		{
			writeAccessor( pw, textarea.name );
		}
		pw.printf( "}\n" );
		pw.close();
	}

	public void writeItemModel( File targetDir, ModelBuilder.List list, String pkg, String page )
		throws IOException
	{
		if( list.itemList.size() == 0 ) return;
		ModelBuilder.ListItem item = list.itemList.get( 0 );
		String klazz = page + list.id + "Item";
		
		File target = new File( targetDir, klazz + ".java" );
		PrintWriter pw = new PrintWriter( target );
		if( pkg != null )
		{
			pw.printf( "package %s;\n", pkg );
		}
		
		pw.println();
		pw.printf( "import %s.*;\n", getModelImport() );
		pw.println();
		
		pw.printf( "public class \n" );
		pw.printf( "	%s\n", klazz ); 
		pw.printf( "extends \n" );
		pw.printf( "	ListItem\n" ); 
		pw.printf( "{\n" );
		
		for( ModelBuilder.ListItemParameter param : item.paramList )
		{
			writeAccessor( pw, param.id );
			
			// TODO: Change this to a Link model
			if( param.kind == ModelBuilder.ListItemParameter.Kind.A && param.href != null )
			{
				writeAccessor( pw, param.id + "Href" );
			}
		}
		pw.printf( "}\n" );
		pw.close();
	}
	
	public void writeArticleModel( File targetDir, ModelBuilder.Article article, String pkg, String page )
		throws IOException
	{
		String klazz = page + article.id + "Article";
		
		File target = new File( targetDir, klazz + ".java" );
		PrintWriter pw = new PrintWriter( target );
		if( pkg != null )
		{
			pw.printf( "package %s;\n", pkg );
		}
		
		pw.println();
		pw.printf( "import %s.*;\n", getModelImport() );
		pw.println();
		
		pw.printf( "public class \n" );
		pw.printf( "	%s\n", klazz ); 
		pw.printf( "extends \n" );
		pw.printf( "	Article<%s>\n", page ); 
		pw.printf( "{\n" );
		pw.printf( "}\n" );
		pw.close();
	}
		
	/**
	 * Assumes name parameter already has first char upper
	 * 
	 * @param pw
	 * @param name
	 * @throws IOException
	 */
	public void writeAccessor( PrintWriter pw, String name )
		throws IOException
	{
		String upper = name;
		String lower = firstCharLower( name );
		
		pw.printf( "	private Object _%s = null;\n", lower );
		pw.printf( "	public Object get%s() { return _%s; }\n", upper, lower );
		pw.printf( "	public void set%s( Object %s ) { _%s = %s; }\n", upper, lower, lower, lower );
		pw.println();
	}
	
	public void writeAccessor( PrintWriter pw, String klass, String name )
		throws IOException
	{
		String upper = name;
		String lower = firstCharLower( name );
		
		pw.printf( "	private %s _%s = new %s();\n", klass, lower, klass );
		pw.printf( "	public %s get%s() { return _%s; }\n", klass, upper, lower );
		pw.printf( "	public void set%s( %s %s ) { _%s = %s; }\n", upper, klass, lower, lower, lower );
		pw.println();
	}
	
	public void writeAccessorBoolean( PrintWriter pw, String name )
		throws IOException
	{
		String upper = name;
		String lower = firstCharLower( name );
		
		pw.printf( "	private boolean _%s = false;\n", lower );
		pw.printf( "	public boolean get%s() { return _%s; }\n", upper, lower );
		pw.printf( "	public void set%s( boolean %s ) { _%s = %s; }\n", upper, lower, lower, lower );
		pw.println();
	}
}
