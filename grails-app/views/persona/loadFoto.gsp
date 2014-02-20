<script src="${resource(dir: 'js/plugins/Jcrop-1902/js', file: 'jquery.Jcrop.min.js')}"></script>
<link href="${resource(dir: 'js/plugins/Jcrop-1902/css', file: 'jquery.Jcrop.min.css')}" rel="stylesheet">

<div>
    <img id="foto" src="${resource(dir: 'images/perfiles/' + usuario.id, file: usuario.foto)}"/>
</div>

<div style="width:200px;height:300px;overflow:hidden;margin-left:5px;">
    <img id="preview" src="${resource(dir: 'images/perfiles/' + usuario.id, file: usuario.foto)}"/>
</div>


<script type="text/javascript">
    $(function () {
        function showPreview(coords) {
            console.log(coords);
            var rx = 200 / coords.w;
            var ry = 300 / coords.h;

            $('#preview').css({
//                width      : Math.round(rx * 500) + 'px',
//                height     : Math.round(ry * 370) + 'px',
                width      : Math.round(rx * ${w}) + 'px',
                height     : Math.round(ry * ${h}) + 'px',
                marginLeft : '-' + Math.round(rx * coords.x) + 'px',
                marginTop  : '-' + Math.round(ry * coords.y) + 'px'
            });
        }

        $('#foto').Jcrop({
            onChange    : showPreview,
            onSelect    : showPreview,
            aspectRatio : 2 / 3
        });

    });
</script>