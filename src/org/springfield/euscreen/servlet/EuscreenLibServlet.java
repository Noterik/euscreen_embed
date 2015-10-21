package org.springfield.euscreen.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EuscreenLibServlet extends HttpServlet implements Servlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EuscreenLibServlet(){}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		System.out.println("EuscreenEmbedServlet.doGet()");
		
        RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/js/embedlib.jsp");
        StringBuffer url = request.getRequestURL();
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        
        String base = url.substring(0, url.length() - uri.length() + ctx.length()) + "/";
        request.setAttribute("serverAddress", base);
        rd.forward(request, response);
	}
}
