

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Panel</title>

    <link rel="stylesheet" href="/static/style.css" />
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
          integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
          crossorigin="anonymous" />

</head>
<body>

<h1>Mixes</h1>

<form action="../catalog_mixes" method="get">
    <select name="mixes">
        <#list mixes as mix>
            <option value="${mix}">${mix}</option>
        </#list>
    </select>
</form>

</body>
</html>