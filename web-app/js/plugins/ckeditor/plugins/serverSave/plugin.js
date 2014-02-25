CKEDITOR.plugins.add('serverSave', {
    icons : 'serverSave',
    init  : function (editor) {
        editor.addCommand('serverSave', {
            exec : function (editor) {
                var id = editor.name;
                var cont = $("#" + id).val();
                console.log(cont);
            }
        });
        editor.ui.addButton('ServerSave', {
            label   : 'Guardar',
            command : 'serverSave',
            toolbar : 'insert'
        });
    }
});