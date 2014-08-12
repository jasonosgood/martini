package blog;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import martini.model.Handler;
import martini.util.DB;
//import martini.runtime.RedirectException;
import blog.SelectRecentPosts;

public class
	BlogHandler
extends 
	Handler<Blog>
{
	private DB _db = null;
	
	public void setDB( DB db )
	{
		_db = db;
	}
	
	public DB getDB()
	{
		return _db;
	}
	
	@Override
	public void GET( Blog page, HttpServletRequest request, HttpServletResponse response )
		throws Exception
	{
		Connection connection = getDB().getConnection();
		SelectRecentPosts recentPosts = new SelectRecentPosts();
		SelectRecentPostsResultSet rs = recentPosts.execute(connection);
		
		List<BlogPostsItem> posts = page.getPosts();
		posts.clear();
		
		while( rs.hasNext() )
		{
			BlogPostsItem item = new BlogPostsItem();
			item.setTitle( rs.getTitle() );
			item.setPublish( rs.getPublish() );
			item.setContent( rs.getContent() );
			posts.add( item );
		}

	}
}
