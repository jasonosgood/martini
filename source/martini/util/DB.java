package martini.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// TODO: Keep static list (global singleton) so that all db connections can be verified en masse
public class DB 
{
	private String _driver = null;
	
	public void setDriver( String driver )
	{
		if( driver == null )
		{
			throw new NullPointerException( "driver" );
		}
		_driver = driver;
	}
	
	public String getDriver()
	{
		return _driver;
	}
	
	private String _url = null;

	public void setURL( String url )
	{
		if( url == null )
		{
			throw new NullPointerException( "url" );
		}
		_url = url;
	}
	
	public String getURL()
	{
		return _url;
	}
	
	private String _username = null;
	
	public void setUsername( String username )
	{
		if( username == null )
		{
			throw new NullPointerException( "username" );
		}
		_username = username;
	}
	
	public String getUsername()
	{
		return _username;
	}
	
	private String _password = "";
	
	public void setPassword( String password )
	{
		if( password == null )
		{
			throw new NullPointerException( "password" );
		}
		_password = password;
	}
	
	public String getPassword()
	{
		return _password;
	}
	
	public static void main( String[] args ) 
		throws Exception
	{
		DB db = new DB();
		Connection c = db.getConnection();
	}

	public Connection getConnection()
		throws SQLException, ClassNotFoundException
	{
		Class.forName( _driver );
		Connection connection = DriverManager.getConnection ( _url, _username, _password );
		return connection;
	}
}
