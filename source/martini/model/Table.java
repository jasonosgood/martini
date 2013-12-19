package martini.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class 
	Table <P extends Page, H extends TableHeader, R extends TableRow>
implements
	Iterable<R>
{
//	private P _page = null;
//	
//	public void setPage( P page )
//	{
//		_page = page;
//	}
//	
//	public P getPage()
//	{
//		return _page;
//	}

	private H _header = null;
	
	public void setHeader( H header ) 
	{
		_header = header;
	}
	public H getHeader()
	{
		return _header;
	}
	
	private ArrayList<R> _rowList = new ArrayList<R>();
	
	public List<R> getRowList()
	{
		return _rowList;
	}
	
	public void addRow( R row )
	{
		getRowList().add( row );
	}
	
	public Iterator<R> iterator() 
	{
		
		return _rowList.iterator();
	}

}
