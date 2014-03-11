CKEDITOR.plugins.add('createPdf', {
    icons : 'createPdf',
    init  : function (editor) {
        editor.addCommand('createPdf', {
            exec : function (editor) {
                var id = editor.name;
                var cont = $("#" + id).val();
                var url = editor.config.createPdf.saveUrl;
                var data = editor.config.createPdf.saveData;
                var action = editor.config.createPdf.pdfAction.toLowerCase();
                data[id] = cont;

                switch (action) {
                    case "download":
                        location.href = url + "?" + $.param(data);
                        break;
                    case "save":
                        $.ajax({
                            type    : "POST",
                            url     : url,
                            data    : data,
                            success : function (msg) {
                                editor.config.createPdf.createDone(msg);
                            }
                        });
                        break;
                    default:
                        $.ajax({
                            type    : "POST",
                            url     : url,
                            data    : data,
                            success : function (msg) {
                                editor.config.createPdf.createDone(msg);
                            }
                        });
                }
            }
        });
        editor.ui.addButton('CreatePdf', {
            label   : 'Descargar Pdf',
            command : 'createPdf',
            toolbar : 'insert'
        });
    }
});