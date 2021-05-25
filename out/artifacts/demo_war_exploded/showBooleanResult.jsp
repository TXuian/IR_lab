<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<!-- 网页使用的语言 -->
<html lang="zh-CN">

<style type="text/css">
    html {
        font-family: sans-serif;
        -ms-text-size-adjust: 100%;
        -webkit-text-size-adjust: 100%;
    }

    body {
        margin: 10px;
    }
    table {
        border-collapse: collapse;
        border-spacing: 0;
    }

    td,th {
        padding: 0;
    }

    .pure-table {
        border-collapse: collapse;
        border-spacing: 0;
        empty-cells: show;
        border: 1px solid #cbcbcb;
    }

    .pure-table caption {
        color: #000;
        font: italic 85%/1 arial,sans-serif;
        padding: 1em 0;
        text-align: center;
    }

    .pure-table td,.pure-table th {
        border-left: 1px solid #cbcbcb;
        border-width: 0 0 0 1px;
        font-size: inherit;
        margin: 0;
        overflow: visible;
        padding: .5em 1em;
    }

    .pure-table thead {
        background-color: #e0e0e0;
        color: #000;
        text-align: left;
        vertical-align: bottom;
    }

    .pure-table td {
        background-color: transparent;
    }

    .pure-table-bordered td {
        border-bottom: 1px solid #cbcbcb;
    }

    .pure-table-bordered tbody>tr:last-child>td {
        border-bottom-width: 0;
    }
</style>

<head>
    <!-- 指定字符集 -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>古诗词检索 Lab01</title>

    <!-- 1. 导入CSS的全局样式 -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <!-- 2. jQuery导入，建议使用1.9以上的版本 -->
    <script src="js/jquery-2.1.0.min.js"></script>
    <!-- 3. 导入bootstrap的js文件 -->
    <script src="js/bootstrap.min.js"></script>
    <style type="text/css">
        td, th {
            text-align: center;
        }
    </style>
</head>
<body>
<div class="container">
    <h3 style="text-align: center">古诗词布尔模型检索结果</h3>
    <table border="1" class="pure-table pure-table-bordered">
        <thead class="success">
            <th>标题</th>
            <th>作者</th>
            <th>正文</th>
            <th>tf-idf得分</th>
            <th>wf-idf得分</th>
        </thead>

<%--        <jsp:useBean id="hm_docs" scope="request" type="java.util.HashMap"/>--%>
        <c:forEach items="${DocStruct}" var="doc" varStatus="s">
            <tr>
                <td>${doc.value.head}</td>
                <td>${doc.value.author}</td>
                <td>${doc.value.content}</td>
                <td>${doc.key.sum_tf_idf}</td>
                <td>${doc.key.sum_wf_idf}</td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>