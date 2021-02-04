<?php
$csrf = intdiv(time(), 10);
?>
<html>
<head>
<script>
function doPost(){
	var request = new XMLHttpRequest();
	var path="/csrf-header-xhr.php";

	request.open("POST", path, false);
	request.setRequestHeader("csrf", "<?php echo $csrf;?>");
	request.setRequestHeader("Accept","text/plain");


	request.send("account="+document.getElementById("account").value+"&amount="+document.getElementById("amount").value);
	if (request.status === 200) {
		document.getElementById("success").style.display="block";
		document.getElementById("error").style.display="none";		
	}
	else {
		document.getElementById("error").style.display="block";	
		document.getElementById("success").style.display="none";	
	}
}

</script>
</head>
<body>
<p id=error style="display:none"><b>ERROR!</b></p>
<p id=success style="display:none">Success!</p>

<form action="" method="POST">
 <p>Account: <input type="text" id="account" /></p>
 <p>Amount: <input type="text" id="amount" /></p>
 <p><input type="button" onclick="doPost()" value="Submit"/></p>
</form>
</body>
</html>
