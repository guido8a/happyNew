<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 3/12/14
  Time: 11:35 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Ver trámite</title>
        <style type="text/css">
        .panel-title {
            color : #00203F !important;
        }

        .panel-body {
            padding : 5px;
        }

        .show {
            color  : #BB9424;
            cursor : pointer;
        }
        </style>
    </head>

    <body>
        <util:renderHTML html="${html}"/>

        <script type="text/javascript">
            $(function () {
                $(".show").click(function () {
                    $(this).parent().next().toggleClass("hide");
                });
            });
        </script>
    </body>
</html>