package martini.runtime;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import martini.HTMLBuilder;


public class MarkdownRender
{
	public static void main( String[] args )
	throws Exception
	{
		File source = new File( "./data/wa/2011-12/5000.txt" );
		
		MarkdownRender render = new MarkdownRender();
		HTMLBuilder builder = new HTMLBuilder( System.out );
		render.renderBill( source, builder );
		
		
	}
	public void renderBill( File source, HTMLBuilder builder )
		throws IOException
	{
		FileReader reader = new FileReader( source );
		StringBuilder sb = new StringBuilder();
		boolean needParagraph = true;
		boolean bold = false;
		boolean underline = false;
		boolean strike = false;
	//	builder.element( "p" );
		char c = 0;
		char last = c;
		while( (c = (char) reader.read()) != (char) -1 )
		{
			if( needParagraph )
			{
				builder.element( "p" );
				needParagraph = false;
			}
			switch( c )
			{
				case '*':
				{
					if( sb.length() > 0 )
					{
						builder.text( sb.toString() );
						sb.delete( 0, sb.length() );
					}
					if( bold )
					{
						builder.pop();
					}
					else
					{
						builder.element( "b" );
					}
					bold = !bold;
					break;
				}
				
				case '_':
				{
					if( sb.length() > 0 )
					{
						builder.text( sb.toString() );
						sb.delete( 0, sb.length() );
					}
					if( underline )
					{
						builder.pop();
					}
					else
					{
						builder.element( "u" );
					}
					underline = !underline;
					break;
				}
				
				case '~':
				{
					if( sb.length() > 0 )
					{
						builder.text( sb.toString() );
						sb.delete( 0, sb.length() );
					}
					if( strike )
					{
						builder.pop();
					}
					else
					{
						builder.element( "strike" );
					}
					strike = !strike;
					break;
				}
				
				case '\n':
				case '\r':
				{
					c = '\n';
					if( sb.length() > 0 )
					{
						builder.text( sb.toString() );
						sb.delete( 0, sb.length() );
					}
					
					// Skip blank lines
					if( last != '\n' )
					{
						builder.pop();
						needParagraph = true;
					}
					break;
				}
					
				default:
					sb.append( c );
			}
			last = c;
		}
		if( sb.length() > 0 )
		{
			builder.text( sb.toString() );
			sb.delete( 0, sb.length() );
		}
	}

}
