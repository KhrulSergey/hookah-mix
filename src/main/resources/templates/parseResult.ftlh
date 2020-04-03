<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>Hello, Admin!</title>

    <link rel="stylesheet" href="/static/style.css"/>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
          integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
          crossorigin="anonymous"/>
</head>
<body>

<div class="container">
    <h2>Получение данных из источников</h2>
    <div>
        <a href="/main">Go to main page</a>
    </div>

    <div class="container">
        <h4>Получить данные по миксам</h4>
        <form action="/parse-mix" method="get">
            <div style="align-items: flex-start; flex-direction: column; flex: 1">
                <p value="${result.status}"><strong>Статус обработки:</strong> ${result.status.title}.</p>
                <p><strong>Список ошибок:</strong></p>
                <div style="height: 200px; margin: 15px; overflow: scroll; border: lightskyblue inset 5px; font-size: 14px;">
                    <#list result.errorLog as errorMsg>
                        <pre>${errorMsg}</pre>
                    <#else>No errors
                    </#list>
                </div>
                <p><strong>Список предупреждений:</strong></p>
                <div style="height: 50px; margin: 15px; overflow-y: scroll; border: lightskyblue inset 5px; font-size: 14px;">
                    <#list result.warningLog as warningMsg>
                    <pre>${warningMsg}</pre>
                    <#else>
                        No warnings
                    </#list>
                </div>
                <p><strong>Добавленные миксы:</strong></p>
                <div style="height: 50px; margin: 15px; overflow-y: scroll; border: lightskyblue inset 5px; font-size: 14px;">
                    <#list result.dataList as item>
                    <pre>${item}</pre>
                    <#else>
                        No data
                    </#list>
                </div>
                <p><strong>Итог обработки:</strong> ${result.toString()}</p>
            </div>
            <div>
                <label><strong>Введите количество:</strong> </label>
                <input style="width: 50px" type="number" required name="count" value="50" min="0" step="25" placeholder="Кол-во даных для распознавания">
                <button type="submit" class="btn btn-primary">Начать парсинг</button>
            </div>
        </form>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
        integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
        integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
        crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"
        integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy"
        crossorigin="anonymous"></script>


</body>
</html>