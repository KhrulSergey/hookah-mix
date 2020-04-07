<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>Hello, Admin!</title>

    <link rel="stylesheet" href="/static/style.css"/>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
          integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
          crossorigin="anonymous"/>
    <style>
        .spoiler-wrap {
            background: #fff;
            margin: 0 0 8px;
            border: 1px solid dodgerblue;
        }

        .spoiler-head {
            background: lightslategray;
            cursor: pointer;
            padding: 5px;
            font-size: 18px;
            font-weight: bold;
        }

        .spoiler-body {
            padding: 10px
        }

        .spoiler-wrap.disabled .spoiler-body {
            display: none
        }

        .spoiler-wrap.active {
            border-color: dodgerblue
        }

        .spoiler-wrap.active .spoiler-head {
            background: dodgerblue;
            color: #fff
        }
    </style>

    <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
            integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"
            integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy"
            crossorigin="anonymous"></script>
    <script>
        jQuery(document).ready(function () {
            jQuery('.spoiler-head').click(function () {
                $(this).parents('.spoiler-wrap').toggleClass("active").find('.spoiler-body').slideToggle();
            })
        })
    </script>
</head>
<body>

<div class="container">
    <h2>Получение данных из источников</h2>
    <div>
        <a href="/main">Go to main page</a>
    </div>
    <div class="spoiler-wrap <#if (resultTobacco.sourceEntriesCount > 0) > active <#else> active </#if> ">
        <div class="spoiler-head">Получить данные по табакам</div>
        <div class="spoiler-body">
            <form action="../parse-tobacco" method="get">
                <div style="align-items: flex-start; flex-direction: column; flex: 1">
                    <p><strong>Статус обработки:</strong> ${resultTobacco.status.title}.</p>
                    <p><strong>Список ошибок:</strong></p>
                    <div style="height: 200px; margin: 15px; overflow: scroll; border: dodgerblue inset 5px; font-size: 14px;">
                        <#list resultTobacco.errorLog as errorMsg>
                            <pre>${errorMsg}</pre>
                        <#else>No errors
                        </#list>
                    </div>
                    <p><strong>Список предупреждений:</strong></p>
                    <div style="height: 50px; margin: 15px; overflow-y: scroll; border: dodgerblue inset 5px; font-size: 14px;">
                        <#list resultTobacco.warningLog as warningMsg>
                            <pre>${warningMsg}</pre>
                        <#else>
                            No warnings
                        </#list>
                    </div>
                    <p><strong>Добавленные миксы:</strong></p>
                    <div style="height: 50px; margin: 15px; overflow-y: scroll; border: dodgerblue inset 5px; font-size: 14px;">
                        <#list resultTobacco.dataList as item>
                            <pre>${item}</pre>
                        <#else>
                            No data
                        </#list>
                    </div>
                    <p><strong>Итог обработки:</strong> ${resultTobacco.toString()}</p>
                </div>
                <div>
                    <label><strong>Введите количество:</strong> </label>
                    <input style="width: 50px" type="number" required name="count" value="5" min="0" step="5"
                           placeholder="Кол-во даных для распознавания">
                    <button type="submit" class="btn btn-danger">Начать парсинг</button>
                </div>
            </form>
        </div>
    </div>
    <div class="spoiler-wrap <#if (resultMix.sourceEntriesCount > 0) > active <#else> disabled </#if> ">
        <div class="spoiler-head">Получить данные по миксам</div>
        <div class="spoiler-body">
            <form action="../parse-mix" method="get">
                <div style="align-items: flex-start; flex-direction: column; flex: 1">
                    <p><strong>Статус обработки:</strong> ${resultMix.status.title}.</p>
                    <p><strong>Список ошибок:</strong></p>
                    <div style="height: 200px; margin: 15px; overflow: scroll; border: lightskyblue inset 5px; font-size: 14px;">
                        <#list resultMix.errorLog as errorMsg>
                            <pre>${errorMsg}</pre>
                        <#else>No errors
                        </#list>
                    </div>
                    <p><strong>Список предупреждений:</strong></p>
                    <div style="height: 50px; margin: 15px; overflow-y: scroll; border: lightskyblue inset 5px; font-size: 14px;">
                        <#list resultMix.warningLog as warningMsg>
                            <pre>${warningMsg}</pre>
                        <#else>
                            No warnings
                        </#list>
                    </div>
                    <p><strong>Добавленные миксы:</strong></p>
                    <div style="height: 50px; margin: 15px; overflow-y: scroll; border: lightskyblue inset 5px; font-size: 14px;">
                        <#list resultMix.dataList as item>
                            <pre>${item}</pre>
                        <#else>
                            No data
                        </#list>
                    </div>
                    <p><strong>Итог обработки:</strong> ${resultMix.toString()}</p>
                </div>
                <div>
                    <label><strong>Введите количество:</strong> </label>
                    <input style="width: 50px" type="number" required name="count" value="50" min="0" step="25"
                           placeholder="Кол-во даных для распознавания">
                    <button type="submit" class="btn btn-danger">Начать парсинг</button>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>