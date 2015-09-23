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

        <title>Read Text</title>
</head>
 
<body>
<%
               String paths="/Users/sdnuser/Documents/workspace/WebApp/ad.txt"; 


                File fileObject = new File(paths);

                BufferedReader br = new BufferedReader(new FileReader(paths));

                String line1 = null, line2 = null;
                
                while((line1 =br.readLine())!= null) {
                    
                    out.println(line1);
                }
            %>
</body>
</html>