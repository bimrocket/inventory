<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Downloads IDEBarcelona - File with errors</title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link href='http://fonts.googleapis.com/css?family=Roboto'
	rel='stylesheet' type='text/css'>

	<!-- use the font -->
	<style>
body {
	font-family: 'Roboto', sans-serif;
	font-size: 14px;
}

.activate {
	width: 200px;
	height: 30px;
	font-size: 20px;
	background-color: red;
	color: white;
	display:block;
	padding-top: 15px;
	padding-bottom: 15px;
	text-decoration: none;
}

.not-working {
	font-size: 10px !important;
	color: grey !important;
}
</style>
</head>
<body style="margin: 0; padding: 0;">

	<table align="center" border="0" cellpadding="0" cellspacing="0"
width="600" style="border-collapse: collapse;">
<tr>
	<td align="center" bgcolor="lightgrey" style="padding: 40px 0 30px 0;">
<img src="${imgUrl}logo-diba.png" alt="DIBA"
style="display: block;" />
	</td>
</tr>
<tr>
	<td align="center" style="padding: 40px 30px 40px 30px;">
<p>Dear ${name},</p>
<p>Your download is ready, although there have been errors in generating some of the requested files.</p>
${superatMida}
<p>You can download the files we were able to generate by clicking on the following button:</p> 
<a href="${download_url}" target="_blank" class="activate">DOWNLOAD</a>
<p id="pUrlNavegador" style="width: 600px; word-wrap: break-word; font-family: 'Montserrat', Arial, sans-serif; color: #c3c3c3; font-size: 13px; font-weight: bold; line-height: 1.5em; margin: 10px;">
                	Button not working? Copy and paste the following link into your browser<br />
                   	<a href="${download_url}" target="_blank" style="font-family: 'Montserrat', Arial, sans-serif; color: #c3c3c3; font-size: 13px; font-weight: bold; line-height: 1.5em; text-decoration: none; word-wrap: break-word;"><code> ${download_url} </code></a>
                </p>
	</td>
	
</tr>
<tr>
	<td align="center" bgcolor="lightgrey" style="padding: 40px 0 30px 0;">
<img src="${imgUrl}IDE_Barcelona_Transparent.png" alt="IDE Barcelona"
style="display: block;" />
	</td>
</tr>
	</table>

</body>
</html>