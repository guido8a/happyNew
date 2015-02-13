/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function (config) {

    // %REMOVE_START%
    // The configuration options below are needed when running CKEditor from source files.
//    config.plugins = 'dialogui,dialog,about,a11yhelp,dialogadvtab,basicstyles,bidi,blockquote,clipboard,button,panelbutton,panel,floatpanel,colorbutton,colordialog,templates,menu,contextmenu,div,resize,toolbar,elementspath,enterkey,entities,popup,filebrowser,find,fakeobjects,flash,floatingspace,listblock,richcombo,font,forms,format,horizontalrule,htmlwriter,iframe,wysiwygarea,image,indent,indentblock,indentlist,smiley,justify,menubutton,language,link,list,liststyle,magicline,maximize,newpage,pagebreak,pastetext,pastefromword,preview,print,removeformat,save,selectall,showblocks,showborders,sourcearea,specialchar,scayt,stylescombo,tab,table,tabletools,undo,wsc,autosave,eqneditor,wordcount';
    // %REMOVE_END%

    // Define changes to default configuration here. For example:
    // config.language = 'fr';
    config.skin = 'moonocolor';
    config.language = "es";
    config.uiColor = '#4D76A3';
//    config.enterMode = CKEDITOR.ENTER_BR;
    config.shiftEnterMode = CKEDITOR.ENTER_DIV;

//    config.extraPlugins = 'timestamp,serverSave,createPdf';

    config.scayt_autoStartup = true;
    config.scayt_sLang = 'es_ES';

    config.removePlugins = 'elementspath,resize';

    config.extraCss = "body{ min-width:400px; }";

    config.contentsCss= "p { text-align: justify; margin-bottom: 0; }";

    /*
     // Toolbar configuration generated automatically by the editor based on config.toolbarGroups.
     config.toolbar = [
     { name: 'document', groups: [ 'mode', 'document', 'doctools' ], items: [ 'Source', '-', 'Save', 'NewPage', 'Preview', 'Print', '-', 'Templates' ] },
     { name: 'clipboard', groups: [ 'clipboard', 'undo' ], items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] },
     { name: 'editing', groups: [ 'find', 'selection', 'spellchecker' ], items: [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ] },
     { name: 'forms', items: [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField' ] },
     '/',
     { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },
     { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl', 'Language' ] },
     { name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
     { name: 'insert', items: [ 'Image', 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe' ] },
     '/',
     { name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
     { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
     { name: 'tools', items: [ 'Maximize', 'ShowBlocks' ] },
     { name: 'others', items: [ '-' ] },
     { name: 'about', items: [ 'About' ] }
     ];
     */

    /*
     config.toolbar = [
     [ 'Source', '-', 'Save', 'NewPage', 'Preview', 'Print', '-', 'Templates' ] ,
     [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ] ,
     [ 'Find', 'Replace', '-', 'SelectAll', '-', 'Scayt' ],
     [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField' ],
     '/',
     [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat' ],
     [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl', 'Language' ],
     [ 'Link', 'Unlink', 'Anchor' ] ,
     [ 'Image', 'Flash', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe' ],
     '/',
     [ 'Styles', 'Format', 'Font', 'FontSize' ],
     [ 'TextColor', 'BGColor' ] ,
     [ 'Maximize', 'ShowBlocks' ],
     [ '-' ] ,
     [ 'About' ]
     ]
     */
};

//CKEDITOR.on( 'instanceReady', function( ev ) {
//    var blockTags = ['div','h1','h2','h3','h4','h5','h6','p','pre','li','blockquote','ul','ol','table','thead','tbody','tfoot','td','th',];
//    var rules = {
//        indent : true,
//        breakBeforeOpen : true,
//        breakAfterOpen : false,
//        breakBeforeClose : false,
//        breakAfterClose : true
//    };
//
//    for (var i=0; i<blockTags.length; i++) {
//        ev.editor.dataProcessor.writer.setRules( blockTags[i], rules );
//    }
//});