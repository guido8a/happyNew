CKEDITOR.plugins.add('timestamp', {
    icons : 'timestamp',
    init  : function (editor) {
        editor.addCommand('insertTimestamp', {
            exec : function (editor) {
                var now = new Date();
                editor.insertHtml(now.toString("dd-MM-yyyy HH:mm:ss"));
            }
        });
        editor.ui.addButton('Timestamp', {
            label   : 'Insertar fecha y hora',
            command : 'insertTimestamp',
            toolbar : 'insert'
        });
    }
});