
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello, Admin!</title>

    <link rel="stylesheet" href="/static/style.css" />
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
          integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
          crossorigin="anonymous" />
</head>
<body>
<h2>Hello, Admin!</h2>

<#--<div>-->
    <#--<a href="/tobaccos_list">Tobaccos List</a>-->
    <#--<a href="/mixes_list">Mixes List</a>-->
<#--</div>-->

<div class="container">

    <div>
        <a href="/tobaccos_list">Tobaccos List</a>
        <a href="/mixes_list">Mixes List</a>
    </div>

    <div>
        <form action="/main" method="post">

            <label><p>Tobacco:</p> </label>
            <div>
                <p><input type="text" class="form-control col-sm-3" name="title" required placeholder="Title" /></p>
            </div>
            <div>
                <select name="makers" class="form-control selcls col-sm-3">
                        <#list makers as maker>
                            <option value="${maker.title}">${maker.title}</option>
                        </#list>
                </select>
            </div>
            <div>
                <p><input type="text" class="form-control col-sm-3" name="description" required placeholder="Description" /></p>
            </div>
            <div>
                <select name="tastes" class="form-control selcls col-sm-3">
                        <#list tastes as taste>
                            <option value="${taste}">${taste}</option>
                        </#list>
                </select>
            </div>
            <div>
                <p><input type="text" class="form-control col-sm-3" name="strength" required placeholder="Strength" /></p>
            </div>
            <div>
                <p><input type="text" class="form-control col-sm-3" name="image" required placeholder="Image" /></p>
            </div>
            <div>
                <p><input type="text" class="form-control col-sm-3" name="tags" required placeholder="Tags" /></p>
            </div>

            <div>
                <button type="submit" class="btn btn-primary">Add tobacco</button>
            </div>

        </form>

    </div>

    <div class="my-3">
        <form action="/add_mix" method="post">

            <label><p>Mix: </p></label>

            <select name="mix" class="form-control selcls col-sm-3">

                <option>Tobacco Component</option>
                <option>Tobacco Component</option>
                <option>Tobacco Component</option>
                <option>Tobacco Component</option>
                <option>Tobacco Component</option>

            </select>

            <div>
                <p><button type="submit" class="btn btn-primary">Add mix</button></p>
            </div>
        </form>
    </div>

</div>


</body>
</html>