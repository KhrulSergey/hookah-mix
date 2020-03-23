
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
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


<div class="container">

    <div>
        <a href="/hookah-mix/catalog_tobaccos">Tobaccos List</a>
        <a href="/hookah-mix/catalog_mixes">Mixes List</a>
    </div>

    <div>
        <form action="/main" method="post">

            <label><p>Tobacco:</p> </label>
            <div>
                <p><input type="text" class="form-control col-sm-3" name="title" required placeholder="Title" /></p>
            </div>
            <div>
                <select name="makers" class="form-control selcls col-sm-3">
                    <option value="" disabled selected>Maker</option>
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
                    <option value="" disabled selected>Taste</option>
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

    <div class="container">
        <div class="row">
            <div class="col-sm-6">
                <form action="/add_mix" method="post">

                    <label><p>Mix: </p></label>

                    <div>
                        <p><input type="text" class="form-control col-sm-6" name="title" required placeholder="Mix title"/></p>
                    </div>

                    <div>
                        <p><input type="text" class="form-control col-sm-6" name="tags" required placeholder="Tags"/></p>
                    </div>

                    <div>
                        <p><input type="text" class="form-control col-sm-6" name="description" required placeholder="Description"/></p>
                    </div>

                    <div>
                        <p><input type="text" class="form-control col-sm-6" name="strength" required placeholder="Strength"/></p>
                    </div>

                    <div>
                        <p><button type="submit" class="btn btn-primary">Add mix</button></p>
                    </div>
                </form>
            </div>

            <div class="col-sm-6">
                <form action="/add_component" method="post">
                    <div class="collapse my-3" id="collapseExample">
                        <div>
                            <label>Add component for this mix</label>

                            <div>
                                <p><input type="text" class="form-control col-sm-6" name="mixTitle" required placeholder="Mix Title"/></p>
                            </div>

                            <div>
                                <p><input type="text" class="form-control col-sm-6" name="makerTitle" required placeholder="Maker Title"/></p>
                            </div>

                            <div>
                                <p><input type="text" class="form-control col-sm-6" name="tobaccoTitle" required placeholder="Tobacco Title"/></p>
                            </div>

                            <div>
                                <p><input type="text" class="form-control col-sm-6" name="composition" required placeholder="Percentage" /></p>
                            </div>

                            <div>
                                <p><button type="submit" class="btn btn-primary">Add component</button></p>
                            </div>

                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="my-3">

    </div>

    <a class="btn btn-primary" data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false" aria-controls="collapseExample">
        Components
    </a>

</div>

<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>

</body>
</html>