<?php
$csrf = intdiv(time(), 10);

$csrf_error=false;
$success=false;

if ($_SERVER['REQUEST_METHOD'] === 'POST'){
	//var_dump($_SERVER['HTTP']);
	
	$headers = getallheaders();

	if ( intval($headers['csrf'])!=$csrf)
		$csrf_error =true;

	if(!$csrf_error /*other tests here*/)
		http_response_code(200);
	else
		http_response_code(400);
}
?>
