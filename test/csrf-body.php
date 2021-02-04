<?php
$csrf = intdiv(time(), 10);

$csrf_error=false;
$success=false;

if ($_SERVER['REQUEST_METHOD'] === 'POST'){
	//var_dump($_POST);
	if ( intval($_POST["csrf"])!=$csrf)
		$csrf_error =true;
	/*...*/

	if(!$csrf_error /*other tests here*/)

		$success = true;
}
?>
<html>
<body>
<?php if($csrf_error){ ?>
<p><b>ERROR!</b></p>
<?php } else if ($success) {  ?>
<p>Success!</p>
<?php } ?>

<form action="" method="POST">
 <p>Account: <input type="text" name="account" /></p>
 <p>Amount: <input type="text" name="amount" /></p>
 <input type="hidden" name="csrf" value="<?php echo $csrf;?>"/>
 <p><input type="submit" /></p>
</form>
</body>
</html>
