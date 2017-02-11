/******************************************************************************
 * Product: Posterita Ajax UI 												  *
 * Copyright (C) 2007 Posterita Ltd.  All Rights Reserved.                    *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Posterita Ltd., 3, Draper Avenue, Quatre Bornes, Mauritius                 *
 * or via info@posterita.org or http://www.posterita.org/                     *
 *****************************************************************************/

package org.adempiere.webui.panel;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.Tabpanels;
import org.adempiere.webui.component.Tabs;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.event.WTableModelListener;
import org.compiere.apps.search.Info_Column;
import org.compiere.minigrid.ColumnInfo;
import org.compiere.minigrid.IDColumn;
import org.compiere.model.MLocation;
import org.compiere.model.MQuery;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.Util;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;

/**
*	Search Business Partner and return selection
*   Based on InfoBPartner written by Jorg Janke
* 	@author Sendy Yagambrum
* 
* 	Zk Port
* 	@author Elaine
* 	
 * @author Michael McKay, ADEMPIERE-72 VLookup and Info Window improvements
 * 	<li>https://adempiere.atlassian.net/browse/ADEMPIERE-72
* 	@version	InfoBPartner.java Adempiere Swing UI 3.7.1 
*/


public class InfoSearchPanel extends InfoPanel implements EventListener, WTableModelListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5677624151607188344L;

	
	/**
	 *	Standard Constructor
	 *  @param record_id 	ID of current record, if known.  0 otherwise.
	 *  @param queryvalue   Query value Name or Value if contains only numbers
	 *  @param isSOTrx  if false, query vendors only
	 *  @param isSOMatch	Should the customer/vendor only checkbox be checked?
	 *  @param whereClause where clause
	 */
	@Deprecated
	public InfoSearchPanel(int record_id, String value,int windowNo, 
				boolean isSOTrx, boolean isSOMatch, 
				boolean multipleSelection, 
				String whereClause)
	{
		this(windowNo, true, record_id, value, isSOTrx, isSOMatch, multipleSelection, 
				false, whereClause);

	}

	/**
	 *	Standard Constructor
	 *  @param record_id 	ID of current record, if known.  0 otherwise.
	 *  @param queryvalue   Query value Name or Value if contains only numbers
	 *  @param isSOTrx  if false, query vendors only
	 *  @param isSOMatch	Should the customer/vendor only checkbox be checked?
	 *  @param saveResults  True if results will be saved, false for info only
	 *  @param whereClause where clause
	 *  @param modal True if window is opened in modal mode.
	 */
	public InfoSearchPanel(int windowNo, boolean modal, int record_id, String value, 
				boolean isSOTrx, boolean isSOMatch, boolean multipleSelection, 
				boolean saveResults, String whereClause)
	{

		super (windowNo, modal, "C_BPartner", "C_BPartner_ID", multipleSelection, saveResults, whereClause);
		log.info(value + ", WHERE=" + whereClause);
		
		
		setTitle("New Search Panel");
		
		
		m_isSOTrx = isSOTrx;
		m_isSOMatch = isSOMatch;
		//
		StringBuffer where = new StringBuffer();
		where.append("C_BPartner.IsSummary='N' AND C_BPartner.IsActive='Y'");
		if (whereClause != null && whereClause.length() > 0)
			where.append(" AND ").append(whereClause);
		setWhereClause(where.toString());
		setTableLayout(s_Layout);
		setFromClause(s_From);
		setOrderClause(s_Order);
		//
		setShowTotals(true);
		//
        statInit();
		initInfo(record_id, value, false);
        //
        if(autoQuery() || record_id != 0 || (value != null && value.length() > 0 && value != "%"))
        {
        	prepareAndExecuteQuery();
        }
        //
        p_loadedOK = true;
	}

	/** SalesOrder Trx          */
	private boolean 		m_isSOTrx = false;
	private boolean			m_isSOMatch = true;

	private int m_AD_User_ID = -1;
    private int m_C_BPartner_Location_ID = -1;

	/** From Clause             */
	private static String s_From = "C_BPartner";
	/** Order Clause             */
	private static String s_Order = "C_BPartner.Value";

	/**  Array of Column Info    */
	private static Info_Column[] s_Layout = {
		new Info_Column("ID", "C_BPartner.Value", String.class),
		new Info_Column("Search Result", "C_BPartner.Name", String.class)
	};
	
	
	private int fieldID = 0; 
	private Label labelValue ;
	private Textbox fieldValue ;
	private Label labelName;
	private Textbox fieldName ;
	private Label labelContact ;
	private Textbox fieldContact;
	private Label labelEMail ;
	private Textbox fieldEMail;
	private Label labelPostal;
	private Textbox fieldPostal;
	private Label labelPhone;
	private Textbox fieldPhone;
	private Checkbox checkAND ;
	private Checkbox checkCustomer;

	/**	Logger			*/
	protected CLogger log = CLogger.getCLogger(getClass());
	
	private Tabbox detailTabBox = new Tabbox();
	private WListbox contactTbl = ListboxFactory.newDataTable();
	private String m_sqlContact;
	private WListbox addressTbl = ListboxFactory.newDataTable();
	private String m_sqlAddress;
	private int m_C_BPartner_ID = 0;
	private static int ADDRESS_INDEX;

	/**
	 *  Initialize the zk components.
	 */
	private void initComponents()
	{
		
		labelValue = new Label();
		labelValue.setValue("Keyword");
		
		fieldID = 0; // Record_ID
		
		fieldValue = new Textbox();
		fieldValue.setMaxlength(40);
		fieldValue.setAttribute("zk_component_ID", "Lookup_Criteria_fieldValue");
		fieldValue.addEventListener(Events.ON_CHANGE, this);
		
		checkAND = new Checkbox();
		checkAND.setText(Msg.getMsg(Env.getCtx(), "SearchAND"));
		checkAND.setName("SearchAND");
		checkAND.setTooltiptext(Msg.getMsg(Env.getCtx(), "SearchANDInfo"));
		checkAND.setSelected(true);
		checkAND.addActionListener(this);
		checkAND.setAttribute("zk_component_ID", "Lookup_Criteria_checkAND");
		
	}
	
	private void statInit()
	{
		initComponents();

		fieldValue.setWidth("100%");
		
		Rows rows = new Rows();
		
		Row row = new Row();
		rows.appendChild(row);
		row.appendChild(labelValue.rightAlign());
		row.appendChild(fieldValue);
		row.appendChild(checkAND);
        
		statusBar.setEastVisibility(false);
				
		p_criteriaGrid.appendChild(rows);
		super.setSizes();
		
	}	
	
	/**
	 *	Reset the Criteria Info - init with blank data
	 */		
	protected void initInfo()
	{
		initInfo(0,"", true);
	}

	/**
	 *	Dynamic Init
	 *  @param value value
	 *  @param whereClause where clause
	 */
		
	private void initInfo(int record_id, String value, boolean reset)
	{			
	    //
	    if (!(record_id == 0) && value != null && value.length() > 0)
		{
			log.severe("Received both a record_id and a value: " + record_id + " - " + value);
		}
		//  In case of reset, clear all parameters to ensure we are at a known starting point.
		if(reset)
		{
			clearParameters();
		}
		//
	    if (!(record_id == 0))  // A record is defined
	    {
	    	fieldID = record_id;
	    }
	    else
	    {
			if (value != null && value.length() > 0)
			{
				fieldValue.setText(value);
	    	}
			else
			{
				//  Try to find the fieldID from the context
	        	String bp = Env.getContext(Env.getCtx(), p_WindowNo, "C_BPartner_ID");
				if (bp != null && bp.length() != 0)
				{
					fieldID = new Integer(bp).intValue();
				}
			}
	    }
	}	//	initInfo

	/**
	 * A record was selected - take action to sync subordinate tables if any
	 * @param key of the selected record
	 */
	protected void recordSelected(int key)
	{
		m_C_BPartner_ID = key;
		//refresh();
		p_centerSouth.setOpen(true);
		return;
	}
	/**
	 * No record was selected - take action to sync subordinate tables if any
	 */
	protected void noRecordSelected()
	{
		//  Nothing was selected, or the query is empty
		//  - close the panel
		m_C_BPartner_ID = 0;
    	p_centerSouth.setOpen(false);
		return;
	}

    public void onEvent(Event e)
    {
    	// Handle panel specific actions and pass the event to the parent class

		if(!p_loadedOK)
			return;
				
		/*
		Component component = e.getTarget();
		
		if(component != null)
		{
			if (component instanceof Tab) // a tab in the subordinate panel is selected
			{
				refresh();
				return;
			}
		} 
		*/
		
		super.onEvent(e);

    }
	/**
	 * 	Refresh Query
	 */
	protected void refresh()
	{				
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		int leadRowKey = 0;

		if (p_table != null || p_table.getRowCount() > 0)
			leadRowKey = p_table.getLeadRowKey();
    	
		if (m_C_BPartner_ID != leadRowKey)
    	{
			m_C_BPartner_ID = leadRowKey;  //  From the main table
    	}

		if (detailTabBox.getSelectedIndex() == 0)
		{
			//  Contact tab	
			log.finest(m_sqlContact);
			try
			{
				pstmt = DB.prepareStatement(m_sqlContact, null);
				pstmt.setInt(1, m_C_BPartner_ID);
				rs = pstmt.executeQuery();
				contactTbl.loadTable(rs);
				rs.close();
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, m_sqlContact, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
		}
		else
		{
			//  Address tab	
			log.finest(m_sqlAddress);
			try
			{
				pstmt = DB.prepareStatement(m_sqlAddress, null);
				pstmt.setInt(1, m_C_BPartner_ID);
				rs = pstmt.executeQuery();
				addressTbl.loadTable(rs);
				rs.close();
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, m_sqlContact, e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}
	
			String trxName = Trx.createTrxName();
	        for (int row=0; row < addressTbl.getRowCount(); row++)
			{
				int loc_id = 0;
				Object loc_data = addressTbl.getValueAt(row, addressTbl.getKeyColumnIndex());
	            if (loc_data != null && loc_data instanceof IDColumn)
	            {
	            	IDColumn dataColumn = (IDColumn) loc_data;
	        		loc_id = dataColumn.getRecord_ID();
	            }
	
				MLocation loc = MLocation.getBPLocation(Env.getCtx(), loc_id, trxName);
				addressTbl.setValueAt(loc.toString(), row, ADDRESS_INDEX);
			}
			Trx.get(trxName, false).close();
			addressTbl.autoSize();
		}
	}	//	refresh

	/**
	 *	Construct SQL Where Clause and define parameters.
	 *  (setParameters needs to set parameters)
	 *  Includes first AND
	 *  @return WHERE clause
	 */
	protected String getSQLWhere()
	{
		ArrayList<String> list = new ArrayList<String>();
		//  => ID
		if(isResetRecordID())
			fieldID = 0;
		if(!(fieldID == 0))
			list.add("C_BPartner.C_BPartner_ID = ?");
		//	=> Value
		if (isValidSQLText(fieldValue))
			list.add ("UPPER(C_BPartner.Value) LIKE ?");
		
		StringBuffer sql = new StringBuffer();
		int size = list.size();
		//	Just one
		if (size == 1)
			sql.append(" AND ").append(list.get(0));
		else if (size > 1)
		{
			boolean AND = checkAND.isSelected();
			sql.append(" AND ");
			if (!AND)
				sql.append("(");
			for (int i = 0; i < size; i++)
			{
				if (i > 0)
					sql.append(AND ? " AND " : " OR ");
				sql.append(list.get(i));
			}
			if (!AND)
				sql.append(")");
		}

		
		return sql.toString();
	}	//	getSQLWhere

	/**
	 *  Set Parameters for Query.
	 *  (as defined in getSQLWhere)
	 *  @param pstmt pstmt
	 *  @param forCount for counting records
	 *  @throws SQLException
	 */
	protected void setParameters(PreparedStatement pstmt, boolean forCount) throws SQLException
	{
		
		int index = 1;
		//  => ID
		if (!(fieldID == 0))
		{
			pstmt.setInt(index++, fieldID);
			log.fine("Record ID: " + fieldID);
		}
		//	=> Value
		if (isValidSQLText(fieldValue))
		{
			pstmt.setString(index++, getSQLText(fieldValue));
			log.fine("Value: " + fieldValue.getText());
		}
	
	}   //  setParameters
    
    /*************************************************************************/

    /**
     *  Save Selection Details
     *  Get Location/Partner Info
     */
    public void saveSelectionDetail()
    {
        int row = p_table.getSelectedRow();
        if (row == -1)
            return;

        // Elaine 2008/12/16
        if (m_AD_User_ID == -1)
        {
			int leadRowKey = -1;

			if (contactTbl != null || contactTbl.getRowCount() > 0)
				leadRowKey = contactTbl.getLeadRowKey();
	    	
			if (m_AD_User_ID != leadRowKey)
	    	{
				m_AD_User_ID = leadRowKey;  //  From the main table
	    	}
        }
        //
        if (m_C_BPartner_Location_ID == -1)
        {
			int leadRowKey = -1;

			if (addressTbl != null || addressTbl.getRowCount() > 0)
				leadRowKey = addressTbl.getLeadRowKey();
	    	
			if (m_C_BPartner_Location_ID != leadRowKey)
	    	{
				m_C_BPartner_Location_ID = leadRowKey;  //  From the main table
	    	}
        }
        //  publish for Callout to read
        Env.setContext(Env.getCtx(), p_WindowNo, Env.TAB_INFO, "C_BPartner_ID", String.valueOf(m_C_BPartner_ID));
        Env.setContext(Env.getCtx(), p_WindowNo, Env.TAB_INFO, "AD_User_ID", String.valueOf(m_AD_User_ID));
        Env.setContext(Env.getCtx(), p_WindowNo, Env.TAB_INFO, "C_BPartner_Location_ID", String.valueOf(m_C_BPartner_Location_ID));
       
    }   //  saveSelectionDetail
    
    // Elaine 2008/12/16
	/**************************************************************************
	 *	Show History
	 */
	protected void showHistory()
	{
		log.info("");
		Integer C_BPartner_ID = getSelectedRowKey();
		if (C_BPartner_ID == null)
			return;
		InvoiceHistory ih = new InvoiceHistory (this, C_BPartner_ID.intValue(), 
			0, 0, 0);
		ih.setVisible(true);
		ih = null;
	}	//	showHistory

	/**
	 *	Has History
	 *  @return true
	 */
	protected boolean hasHistory()
	{
		return true;
	}	//	hasHistory

	/**
	 *	Zoom
	 */
	public void zoom()
	{
		log.info( "InfoBPartner.zoom");
		Integer C_BPartner_ID = getSelectedRowKey();
		if (C_BPartner_ID == null)
			return;

		MQuery query = new MQuery("C_BPartner");
		query.addRestriction("C_BPartner_ID", MQuery.EQUAL, C_BPartner_ID);
		query.setRecordCount(1);
		int AD_WindowNo = getAD_Window_ID("C_BPartner", true);	//	SO
		AEnv.zoom (AD_WindowNo, query);
	}	//	zoom

	/**
	 *	Has Zoom
	 *  @return true
	 */
	protected boolean hasZoom()
	{
		return true;
	}	//	hasZoom

		
	/**
	 * Does the parameter panel have outstanding changes that have not been
	 * used in a query?
	 * @return true if there are outstanding changes.
	 */
	protected boolean hasOutstandingChanges()
	{
		//  All the tracked fields
		return(
			fieldValue.hasChanged()
			);
			
	}
	/**
	 * Record outstanding changes by copying the current
	 * value to the oldValue on all fields
	 */
	protected void setFieldOldValues()
	{
		fieldValue.set_oldValue();
		return;
	}

    /**
	 *  Clear all fields and set default values in check boxes
	 */
	private void clearParameters()
	{
		//  Clear fields and set defaults
		fieldValue.setText("");
	}

}
