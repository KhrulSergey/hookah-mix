
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

<h1>Tobaccos</h1>

<form action="/tobaccos_list" method="get">
    <input type="text" class="form-control col-sm-2" name="filter" value="${filter}" placeholder="Maker">
    <button type="submit" class="btn btn-primary">Enter</button>
</form>

<form action="/tobaccos_list" method="get">
    <select name="tobaccos" class="form-control selcls col-sm-2">
        <#list tobaccos as tobacco>
            <option value="${tobacco}">${tobacco}</option>
        </#list>
    </select>
</form>

</body>
</html>