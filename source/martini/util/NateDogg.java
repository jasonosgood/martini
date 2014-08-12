package martini.util;

import java.net.URLDecoder;
import java.util.HashMap;

public class NateDogg {

	/**
	 * @param args
	 */
	public static void main( String[] args )
		throws Exception
	{
		String payload = "campus=Seattle&college=Arts+%26+Sciences&dept=Arts&abbrev=&number=&title=&transcript=&credits=Fixed&creditsMin=&creditsMax=&terms=Autumn&grading=Standard&format=Lecture&startYear=&startTerm+=Autumn+%281%29&endYear=&endTerm=Autumn+%281%29";
		
		HashMap<String,String> map = new HashMap<String,String>();
		String[] stuff = payload.split( "&" );
		for( String item : stuff )
		{
			String[] pair = item.split( "=" );
			String key = pair[0];
			key = URLDecoder.decode( key, "UTF-8" ).trim();
			
			if( pair.length > 1 )
			{
			
				String value = pair.length > 1 ? pair[1] : "";
				value = URLDecoder.decode( value, "UTF-8" );
				System.out.printf( "\n%s = %s", key, value );
				map.put( key, value );
			}
		}
	}

}
