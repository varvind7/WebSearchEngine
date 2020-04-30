package searchEngine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javaFiles.SearchEngine;

/**
 * Servlet implementation class HandleSearch
 */
@WebServlet("/handleSearch")
public class HandleSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HandleSearch() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String[] urlResults = new String[10];
		SearchEngine ptiv = new SearchEngine();
		ptiv.buildIndex(request);
		long startTime = System.currentMilliSeconds();
		for (int i = 0; i < ptiv.find(request.getParameter("searchBox"), request).length; i++)
			urlResults[i] = ptiv.find(request.getParameter("searchBox"), request)[i];
		long endTime = System.currentMilliSeconds();
		System.out.println("Time taken:"+(endTime-startTime));
		request.setAttribute("urlResults", urlResults);
		request.setAttribute("results", request.getParameter("searchBox"));
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/searchResults.jsp");
		requestDispatcher.forward(request, response);
	}
}
