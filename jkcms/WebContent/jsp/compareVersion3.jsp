<!DOCTYPE html>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath();
%>
<html lang="en">
<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/ext3.3.1/resources/css/ext-all.css" />
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/adapter/ext-base.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/locale/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/jquery/jquery.min.js"></script>
<script type="text/javascript">
	function doCompare(){
		var diffType = '1';
		var requestId = '2930'
		Ext.Ajax.request( {
			url : '/jkcms/compare/doCompare',
			success : function(response) {
				var result = Ext.decode(response.responseText);
				if (result.success == 'success') {
					Ext.Msg.alert("提示", "海报上传成功!");
					com.walker.file.category.refreshTreeNode();
				}
			},
			failure : function(response) {
				Ext.Msg.alert("提示", "海报上传失败!");
			},
			params : {
				diffType : diffType,
				requestId : requestId
			}
		});
	}
</script>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>JsonComparator</title>
		<style type="text/css">
			body  {
				background-color: white;
				padding: 0;
				margin: 0;
			}

			.header {
				background-color: #205081;
				height: 30px;
			}

			.main_div {
				overflow: hidden;
			}

			.leftdiv {
				width: 200px;
				margin-left: 50px;
				float: left;
			}

		/*	.centerdiv {
				float: left;
				width: 10px;
				border-right: 1px dashed black;
				padding-bottom: 1600px;  
				margin-bottom: -1600px; 
				margin-right: 50px; 
				margin-left: 20px;
			}*/

			.rightdiv {
				float: right;
				margin-right: 200px;
			}	

			.fourdiv {
				overflow: hidden;
			}

			.resdiv {
				float: left;
				width: 150px;

			}


			h1,h2 {
				font-weight: bold;
				text-align: center;
			}


			
			.left_label,.title_label{
				 font-weight: bold;
				 font-size: 20px
				margin-top: 10px;
				display: inline-block;

			}

			
			.left_select {
				margin-top: 10px;
			}

			.left_select #selectKind {                    	
				width: 174px;					
				height: 30px;
				font-size: 15px;	
				font-weight: bold;
				border-radius: 5px;
			}
			.selectIdDiv {
				width: 174px;					
				/*height: 250px;*/
				margin-top: 70px;
				/*border:1px solid red;*/

			}
			.selectKindDiv {
				width: 174px;
				height: 55px;

			}
			.left_select #selectId {                    	
				width: 174px;					
				font-size: 15px;	
				font-weight: bold;
	 			border-radius: 5px;

			}
			
			.left_select #selectKind option{
				 font-weight: bold;
				 font-size: 15px;
			}
			.left_select #selectId option {
				font-weight: bold;
				 font-size: 15px;
			}
		</style>
	</head>
	<body>
		<div class="header">
		</div>
		
		<div class="main_div">
			<div class="leftdiv">
					<h2>查询操作</h2>	  		
				  	<div class='left_select'>
				  		
				  		<div class="selectKindDiv">
				  			<label class="left_label">diff_type</label>
				  			<select id="selectKind">
								<option>Envb的独有键</option>
								<option>Enva的独有键</option>
								<option>值不同的键</option>
							</select>
				  		</div>
					
						
						<div class ="selectIdDiv">
							<label class="left_label">req_id</label>
							<select id="selectId">
								<option>2319</option>
								<option>2320</option>
								<option>2321</option>
								<option>2322</option>
								<option>2323</option>
								<option>2324</option>
								<option>2325</option>
								<option>2326</option>
								<option>2327</option>
								<option>2328</option>
								<option>2329</option>
								<option>2330</option>
								<option>2331</option>
								<option>2332</option>
								<option>2333</option>
								<option>2334</option>
								<option>2335</option>
								<option>2336</option>
								<option>2337</option>
								<option>2338</option>
								<option>2339</option>
								<option>2340</option>
								<option>2341</option>
								<option>2342</option>
								<option>2343</option>
								<option>2344</option>
								<option>2345</option>
								<option>2346</option>
								<option>2347</option>
								<option>2348</option>
								<option>2349</option>
								<option>2350</option>
								<option>2351</option>
								<option>2352</option>
								<option>2353</option>
								<option>2354</option>
								<option>2355</option>
								<option>2356</option>
								<option>2357</option>
								<option>2358</option>
								<option>2359</option>
								<option>2360</option>
							</select>
						</div>
					</div>
					
					<!-- <br> -->
					<button class="button" onclick="doCompare()" type="button">Query</button>
			</div>

			<!-- <div class="centerdiv"></div> -->

			<div class="rightdiv">
					<h2>结果展示</h2>
					<div class="fourdiv">
						<div class="resdiv" id="ltdiv">
							<label class="title_label">key</label>
							<ul id="ltul"></ul>
						</div>	
						<div class="resdiv" id="middiv1">
							<label class="title_label">EnvA—value</label>
							<ul id="midu1"></ul>
						</div>	
						<div class="resdiv" id="middiv2">
							<label class="title_label">EnvB—value</label>
							<ul id="midu2"></ul>
						</div>
						<div class="resdiv" id="rtdiv1">
							<label class="title_label">EnvA-Msg</label>
							<ul id="rtu1"></ul>
						</div>	
						<div class="resdiv" id="rtdiv2">
							<label class="title_label">EnvB-Msg</label>
							<ul id="rtu2"></ul>
						</div>
					</div>
			</div>	
		</div>
	</body>
</html>