package testify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import martini.model.Handler;
import martini.runtime.RedirectException;
import martini.util.DB;
import testify.billsummary.LegDetailsSelect;
import testify.billsummary.LegDetailsSelectResultSet;
import testify.billsummary.SelectLegSponsorsByID;
import testify.billsummary.SelectLegSponsorsByIDResultSet;
import testify.billsummary.SelectLegStatusByID;
import testify.billsummary.SelectLegStatusByIDResultSet;

public class
	BillHandler
extends 
	Handler<Bill>
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
	public void GET( Bill page, HttpServletRequest request, HttpServletResponse response )
		throws Exception
	{
		int legislationID = 0;
		String biennium = page.getBienniumParam();
		String billID = "HB 0000";
		int billNumber = Integer.valueOf( page.getBillNumberParam() );
		String legal = "legal title";
		String desc = "long description";
		int companion = 0;
		
		Connection connection = getDB().getConnection();
		LegDetailsSelect details = new LegDetailsSelect();
		details.setBiennium( biennium );
		details.setBillNumber( billNumber );
		LegDetailsSelectResultSet rsDetails = details.execute( connection );
		if( rsDetails.hasNext() )
		{
			legislationID = rsDetails.getID();
			companion = rsDetails.getCompanion();
			billID = rsDetails.getBillID();
			legal = rsDetails.getLegalTitle();
			desc = rsDetails.getLongDescription();
		}
		else
		{
			BillNotFound billNotFound = new BillNotFound();
			billNotFound.setBiennium( biennium );
			billNotFound.setBillID( billNumber );
			throw new RedirectException( billNotFound.getURI() );
		}
		rsDetails.close();
		
		page.setTitle( "WA " + biennium + " " + billID );
		page.setBiennium( biennium );
		page.setBillID( billID );
		page.setDesc( desc );
		page.setLegal( legal  );

		page.setDisplayCompanion( companion != 0 );
		page.setCompanion( companion );
		page.setCompanionHref( "/bill/" + biennium + "/" + companion );
		
		
		List<BillSponsorsItem> sponsors = page.getSponsors();
		sponsors.clear();
		SelectLegSponsorsByID selectSponsors = new SelectLegSponsorsByID();
		selectSponsors.setLegislationID( legislationID );
		SelectLegSponsorsByIDResultSet selectSponsorsRS = selectSponsors.execute( connection );
		while( selectSponsorsRS.hasNext() )
		{
			BillSponsorsItem item = new BillSponsorsItem();
			item.setText( selectSponsorsRS.getLastName() );
			sponsors.add( item );
		}
		selectSponsorsRS.close();

		SelectLegStatusByID selectStatus = new SelectLegStatusByID();
		selectStatus.setLegislationID( legislationID );
		BillStatusTable statusTable = page.getStatusTable();
		statusTable.getRowList().clear();
		SelectLegStatusByIDResultSet selectStatusRS = selectStatus.execute( connection );
		while( selectStatusRS.hasNext() )
		{
			BillStatusTableRow row = new BillStatusTableRow();
			row.setDate( selectStatusRS.getActionDate() );
			row.setDescription( selectStatusRS.getHistoryLine() );
			statusTable.addRow( row );
		}
		selectStatusRS.close();
	}
}
