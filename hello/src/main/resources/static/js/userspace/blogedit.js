// DOM 加载完再执行
$(function() {

    var testEditor;

    $(function() {
        testEditor = editormd("test-editormd", {
            width   : "90%",
            height  : 640,
            syncScrolling : "single",
            path    : "../../../../js/editor/lib/",
            imageUpload : true,
            imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
            imageUploadURL : "/upload",
            saveHTMLToTextarea : true,

            toolbarIcons : function() {
                return ["undo", "redo", "bold", "preview", "watch", "fullscreen","uploadImage", "saveIcon"]
            },
            toolbarIconsClass : {
                saveIcon : "fa-save",
                uploadImage : "fa-upload"
            },

            /**设置主题颜色*/
            editorTheme: "pastel-on-dark",
            theme: "dark",
            previewTheme: "dark",

            // 自定义工具栏按钮的事件处理
            toolbarHandlers : {

                uploadImage : function(cm, icon, cursor, selection) {
                    // 获取 CSRF Token
                    var csrfToken = $("meta[name='_csrf']").attr("content");
                    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

                    $.ajax({
                        url: 'http://localhost:8081/upload',
                        type: 'POST',
                        cache: false,
                        data: new FormData($('#uploadformid')[0]),
                        beforeSend: function(request) {
                            request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
                        },
                        processData: false,
                        contentType: false,
                        success: function(data){
                            var mdcontent=$("#md").val();
                            $("#md").val(mdcontent + "![]("+data +")");

                            var testEditor;

                            // 调用初始化函数是因为不会重新加载图片
                            $(function() {
                                testEditor = editormd("test-editormd", {
                                    width   : "90%",
                                    height  : 640,
                                    syncScrolling : "single",
                                    path    : "../../../../js/editor/lib/",
                                    imageUpload : true,
                                    imageFormats : ["jpg", "jpeg", "gif", "png", "bmp", "webp"],
                                    imageUploadURL : "/upload",
                                    saveHTMLToTextarea : true,
                                });
                            });
                        }
                    }).done(function(res) {
                        $('#file').val('');
                        $("#md").val($("#md").val()+"\n");
                    }).fail(function(res) {});
                },

                /**
                 * @param {Object}      cm         CodeMirror对象
                 * @param {Object}      icon       图标按钮jQuery元素对象
                 * @param {Object}      cursor     CodeMirror的光标对象，可获取光标所在行和位置
                 * @param {String}      selection  编辑器选中的文本
                 */
                saveIcon : function(cm, icon, cursor, selection) {
                    // 获取 CSRF Token
                    var csrfToken = $("meta[name='_csrf']").attr("content");
                    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

                    $.ajax({
                        //url: '/u/'+ $(this).attr("userName") + '/blogs/edit',
                        url: '/u/'+ "admin" + '/blogs/edit',
                        type: 'POST',
                        contentType: "application/json; charset=utf-8",
                        data:JSON.stringify(
                            {
                                "id":$('#id').val(),
                            "title": $('#title').val(),
                            "summary": $('#summary').val() ,
                            "content": $('#md').val()
                            }
                            ),
                        beforeSend: function(request) {
                            request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
                        },
                        success: function(data){
                            if (data.success) {
                                // 成功后，重定向
                                window.location = data.body;
                            } else {
                                toastr.error("error!"+data.message);
                            }

                        },
                        error : function() {
                            toastr.error("error!");
                        }
                    })
                }
            }
        });
    });
  
    // 初始化标签控件
    $('.form-control-tag').tagEditor({
        initialTags: [],
        maxTags: 5,
        delimiter: ', ',
        forceLowercase: false,
        animateDelete: 0,
        placeholder: '请输入标签'
    });
    
    $('.form-control-chosen').chosen();

});