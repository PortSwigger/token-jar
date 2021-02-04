<?php
/*
Token details
Name: csrf
Type: body
Eval: grp[1]
Regex: csrf" value="([0-9]*)"
*/

// Token expires after aprox 10 seconds, meanwhile the value remains valid for multiple requests
$csrf = intdiv(time(), 10);

$csrf_error=false;
$success=false;

if ($_SERVER['REQUEST_METHOD'] === 'PATCH'){
        parse_str(file_get_contents('php://input'), $_PATCH);
        //var_dump($_PATCH);
        //var_dump($_PATCH["csrf"]);
        if ( intval($_PATCH["csrf"])!=$csrf)
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
<?php }
?>

<?php  
if ($_PATCH["csrf"])
        print("[DEBUG] Submited value: ".$_PATCH["csrf"]);
?>

<script>
function submit(){
        account = document.getElementById("account").value;
        ammount = document.getElementById("ammount").value;
        csrf = document.getElementById("csrf").value;
        data = "account="+account+"&ammount="+ammount+"&csrf="+csrf;

        var request = new XMLHttpRequest();

        request.onreadystatechange = function() {
                if (this.readyState == 4 && this.status == 200) {
                        document.getElementsByTagName("body")[0].innerHTML = request.responseText;
                }
        };

        request.open("PATCH","");
        console.log(request.send(data));
}
</script>

<div>
 <p>Account: <input type="text" id="account" value="100"/></p>
 <p>Ammount: <input type="text" id="ammount" value="1"/></p>
 <p>CSRF (normaly hidden): <input type="text" id="csrf" value="<?php echo $csrf;?>"/> </p>
 <p><input type="button" onclick="submit()" value="Send" /></p>
</div>
</body>
</html>
                                                                                                              18,1          Top
