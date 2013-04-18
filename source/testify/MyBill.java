package testify;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import martini.runtime.RedirectException;
import testify.billsummary.LegDetailsSelect;
import testify.billsummary.LegDetailsSelectResultSet;
import testify.billsummary.SelectLegSponsorsByID;
import testify.billsummary.SelectLegSponsorsByIDResultSet;
import testify.billsummary.SelectLegStatusByID;
import testify.billsummary.SelectLegStatusByIDResultSet;

public class
	MyBill
extends 
	Bill
{
	static String driver = "org.h2.Driver";
//	static String url = "jdbc:h2:tcp://localhost/~/firstreading";
	static String url = "jdbc:h2:tcp://localhost/~/Projects/Camper/testify/h2/testify";
	static String username = "sa";
	static String password = "";

	public static Connection getConnection()
		throws SQLException, ClassNotFoundException
	{
		Class.forName( driver );
		Connection connection = DriverManager.getConnection( url, username, password );
		return connection;
	}

	@Override
	public void beforeHandle()
		throws Exception
	{
		int legislationID = 0;
		String biennium = getBienniumParam();
		String billID = "HB 0000";
		int billNumber = Integer.valueOf( getBillNumberParam() );
		String legal = "legal title";
		String desc = "long description";
		int companion = 0;
		
		Connection connection = getConnection();
		LegDetailsSelect details = new LegDetailsSelect( connection );
		details.setBiennium( biennium );
		details.setBillNumber( billNumber );
		LegDetailsSelectResultSet rsDetails = details.getResultSet();
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
			throw new RedirectException( billNotFound );
		}
		details.close();
		
		setTitle( "WA " + biennium + " " + billID );
		setBiennium( biennium );
		setBillID( billID );
		setDesc( desc );
		setLegal( legal  );

		setDisplayCompanion( companion != 0 );
		setCompanion( companion );
		setCompanionHref( "/bill/" + biennium + "/" + companion );
		
		
		List<BillSponsorsItem> sponsors = getSponsors();
		SelectLegSponsorsByID selectSponsor = new SelectLegSponsorsByID( connection );
		selectSponsor.setLegislationID( legislationID );
		SelectLegSponsorsByIDResultSet rsSponsor = selectSponsor.getResultSet();
		while( rsSponsor.hasNext() )
		{
			
			BillSponsorsItem item = new BillSponsorsItem();
			item.setText( rsSponsor.getLastName() );
			sponsors.add( item );
		}
		selectSponsor.close();

		SelectLegStatusByID selectStatus = new SelectLegStatusByID( connection );
		selectStatus.setLegislationID( legislationID );
		BillStatusTable table = getStatusTable();
		SelectLegStatusByIDResultSet rsStatus = selectStatus.getResultSet();
		while( rsStatus.hasNext() )
		{
			BillStatusTableRow row = new BillStatusTableRow();
			row.setDate( rsStatus.getActionDate() );
			row.setDescription( rsStatus.getHistoryLine() );
			table.addRow( row );
		}
		selectStatus.close();
	}
}
