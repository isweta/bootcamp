<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="styles.css">
<title>Read Text</title>
</head>

<body>
	<%
		String paths = "C:\\Users\\Public\\Documents\\files\\src\\MonitorStats\\data2.txt";

		File fileObject = new File(paths);

		BufferedReader br = new BufferedReader(new FileReader(paths));

		String line = null;

		/* line = br.readLine();
		while (line != null) {
			if (line.contains("TimeStamp")) {
				out.println("<br>" + line);
				line=br.readLine();
				out.println("<br>" + line);
				line=br.readLine();
				out.println("<table style='width:100%'>");
				out.println("<tr>");
				String[] ar = line.split("\\s+");
				for (String str : ar) {
					out.println("<td>");
					out.println(str);
					out.println("</td>");
				}
				out.println("</tr>");
				 */
				 
				 out.println(br.readLine())	;
					out.println("<br><b>"+br.readLine()+"</b>")	;	
					out.println("<br><b>"+br.readLine()+"</b>")	;	
		out.println("<table class='vztable'>");
		while ((line = br.readLine()) != null) {
			/* if(line.contains("TimeStamp"))
			{
				
			} */
			out.println("<tr>");
			String[] ar2 = line.split("\\s+");
			int len = ar2.length;

			for (int i = 0; i < len; i++) {
				String str = ar2[i];
				
				
				if (i == len - 1) {
					
					if(str.contains("s")|| str.contains("S") || str.equals(""))
					{
						
						out.println("<td>");
						
					}
					else{
						if(Integer.parseInt(str)>0){
							out.println("<td bgcolor='RED'>");
						
						}
						else
						{
							out.println("<td>");
						}
					}
						
					
						
					
				}
				else{
					out.println("<td>");
				}
				

				out.println(str);
				out.println("</td>");
			}

			out.println("</tr>");
		}

		out.println("</table");
		

		//}
		//}
	%>
</body>
</html>