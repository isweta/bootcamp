<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="styles.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="jquery-1.4.2.js"></script>

<script type="text/javascript">

     var auto = setInterval(    function ()
     {
          $('#score').load('TrafficFlowStats.jsp').fadeIn("slow");
     }, 5000); // refresh every 5000 milliseconds
</script>
</head>
<body>


<h2 CLASS="vzh2">TRAFFIC STATISTICS</h2>

<div id="score"></div>

</body>
</html>