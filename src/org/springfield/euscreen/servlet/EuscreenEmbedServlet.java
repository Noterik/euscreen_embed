package org.springfield.euscreen.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springfield.euscreen.domain.Video;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EuscreenEmbedServlet extends HttpServlet implements Servlet, ItemsObserver{
	
	private String smithersUrl = "http://localhost:8080/smithers2";
	private Map<String, String> items;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2872527058905236846L;
	private static final String[] HEADERS_TO_TRY = { 
		"X-Forwarded-For",
		"Proxy-Client-IP",
		"WL-Proxy-Client-IP",
		"HTTP_X_FORWARDED_FOR",
		"HTTP_X_FORWARDED",
		"HTTP_X_CLUSTER_CLIENT_IP",
		"HTTP_CLIENT_IP",
		"HTTP_FORWARDED_FOR",
		"HTTP_FORWARDED",
		"HTTP_VIA",
		"REMOTE_ADDR" };
	
	public EuscreenEmbedServlet(){
		System.out.println("LET'S START THE MAGGIE LOADER!");
		new ItemsRequester().addObserver(this);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		System.out.println("EuscreenEmbedServlet.doGet()");
		
		Video video = getVideoFromRequest(request);
		setTicket(video);
		sendTicket(video.getSrc(), getClientIpAddress(request), video.getTicket());
		
        request.setAttribute("video", video);

        RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/embed.jsp");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        
        rd.forward(request, response);
	}
		
	public static String getClientIpAddress(HttpServletRequest request) {
		for (String header : HEADERS_TO_TRY) {
		String ip = request.getHeader(header);
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
		return ip;
		}
		}
		return request.getRemoteAddr();
	}
	
	private static void sendTicket(String videoFile, String ipAddress, String ticket) throws IOException {
		URL serverUrl = new URL("http://82.94.187.227:8001/acl/ticket");
		HttpURLConnection urlConnection = (HttpURLConnection)serverUrl.openConnection();
		
		
		if(videoFile.indexOf(".noterik.com/progressive/") > -1) 
			videoFile = videoFile.substring(videoFile.indexOf("progressive")+11);
			
		Long Sytime = System.currentTimeMillis();
		Sytime = Sytime / 1000;
		String expiry = Long.toString(Sytime+(15*60));
		
		// Indicate that we want to write to the HTTP request body
		
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod("POST");
		videoFile=videoFile.substring(1);
		
		//System.out.println("I send this video address to the ticket server:"+videoFile);
		//System.out.println("And this ticket:"+ticket);
		//System.out.println("And this EXPIRY:"+expiry);
		
		// Writing the post data to the HTTP request body
		BufferedWriter httpRequestBodyWriter = 
		new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		String content = "<fsxml><properties><ticket>"+ticket+"</ticket>"
			+ "<uri>/"+videoFile+"</uri><ip>"+ipAddress+"</ip> "
			+ "<role>user</role>"
			+ "<expiry>"+expiry+"</expiry><maxRequests>1</maxRequests></properties></fsxml>";
		//System.out.println("sending content!!!!"+content);
		httpRequestBodyWriter.write(content);
		httpRequestBodyWriter.close();
		
		// Reading from the HTTP response body
		Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
		while(httpResponseScanner.hasNextLine()) {
			System.out.println(httpResponseScanner.nextLine());
		}
		httpResponseScanner.close();		
	}
	
	private Video getVideoFromRequest(HttpServletRequest request){
		Video video = new Video();
		video.setWidth(request.getParameter("width"));
		video.setHeight(request.getParameter("height"));
		video.setControls(Boolean.parseBoolean(request.getParameter("controls")));
		video.setAutoplay(Boolean.parseBoolean(request.getParameter("autoplay")));
		video.setLoop(Boolean.parseBoolean(request.getParameter("loop")));
		video.setMuted(Boolean.parseBoolean(request.getParameter("muted")));
		video.setSrc(getPath(request.getParameter("id")));
		video.setPoster(request.getParameter("poster"));
		
		return video;
	}
	
	private String getPath(String id){
		System.out.println("getPath(" + id + ")");
		if(items != null && items.containsKey(id)){
			String path = items.get(id);
			String urlStr = this.smithersUrl + path + "/rawvideo/1";
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
				System.out.println("XML: " + xmlStr);
				
				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder;
				try {
					builder = factory.newDocumentBuilder();
					InputSource is = new InputSource(new StringReader(xmlStr));
				    Document doc = builder.parse(is);
				    
				    NodeList extensionNodes = doc.getElementsByTagName("extension");
				    if(extensionNodes.getLength() > 0){
				    	String extension = extensionNodes.item(0).getTextContent();
				    	
				    	NodeList mountNodes = doc.getElementsByTagName("mount");
					    if(mountNodes.getLength() > 0){
					    	String mountNodeContent = mountNodes.item(0).getTextContent();
					    	String mount = mountNodeContent.split(",")[0];
					    	
					    	if (mount.indexOf("http://")==-1) {
					    		return "http://" + mount + ".noterik.com/progressive/" + mount + "/" + path + "/rawvideo/1/raw."+ extension;
							} else if (mount.indexOf(".noterik.com/progressive/") > -1) {
								return mount;
							}
					    	
					    }
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
		return null;
	}
	
	private void setTicket(Video video){
		Random randomGenerator = new Random();
		Integer random= randomGenerator.nextInt(100000000);
		String ticket = Integer.toString(random);
		video.setTicket(ticket);
	}

	@Override
	public void update(Map<String, String> items) {
		// TODO Auto-generated method stub
		this.items = items;
	}

}
