<!DOCTYPE html>
<html lang="en">
	<body>
		<jsp:include page="header.jsp"/>
		<section class="container-section">
			<p>Email has been sent to the following emailIds</p>
			<div class="panel-body">
		    	<table class="table table-hover" border="1" >
		         <thead class="thead-inverse">
		         <tr>
		             <th>Email Ids</th>
		         </tr>
		         </thead>
		         <tr>
		             <td>${emailList}</td>
		         </tr>
		        </table>
		    </div>
		</section>
	</body>
	<jsp:include page="footer.jsp"/>
</html>