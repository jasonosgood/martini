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
        if (propertyName.length() == 0) return null;
        return propertyName.substring( 0, 1 ).toLowerCase() + propertyName.substring( 1 );
    }


}
