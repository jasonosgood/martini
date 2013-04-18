package martini;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class MartiniJetty
{
	public static void main( String[] args ) 
		throws Exception
	{
		Server server = new Server();
		SocketConnector connector = new SocketConnector();
		connector.setPort( 8081 );
		server.setConnectors( new Connector[]{ connector } );
		WebAppContext ctx = new WebAppContext();
		ctx.setServer( server );
		ctx.setContextPath( "/" );
		ctx.setWar( "webapp" );
		server.setHandler( ctx );
		try
		{
			server.start();
			server.join();
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.exit( 100 );
		}
	}
}
