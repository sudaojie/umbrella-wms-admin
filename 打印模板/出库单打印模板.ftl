<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <meta http-equiv="content-type" content="text/html;charset=utf-8"/>
    <style type="text/css">
        body {font-family: SimSun;}
        .mybody h1 {font-size: 24px;text-align: center;line-height: 30px;padding: 20px 0;padding-top: 30px;}
        .content_class p {display: block;margin-block-start: 1em;margin-block-end: 1em;margin-inline-start: 0px;margin-inline-end: 0px;}
        .content_class{box-shadow:none;line-height:40px;margin:15px 0;height: auto !important;padding: 0 !important;}
        .t_tail{ position:relative;text-align:center;}
        .Gzimg{ position:absolute;right: 0;top:100px;width: 140px;height: 140px;}
		.table_class table{
			width: 100%;
			margin-top: 60px;
		}
		.table_class table tr th {
			min-width: 60px;
		}
		.clearfix:after{
			content:"";
			display：block;
			height:0;
			visibility:hidden;
			clear: both;
		}
		.clearfix {
			*zoom: 1;
		}

		.flex {
			display: flex;
			justify-content: space-between;
		}

		.top-box .clearfix{
			width: 500px;
			margin-bottom: 50px;
		}
    </style>
</head>
<body>
<div class="mybody">
    <div class="header"></div>
    <h1>出库单</h1>
	<div class="top-box">
		<div class="clearfix">
			<div style="float: left">出库单号：${outBillCode}</div>
			<div style="float: right">出库类别：${outBillCategory}</div>
		</div>
		<div class="clearfix">
			<div style="float: left">制单人：${createBy}</div>
			<div style="float: right">制单人：${createTime}</div>
		</div>
	</div>
    <div class="content_class">
	<div class="content_class table_class">
		<table border="1" cellspacing="0" cellpadding="0" >
			<thead>
				<tr>
					<th style="text-align: center;width:50px;">序号</th>
					<th style="text-align: center;width:30%;">货物编码</th>
					<th style="text-align: center;width:30%;">货物名称</th>
					<th style="text-align: center;width:20%;">规格型号</th>
					<th style="text-align: center;width:10%;">计量单位</th>
					<th style="text-align: center;width:10%;">出库数量</th>
				</tr>
			</thead>
			<tbody>
				<#assign oneNum=1/>
					<#list outbillGoodsList as list>
					    <tr>
							<td style="text-align: center">${oneNum}</td>
							<td style="text-align: center">${list.goodsCode}</td>
							<td style="text-align: center">${list.goodsName}</td>
							<td style="text-align: center">${list.model}</td>
							<td style="text-align: center">${list.measureUnit}</td>
							<td style="text-align: center">${list.outBillNum}</td>
					    </tr>
					<#assign oneNum++/>
				</#list>
			</tbody>
		</table>
	</div>
    </div>

    <div class="footer"></div>
</div>
</body>
</html>
