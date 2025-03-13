<!DOCTYPE html>
<html>
<head>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            color: #333;
            padding: 20px;
        }
        .header {
            background-color: #4CAF50;
            color: white;
            padding: 10px;
            text-align: center;
            font-size: 24px;
        }
        table {
            border-collapse: collapse;
            width: 100%;
            margin-top: 20px;
        }
        td, th {
            border: 1px solid #ddd;
            padding: 8px;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        tr:hover {
            background-color: #ddd;
        }
    </style>
</head>
<body>
<div class="header">邮箱激活</div>
<div>您的注册信息是：</div>
<table>
    <tr>
        <th>项目</th>
        <th>信息</th>
    </tr>
    <tr>
        <td>管理员</td>
        <td>${name}</td>
    </tr>
    <tr>
        <td>电子邮件</td>
        <td>${email}</td>
    </tr>
</table>
<div>
    <span>邮箱验证码为：<strong>${captcha}</strong></span>
</div>
</body>
</html>
