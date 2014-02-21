<script src="${resource(dir: 'js/plugins/Jcrop-1902/js', file: 'jquery.Jcrop.min.js')}"></script>
<link href="${resource(dir: 'js/plugins/Jcrop-1902/css', file: 'jquery.Jcrop.min.css')}" rel="stylesheet">

<div style="height: ${Math.max(h, 300) + 85}px">
    <div style="height: ${Math.max(h, 300) + 30}px">
        <div class="thumbnail" style="float: left;">
            <img id="foto" src="${resource(dir: 'images/perfiles/', file: usuario.foto)}"/>
        </div>

        <g:if test="${w > 200 || h > 300}">
            <div style="width:200px;height:300px;overflow:hidden;margin-left:5px; float: left; border: 1px solid cornflowerblue;">
                <img id="preview" src="${resource(dir: 'images/perfiles/', file: usuario.foto)}"/>
            </div>
        </g:if>
    </div>

    <g:if test="${w > 200 || h > 300}">
        <div>
            <a href="#" class="btn btn-success" id="btnSave"><i class="fa fa-save"></i> Guardar</a>
        </div>
    </g:if>
</div>

<script type="text/javascript">
    $(function () {
        function showPreview(coords) {
            var rx = 200 / coords.w;
            var ry = 300 / coords.h;

            $('#preview').css({
                width      : Math.round(rx * ${w}) + 'px',
                height     : Math.round(ry * ${h}) + 'px',
                marginLeft : '-' + Math.round(rx * coords.x) + 'px',
                marginTop  : '-' + Math.round(ry * coords.y) + 'px'
            }).data({
                x : coords.x,
                y : coords.y,
                w : coords.w,
                h : coords.h
            });
        }

        <g:if test="${w > 200 || h > 300}">
        $('#foto').Jcrop({
            onChange    : showPreview,
            onSelect    : showPreview,
            aspectRatio : 2 / 3
        });

        $("#btnSave").click(function () {
            $.ajax({
                type    : "POST",
                url     : "${createLink(action:'resizeCropImage')}",
                data    : $("#preview").data(),
                success : function (msg) {
                    openLoader("Cargando...");
                    setTimeout(function () {
                        location.href = "${createLink(action: 'personal', params:[tipo:'foto'])}";
                    }, 1000);
                }
            });
        });
        </g:if>
    });
</script>