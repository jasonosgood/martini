package martini.model;

import java.io.StringWriter;

import aron.ARONWriter;

public class SelectSerial
{

	public static void main( String[] args )
		throws Exception
	{
		Select select = new Select();
		Option option1 = new Option();
		option1.setText( "harsh" );
		OptGroup group1 = new OptGroup();
		group1.setLabel( "taste" );
		Option option2 = new Option();
		option2.setText( "cream" );
//		select.children.add( option1 );
//		select.children.add( group1 );
//		group1.children.add( option2 );
		
		StringWriter sw = new StringWriter();
		ARONWriter aw = new ARONWriter( sw );
		
		aw.write( select );
		System.out.println( sw.toString() );
		

	}

}
