/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
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
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.adempiere.webui.window;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.DatetimeBox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Window;
//import org.adempiere.webui.panel.Component;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.panel.WSchedule;

import org.compiere.model.MAssignmentSlot;
import org.compiere.model.MResourceAssignment;
import org.compiere.model.MRole;
import org.compiere.model.ScheduleUtil;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.TimeUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zk.ui.Component;

import org.adempiere.webui.util.FileManager;



public class InfoSearch extends Window implements EventListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5948901371276429661L;

	/**
	 *  Constructor
	 *  @param mAssignment optional assignment
	 *  @param createNew if true, allows to create new assignments
	 */
	public InfoSearch (MResourceAssignment mAssignment, boolean createNew)
	{
		super();
		
		//setTitle(Msg.getMsg(Env.getCtx(), "InfoSchedule"));
		setTitle("Search");
		
		if (createNew)
			setAttribute("mode", "modal");
		else
			setAttribute("mode", "overlapped");
		this.setWidth("600px");
//		this.setHeight("600px");
		this.setClosable(true);
		this.setBorder("normal");
		this.setStyle("position: absolute");
		
		
		/*
		if (mAssignment == null)
			m_mAssignment = new MResourceAssignment(Env.getCtx(), 0, null);
		else
			m_mAssignment = mAssignment;
		if (mAssignment != null)
			log.info(mAssignment.toString());
		m_dateFrom = m_mAssignment.getAssignDateFrom();
		if (m_dateFrom == null)
			m_dateFrom = new Timestamp(System.currentTimeMillis());
		*/
		
		
		m_createNew = createNew;
		
		try
		{
			init();
			dynInit(createNew);
		}
		catch(Exception ex)
		{
			log.log(Level.SEVERE, "InfoSearch", ex);
		}
		AEnv.showWindow(this);
	}

	
	/**
	 * 	IDE Constructor
	 */
	public InfoSearch()
	{
		this (null, false);
	}

	
	/**	Resource 					*/
	private MResourceAssignment		m_mAssignment;
	/** Date						*/
	private Timestamp		m_dateFrom = null;
	/**	Loading						*/
	private boolean			m_loading = false;
	/**	 Ability to create new assignments	*/
	private boolean			m_createNew;
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(InfoSearch.class);

	private Vbox mainLayout = new Vbox();
	private Grid parameterPanel = GridFactory.newGridLayout();
	
	private Label labelResourceType = new Label();
	
	private Listbox fieldResourceType = new Listbox();
	private Label labelResource = new Label();
	private Listbox fieldResource = new Listbox();
	// Elaine 2008/12/12
	private Button bPrevious = new Button();
	private Label labelDate = new Label();
	private DatetimeBox fieldDate = new DatetimeBox();
	private Button bNext = new Button();
	//
	private ConfirmPanel confirmPanel = new ConfirmPanel(true);

	
	
	// CUSTOM ATTRIBUTES
	private Label labelSearch = new Label();
	private Textbox txtSearch = new Textbox();
	private Button btnSearch = new Button();

	/** The table instance on which tests are to be run. */
	private ListModelTable m_table;
	private WListbox w_listbox;
	
	Vector<Object> data = new Vector<Object>();
	
	//String[] listOfMenu_space;
	
	/**
	 * 	Static Layout
	 * 	@throws Exception
	 */
	private void init() throws Exception
	{
		this.appendChild(mainLayout);
		mainLayout.setHeight("100%");
		mainLayout.setWidth("100%");
		
		/* ORIGINAL CODE
		labelResourceType.setValue(Msg.translate(Env.getCtx(), "S_ResourceType_ID"));
		labelResource.setValue(Msg.translate(Env.getCtx(), "S_Resource_ID"));
		labelDate.setValue(Msg.translate(Env.getCtx(), "Date"));
		
		
		// Elaine 2008/12/12
		bPrevious.setLabel("<");
		bNext.setLabel(">");
		//
		*/
		
		
		// CUSTOM CODE
		labelSearch.setText("Keyword");
		btnSearch.setLabel("Search");
		btnSearch.setName("btnSearch");
		btnSearch.addEventListener(Events.ON_CLICK, this);
		
			
		
		mainLayout.appendChild(parameterPanel);
		
		Rows rows = new Rows();
		rows.setParent(parameterPanel);
		Row row = new Row();
		rows.appendChild(row);
		
		
		/* ORIGIN CODE
		row.appendChild(labelResourceType);
		row.appendChild(fieldResourceType);				
		
		row = new Row();
		rows.appendChild(row);
		row.appendChild(labelResource);
		row.appendChild(fieldResource);
		
		
		// Elaine 2008/12/12
		row = new Row();
		rows.appendChild(row);		
		row.appendChild(labelDate);
		
		
		Hbox hbox = new Hbox();
		hbox.appendChild(bPrevious);
		hbox.appendChild(fieldDate);
		hbox.appendChild(bNext);
		row.appendChild(hbox);
		//
		
		
		mainLayout.appendChild(schedulePane);
		schedulePane.setWidth("100%");
		schedulePane.setHeight("400px");
		*/
		
		
		// CUSTOM CODE
		row.appendChild(labelSearch);
		row.appendChild(txtSearch);
		row.appendChild(btnSearch);
		
		
		// create several rows of data
		int lengthOfRetrievedData = 4;
		String[] ID_RetrievedData = new String[4];
		String[] Result_RetrievedData = new String[4];
		
		ID_RetrievedData[0] = "1";
		ID_RetrievedData[1] = "2";
		ID_RetrievedData[2] = "3";
		ID_RetrievedData[3] = "4";
		
		Result_RetrievedData[0] = "Nugroho";
		Result_RetrievedData[1] = "Kelvin";
		Result_RetrievedData[2] = "Febi";
		Result_RetrievedData[3] = "Hafizh";
		
		
		Vector<String> row_test;
	
	
		for (int idx = 0; idx < lengthOfRetrievedData; idx++) {
			
			row_test = new Vector<String>();
			
			row_test.add(ID_RetrievedData[idx]);
			row_test.add(Result_RetrievedData[idx]);
			
			System.out.println("row_test_ID " + idx + ": " + row_test.get(0));
			System.out.println("row_test_Result " + idx + ": " + row_test.get(1));
			
			// create the data
			data.add(row_test);
			
			for (int idx_t = 0; idx_t <= idx; idx_t++) {
				System.out.println("data " + idx_t + ": " + data.get(idx_t));
			}
			
			//row_test.clear();
			
			System.out.println();
			
		}
		
		// instantiate the model
		m_table = new ListModelTable(data);
		
		// the name of the table columns
		List<String> tab_col = new ArrayList<String>();
		tab_col.add("ID");
		tab_col.add("Search Result");
		
		w_listbox = new WListbox();
		w_listbox.setData(m_table, tab_col);
		
		row = new Row();
		rows.appendChild(row);
	
		row.appendChild(w_listbox);
		
		
		// initialize the spaced document
		//listOfMenu_space = getListOfMenu("C:\\template\\zkwebui\\WEB-INF\\search_DB_space.txt");
		
		
		
		/* ORIGINAL CODE
		Div div = new Div();
		div.appendChild(confirmPanel);
		div.appendChild(statusBar);
		
		mainLayout.appendChild(div);
		
		fieldResourceType.setMold("select");
		fieldResource.setMold("select");
		*/
		
		
	}	//	jbInit

	/**
	 * 	Dynamic Init
	 *  @param createNew if true, allows to create new assignments
	 */
	private void dynInit (boolean createNew) 
	{
		
		/* ORIGINAL CODE
		//	Resource
		fillResourceType();
		fillResource();
		fieldResourceType.addEventListener(Events.ON_SELECT, this);
		fieldResource.addEventListener(Events.ON_SELECT, this);

		//	Date - Elaine 2008/12/12
		fieldDate.setValue(m_dateFrom);
		fieldDate.getDatebox().addEventListener(Events.ON_BLUR, this);
		fieldDate.getTimebox().addEventListener(Events.ON_BLUR, this);
//		fieldDate.addEventListener(Events.ON_BLUR, this);
		bPrevious.addEventListener(Events.ON_CLICK, this);
		bNext.addEventListener(Events.ON_CLICK, this);
		//
		
		
		confirmPanel.addActionListener(Events.ON_CLICK, this);
		if (createNew) {
			Button btnNew = new Button();
	        btnNew.setName("btnNew");
	        btnNew.setId("New");
	        btnNew.setSrc("/images/New24.png");
	        
			confirmPanel.addComponentsLeft(btnNew);			
			btnNew.addEventListener(Events.ON_CLICK, this);
		}
		displayCalendar();
		*/
	
	}	// dynInit
	
	
	/**
	 * Implementation of Levenshtein Distance Computing Algorithm
	 * @param a the first String
	 * @param b the second String
	 * @return the distance of similarity
	 */
    public static int getSimilarityDistance(String a, String b) {

        a = a.toLowerCase();
        b = b.toLowerCase();

        int[] costs = new int[b.length() + 1];

        for (int j = 0; j < costs.length; j++) {
        	costs[j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
        	costs[0] = i;
            int nw = i - 1;

            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);

                nw = costs[j];
                costs[j] = cj;
            }
        }

        return costs[b.length()];

    }
	
    
    public String[] getListOfMenu(String file_loc) {
    	
    	String[] listOfMenu = null;
    	FileManager fm;
    	
    	try {
	    	fm = new FileManager(file_loc);
	    	listOfMenu = fm.OpenFile();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return listOfMenu;
    	
    }
    
    
    public void insertSearchResults(int total_result, String[] final_result) {
    	
    	// create several rows of data
    	int lengthOfRetrievedData = total_result;
    	int counter = 0;
		String[] ID_RetrievedData = new String[lengthOfRetrievedData];
		String[] Result_RetrievedData = new String[lengthOfRetrievedData];
		
		for (int idx = 0; idx < lengthOfRetrievedData; idx++) {	
			ID_RetrievedData[idx] = String.valueOf(idx + 1);
			Result_RetrievedData[idx] = final_result[idx];
		}
			
		
		Vector<String> row_test;
	
	
		for (int idx = 0; idx < lengthOfRetrievedData; idx++) {
			
			row_test = new Vector<String>();
			
			row_test.add(ID_RetrievedData[idx]);
			row_test.add(Result_RetrievedData[idx]);
			
			System.out.println("row_test_ID " + idx + ": " + row_test.get(0));
			System.out.println("row_test_Result " + idx + ": " + row_test.get(1));
			
			// create the data
			data.add(row_test);
			
			for (int idx_t = 0; idx_t <= idx; idx_t++) {
				System.out.println("data " + idx_t + ": " + data.get(idx_t));
			}
			
			System.out.println();
			
		}
	
    }
    
    
    
	public void onEvent(Event event) throws Exception {
		
		/* ORIGINAL CODE
		if (m_loading)
			return;

		if (event.getTarget().getId().equals("Ok"))
			dispose();
		else if (event.getTarget().getId().equals("Cancel"))
			dispose();
		//
		*/
		
		if (event != null) {
			
			Component component = event.getTarget();
	    	
			if (component != null) {
			
				if (component instanceof Button) {
					if (event.getName().equals("onClick")) {
						
						Button btn_tmp = (Button) component;
						
						if (btn_tmp.getName() != null && btn_tmp.getName().equals("btnSearch")) {
							
							// search process
							List<Integer> dist = new ArrayList<Integer>();
							String[] listOfMenu_nospace = getListOfMenu("C:\\template\\zkwebui\\WEB-INF\\search_DB_space.txt");
							String keyword = txtSearch.getValue();
							
							System.out.println("onClick btnSearch: " + keyword);
							
							for (int i = 0; i < listOfMenu_nospace.length; i++) {
								System.out.println(listOfMenu_nospace[i]);
							}
							
							String[] splitted_listOfMenu;
							
					        for (int i = 0; i < listOfMenu_nospace.length; i++) {
					        	
					        	// split
					        	splitted_listOfMenu = listOfMenu_nospace[i].split(" ");
					        	
					        	System.out.println("splitted_listOfMenu " + i + ": " + listOfMenu_nospace[i]);
					        	
					        	for (int j = 0; j < splitted_listOfMenu.length; j++) {
						        	dist.add(getSimilarityDistance(splitted_listOfMenu[j], keyword));
						        
						        	System.out.println("getSimilarityDistance: " + splitted_listOfMenu[j] + " " + getSimilarityDistance(splitted_listOfMenu[j], keyword));
					        	}
					        	
					        	System.out.println("--------------------------------");

					        }

					        Collections.sort(dist);

					        int counter = 0;
					        String[] final_result = new String[listOfMenu_nospace.length];
					        
					        for (int i = 0; i < listOfMenu_nospace.length; i++) {
					        	
					        	// split
					        	splitted_listOfMenu = listOfMenu_nospace[i].split(" ");
					        	
					        	for (int j = 0; j < splitted_listOfMenu.length; j++) {					        	
						            if (getSimilarityDistance(splitted_listOfMenu[j], keyword) == dist.get(0)) {
	
						                System.out.println(splitted_listOfMenu[j] + " : " + listOfMenu_nospace[i]);
						                final_result[counter] = listOfMenu_nospace[i];
						                counter++;
						                
						                break;
						                
						            }
					        	}
					            
					        }
					        
					        insertSearchResults(counter, final_result);

						}
						
					}
				}
				
			}
		}
	}
	
	
	/**************************************************************************
	 * 	Dispose.
	 */
	public void dispose()
	{
		this.detach();
	}	//	dispose

	/*************************************************************************/

}	//	InfoSchedule
