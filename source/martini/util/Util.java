package martini.util;

public class Util 
{
    public static String firstCharUpper( String text ) 
    {
    	if( text == null ) return null;
        if( text.length() == 0 ) return text;
        return text.substring( 0, 1 ).toUpperCase() + text.substring( 1 );
    }

    public static String firstCharLower( String propertyName ) 
    {
    	if( propertyName == null ) return null;
        if( propertyName.length() == 0 ) return null;
        return propertyName.substring( 0, 1 ).toLowerCase() + propertyName.substring( 1 );
    }

    public static boolean hasText( String text )
    {
    	return text != null && text.length() > 0;
    }

	public static String escape( String text )
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
