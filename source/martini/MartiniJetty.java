package martini;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class MartiniJetty
{
	public static void main( String[] args ) 
		throws Exception
	{
		Server server = new Server( 8081 );
		WebAppContext webapp = new WebAppContext();
//		webapp.setServer( server );
		webapp.setContextPath( "/school" );
		webapp.setWar( "webapp" );
		server.setHandler( webapp );
		server.start();
		server.join();
	}
}
