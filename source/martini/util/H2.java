package martini.util;

import org.h2.tools.Server;

public class H2
{
	public static void main( String[] args ) throws Exception
	{
		start();
		// stop();
		// Server server = Server.createTcpServer( args );
		// server.start();
		// System.out.println( server.getURL() );
		// System.out.println( server.isRunning() );
	}

	private static boolean _tcpStarted = false;
	private static boolean _webStarted = false;

	public static void start()
	{
		Server server = null;

		if( !_tcpStarted )
		{
			try
			{
				server = Server.createTcpServer( new String[]{ "-tcp", "-tcpAllowOthers", "true" } );
				server.start();
				System.out.println( "Started H2 TCP Server: " + server.getURL() );

				_tcpStarted = true;
			}
			catch( Exception e )
			{
				System.out.println( "H2 TCP Server is already running" );
			}
		}

		if( !_webStarted )
		{
			try
			{
				server = Server.createWebServer( new String[]{} );
				server.start();
				System.out.println( "Started H2 Web Server: " + server.getURL() );

				_webStarted = true;
			}
			catch( Exception e )
			{
				System.out.println( "H2 Web Server is already running" );
			}
		}
	}

	public static void stop()
	{
		Server server = null;

		if( _tcpStarted )
		{
			try
			{
				server = Server.createTcpServer( new String[]{} );
				server.stop();
				System.out.println( "Stopped H2 TCP Server: " + server.getURL() );

				_tcpStarted = false;
			}
			catch( Exception e )
			{
				System.out.println( "H2 TCP Server could not be stopped:" );
				e.printStackTrace( System.err );
			}
		}

		if( _webStarted )
		{
			try
			{
				server = Server.createWebServer( new String[]{} );
				try
				{
					server.start();
				}
				catch( RuntimeException e )
				{
					e.printStackTrace( System.err );
				}
				server.stop();
				System.out.println( "Stopped H2 Web Server: " + server.getURL() );

				_webStarted = false;
			}
			catch( Exception e )
			{
				System.out.println( "H2 Web Server could not be stopped:" );
				e.printStackTrace( System.err );
			}
		}

	}
}
