package org.springfield.euscreen.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ItemsRequester extends Thread {

	private HashMap<String, String> items = new HashMap<String, String>();
	private ArrayList<String> users = new ArrayList<String>();
	private String smithersUrl = "http://localhost:8080/smithers2";
	private ArrayList<ItemsObserver> observers = new ArrayList<ItemsObserver>();

	public ItemsRequester() {
		users.add("nisv");
		users.add("dw");
		users.add("lcva");
		users.add("rte");
		users.add("tvc");
		users.add("tvr");
		users.add("ina");
		users.add("nina");
		users.add("orf");
		users.add("sase");
		users.add("kb");
		users.add("nava");
		users.add("ctv");
		users.add("rtp");
		users.add("henaa");
		users.add("dr");
		users.add("rtbf");
		users.add("rai");
		users.add("luce");
		users.add("rtvs");
		users.add("bbc");
		users.add("vrt");
		users.add("tvp");
		start();
	}

	public synchronized void getItems() {
		Iterator<String> i = users.iterator();
		while (i.hasNext()) {
			String user = "eu_" + i.next();
			System.out.println("GET FOR: " + user);
			String userURI = "/domain/euscreenxl/user/"
					+ user;
			String urlStr = smithersUrl + userURI;

			StringBuilder result = new StringBuilder();
			HttpURLConnection conn;
			URL url;
			try {
				url = new URL(urlStr);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");

				BufferedReader rd;
				try {
					rd = new BufferedReader(new InputStreamReader(
							conn.getInputStream()));
				} catch (IOException ioe) {
					rd = new BufferedReader(new InputStreamReader(
							conn.getErrorStream()));
				}
				String line;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				rd.close();
				String xmlStr = result.toString();
				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder;
				try {
					builder = factory.newDocumentBuilder();
					InputSource is = new InputSource(new StringReader(xmlStr));
				    Document doc = builder.parse(is);
				    
				    NodeList videoNodes = doc.getElementsByTagName("video");
				    for(int c = 0; c < videoNodes.getLength(); c++){
				    	Element elem = (Element) videoNodes.item(c);
				    	items.put(elem.getAttribute("id"), userURI + "/video/" + elem.getAttribute("id"));
				    }
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		update();
	}
	
	public void addObserver(ItemsObserver observer){
		this.observers.add(observer);
	}
	
	public void update(){
		System.out.println("UPDATE!!!");
		for(Iterator<ItemsObserver> i = observers.iterator(); i.hasNext();){
			ItemsObserver observer = i.next();
			observer.update(items);
		}
	}

	public void run() {
		getItems();
	}
}
