package searchEngine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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

/**
 * Servlet implementation class InitializeData
 */
@WebServlet("/initializeData")
public class InitializeData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitializeData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		ServletContext cntxt = getServletContext();
		String fName = "/WEB-INF/"+request.getParameter("fileName");
		InputStream ins = cntxt.getResourceAsStream(fName);
		String content = "";
		if (ins == null) {
			System.out.println("Not found");
		} else {
			BufferedReader br = new BufferedReader((new InputStreamReader(ins)));
			String word;
			String fileName = "file";
			int i = 1;
			int j = 1;
			while ((word = br.readLine()) != null) {
				Connection.Response execute = Jsoup.connect(word).ignoreHttpErrors(true).timeout(100000).execute();
				Document doc = Jsoup.parse(execute.body());
				j++;
				String webInfPath = getServletConfig().getServletContext().getRealPath("/WEB-INF");
				File path = new File(webInfPath + "/newFileFolder");
				if (!path.exists()) {
				    path.mkdirs();
				}
				File newFile = new File(path+"/"+fileName+i+".txt");
				i++;
				FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(word+"\n");
				bw.write(doc.text());
				bw.close();
			}
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/searchPage.jsp");
			requestDispatcher.forward(request, response);
		}
	}

}
