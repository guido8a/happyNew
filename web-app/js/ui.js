/**
 * Created with IntelliJ IDEA.
 * User: luz
 * Date: 11/12/13
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */

//hace q todos los elementos con un atributo title tengan el title bonito de twitter bootstrap
$('[title!=]').tooltip({});

//hace q los inputs q tienen maxlenght muestren la cantidad de caracteres utilizados/caracterres premitidos
$('[maxlength]').maxlength({
    alwaysShow        : true,
    warningClass      : "label label-success",
    limitReachedClass : "label label-important",
    placement         : 'top'
});

//para los dialogs, setea los defaults
bootbox.setDefaults({
    locale      : "es",
    closeButton : false,
    show        : true
});

//para el context menu deshabilita el click derecho en las paginas
context.init({
    preventDoubleContext : false
});
//context.attach('html', [
//    {
//        header : 'No click derecho!'
//    }
//]);

$(".digits").keydown(function (ev) {
    return validarInt(ev);
});

$(".number").keydown(function (ev) {
    return validarDec(ev);
});