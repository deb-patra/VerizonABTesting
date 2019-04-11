<!DOCTYPE html>
<html lang="en">
	<body>
		<jsp:include page="header.jsp"/>
		<form action="/getGridwall" method="post">
			<section class="container-section">
				<div style="background-color:grey; width:70%">
		        	<h1>Home Page Updated</h1>
			    </div>
			    <div style="background-color:grey; width:70%; font-style: italic; font-size:20px">
			        <p class="oblique">Welcome </p>
			    </div>
			    <div style="background-color:red; width:70%; font-size:20px; font-weight: bold; font-style: oblique">${error}</div>
				<div class="panel-body">
					<p class="oblique">Please enter User Id: </p>
					<input type="text" id="userId" name="userId" value="" />
					<input type="submit" value = "Get Details" class="btn-primary">
				</div>
			</section>
		</form>
	</body>
	<jsp:include page="footer.jsp"/>
</html>