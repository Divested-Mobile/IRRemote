<?php

$qry = $_SERVER['QUERY_STRING'];
$host="org.twinone.irremote://launch?".$qry;
?>

<!DOCTYPE html>
<html>
<head><title>IR Remote</title></head>

<body>
	<script>
	document.location="<?php echo $host; ?>";
	</script>

	<a href="<?php echo $host; ?>">
		Click here if the Twinone IR Remote app has not been opened
	</a>
</body>
</html>
