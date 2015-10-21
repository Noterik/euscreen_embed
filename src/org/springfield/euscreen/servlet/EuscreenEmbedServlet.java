package org.springfield.euscreen.servlet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springfield.euscreen.domain.Video;

public class EuscreenEmbedServlet extends HttpServlet implements Servlet{

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
	
	public EuscreenEmbedServlet(){}
	
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
		video.setSrc(request.getParameter("src"));
		video.setPoster(request.getParameter("poster"));
		
		return video;
	}
	
	private void setTicket(Video video){
		Random randomGenerator = new Random();
		Integer random= randomGenerator.nextInt(100000000);
		String ticket = Integer.toString(random);
		video.setTicket(ticket);
	}

}
