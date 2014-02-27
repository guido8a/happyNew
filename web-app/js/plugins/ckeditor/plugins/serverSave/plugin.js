CKEDITOR.plugins.add('serverSave', {
    icons : 'serverSave',
    init  : function (editor) {
        editor.addCommand('serverSave', {
            exec : function (editor) {
                var id = editor.name;
                var cont = $("#" + id).val();
                var url = editor.config.serverSave.saveUrl;
                var data = editor.config.serverSave.saveData;

                data[id] = cont;

                $.ajax({
                    type    : "POST",
                    url     : url,
                    data    : data,
                    success : function (msg) {
                        editor.config.serverSave.saveDone(msg);
                    }
                });
            }
        });
        editor.ui.addButton('ServerSave', {
            label   : 'Guardar',
            command : 'serverSave',
            toolbar : 'insert'
        });
    }
});