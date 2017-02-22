package org.nugroho.heatmap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.logging.Level;

import org.compiere.util.DB;
import org.compiere.util.ValueNamePair;

public class heatmapbarang {
	/**
	 * berisikan data dengan:
	 * kolom 0: id
	 * kolom 1:	jumlah barang
	 * kolom 2: koordinat x
	 * kolom 3: koordinat y
	 */
	public static String[][] heatmapdata;
	
	private static final String FILENAME = "barang.html";
	
	public static ValueNamePair[] coor;
	
	private static String awal = "<!DOCTYPE html>\n" + 
			"<html>\n" + 
			"  <head>\n" + 
			"    <meta charset=\"utf-8\">\n" + 
			"    <title>Heatmaps</title>\n" + 
			"    <style>\n" + 
			"      /* Always set the map height explicitly to define the size of the div\n" + 
			"       * element that contains the map. */\n" + 
			"      #map {\n" + 
			"        height: 100%;\n" + 
			"      }\n" + 
			"      /* Optional: Makes the sample page fill the window. */\n" + 
			"      html, body {\n" + 
			"        height: 100%;\n" + 
			"        margin: 0;\n" + 
			"        padding: 0;\n" + 
			"      }\n" + 
			"      #floating-panel {\n" + 
			"        position: absolute;\n" + 
			"        top: 10px;\n" + 
			"        left: 25%;\n" + 
			"        z-index: 5;\n" + 
			"        background-color: #fff;\n" + 
			"        padding: 5px;\n" + 
			"        border: 1px solid #999;\n" + 
			"        text-align: center;\n" + 
			"        font-family: 'Roboto','sans-serif';\n" + 
			"        line-height: 30px;\n" + 
			"        padding-left: 10px;\n" + 
			"      }\n" + 
			"      #floating-panel {\n" + 
			"        background-color: #fff;\n" + 
			"        border: 1px solid #999;\n" + 
			"        left: 25%;\n" + 
			"        padding: 5px;\n" + 
			"        position: absolute;\n" + 
			"        top: 10px;\n" + 
			"        z-index: 5;\n" + 
			"      }\n" + 
			"    </style>\n" + 
			"  </head>\n" + 
			"\n" + 
			"  <body>\n" + 
			"    <div id=\"floating-panel\">\n" + 
			"      <button onclick=\"toggleHeatmap()\">Toggle Heatmap</button>\n" + 
			"      <button onclick=\"changeGradient()\">Change gradient</button>\n" + 
			"      <button onclick=\"changeRadius()\">Change radius</button>\n" + 
			"      <button onclick=\"changeOpacity()\">Change opacity</button>\n" + 
			"    </div>\n" + 
			"    <div id=\"map\"></div>\n" + 
			"    <script>\n" + 
			"\n" + 
			"      // This example requires the Visualization library. Include the libraries=visualization\n" + 
			"      // parameter when you first load the API. For example:\n" + 
			"      // <script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyAbmdAJwDo_ScrwZDn7PMx1f1h7AMYCIv4&libraries=visualization\">\n" + 
			"\n" + 
			"      var map, heatmap;\n" + 
			"\n" + 
			"      function initMap() {\n" + 
			"        map = new google.maps.Map(document.getElementById('map'), {\n" + 
			"          zoom: 13,\n" + 
			"          center: {lat: 0, lng: 0},\n" + 
			"          mapTypeId: 'satellite'\n" + 
			"        });\n" + 
			"\n" + 
			"        heatmap = new google.maps.visualization.HeatmapLayer({\n" + 
			"          data: getPoints(),\n" + 
			"          map: map\n" + 
			"        });\n" + 
			"      }\n" + 
			"\n" + 
			"      function toggleHeatmap() {\n" + 
			"        heatmap.setMap(heatmap.getMap() ? null : map);\n" + 
			"      }\n" + 
			"\n" + 
			"      function changeGradient() {\n" + 
			"        var gradient = [\n" + 
			"          'rgba(0, 255, 255, 0)',\n" + 
			"          'rgba(0, 255, 255, 1)',\n" + 
			"          'rgba(0, 191, 255, 1)',\n" + 
			"          'rgba(0, 127, 255, 1)',\n" + 
			"          'rgba(0, 63, 255, 1)',\n" + 
			"          'rgba(0, 0, 255, 1)',\n" + 
			"          'rgba(0, 0, 223, 1)',\n" + 
			"          'rgba(0, 0, 191, 1)',\n" + 
			"          'rgba(0, 0, 159, 1)',\n" + 
			"          'rgba(0, 0, 127, 1)',\n" + 
			"          'rgba(63, 0, 91, 1)',\n" + 
			"          'rgba(127, 0, 63, 1)',\n" + 
			"          'rgba(191, 0, 31, 1)',\n" + 
			"          'rgba(255, 0, 0, 1)'\n" + 
			"        ]\n" + 
			"        heatmap.set('gradient', heatmap.get('gradient') ? null : gradient);\n" + 
			"      }\n" + 
			"\n" + 
			"      function changeRadius() {\n" + 
			"        heatmap.set('radius', heatmap.get('radius') ? null : 20);\n" + 
			"      }\n" + 
			"\n" + 
			"      function changeOpacity() {\n" + 
			"        heatmap.set('opacity', heatmap.get('opacity') ? null : 0.2);\n" + 
			"      }\n" + 
			"\n" + 
			"      // Heatmap data: 500 Points\n" + 
			"      function getPoints() {\n" + 
			"        return [\n" + 
			"";
	
	private static String akhir = " ];\n" + 
			"      }\n" + 
			"    </script>\n" + 
			"    <script async defer\n" + 
			"        src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyAbmdAJwDo_ScrwZDn7PMx1f1h7AMYCIv4&libraries=visualization&callback=initMap\">\n" + 
			"    </script>\n" + 
			"  </body>\n" + 
			"</html>";
	
	public static void hitungbarang(){
		ValueNamePair[] lokasi = DB.getValueNamePairs("select m_storage.m_locator_id,count(m_storage.m_locator_id),x,y,z from m_storage join m_locator on (m_storage.m_locator_id = m_locator.m_locator_id) group by m_storage.m_locator_id,x,y,z;", false, null);
		coor = DB.getValueNamePairs("SELECT x,y,z from m_storage join m_locator on (m_storage.m_locator_id = m_locator.m_locator_id) group by m_storage.m_locator_id,x,y,z;", false, null);
		for (ValueNamePair x:lokasi){
			System.out.println(x.getName()+":"+x.getValue());
		}
		heatmapdata = new String[lokasi.length][4];
		for (int i=0;i<lokasi.length;i++){
			heatmapdata[i][0] = lokasi[i].getValue();
			heatmapdata[i][1] = lokasi[i].getName();
			heatmapdata[i][2] = coor[i].getValue();
			heatmapdata[i][3] = coor[i].getName();
		}
	}
	public static void printtest(){
		for (int i=0;i<heatmapdata.length;i++){
			System.out.println(heatmapdata[i][0]+","+heatmapdata[i][1]+","+heatmapdata[i][2]+","+heatmapdata[i][3]);
		}
	}
	
	public static void printhtml(){
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			boolean mulai = true;
			fw = new FileWriter(FILENAME);
			bw = new BufferedWriter(fw);
			bw.write(awal);
			for(int i=0;i<heatmapdata.length;i++){
				int jml = Integer.parseInt(heatmapdata[i][1]);
				for(int j=0;j<jml;j++){
					if(mulai){
						bw.write("new google.maps.LatLng(");
						bw.write(heatmapdata[i][2]+","+heatmapdata[i][3]+")");
					} else {
						bw.write(",new google.maps.LatLng(");
						bw.write(heatmapdata[i][2]+","+heatmapdata[i][3]+")");
					}
					mulai = false;
				}
			}
			bw.write(akhir);
			bw.close();
			System.out.println("selesai printhtml");
		} catch (Exception e){
			System.out.println(e.toString());
		}
	}
}
