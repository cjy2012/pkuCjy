/**编写入口文件*/
//加载依赖库
var express=require('express');
var path=require('path');
var bodyParser=require('body-parser');
var crypto=require('crypto');
var session=require('express-session');
var moment = require('moment');
var mysql = require('mysql');
var app=express();

var connection = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'cjy1314520',
    database: 'mynote'
});

//define EJS template and file location
app.set('views',path.join(__dirname,'views'));//__dirname表示本代码所在的目录
app.set('view engine','ejs');

//define static file menu
app.use(express.static(path.join(__dirname,'public')));

//define data parser
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));

//create session model
app.use(session({
	secret:'1234',
	name:'mynote',
	cookie:{maxAge:1000*60*10080},//设置session保存为一周时间
	resave:false,
	saveUninitialized:true
}));

app.get('/',function(req,res){
	if(req.session.user){
			var value=req.session.user.username;
			connection.query('SELECT * FROM notes where author="'+value+'"', function (err, result) {
				if (err) throw err;
				console.log("查到用户的笔记：")
				var count=result.length;
				console.log(result);
				connection.query('SELECT * FROM notes where author="'+value+'"limit 3', function (err, result) {
					if (err) throw err;
					res.render('index',{
						user:req.session.user,
						result:result,
						current:1,
						count:count,
						title:'首页',
						moment:moment
					});
				});
			});
	}else{
		res.render('index',{
			user:req.session.user,
			title:'首页'
		});
	}
});

app.get('/register',function(req,res){
	if(req.session.user){
		return res.redirect('/');
	}else{
		console.log('注册');
		res.render('register', {
			user:req.session.user,
			title:'注册',
			registerInfo:""
		});
	}
});

//post请求
app.post('/register',function(req,res){
	var registerInfo="";
	//获取表单数据
	var username=req.body.username;
	var password=req.body.password;
	var passwordRepeat=req.body.passwordRepeat;
	//检查输入用户名
	var userRule =/^[a-zA-Z]\w{2,19}$/;    // /^[a-zA-Z0-9_]{3,20}$/;
	var reg1= /[0-9]/   //存在数字
	var reg2=/[a-z]/    //存在小写字母
	var reg3=/[A-Z]/    //存在大写字母
	if(username.trim().length==0){
		console.log('用户名不能为空');
		registerInfo='用户名不能为空';
		//return res.redirect('/register');
		return res.render('register', {
			user:req.session.user,
			title:'注册',
			registerInfo:registerInfo
		});
	}
	if(!username.trim().match(userRule)){
		//console.log('用户名只能由字母、数字、下划线组成');
		registerInfo='用户名只能由字母、数字、下划线组成';
		//return res.redirect('/register');
		return res.render('register', {
			user:req.session.user,
			title:'注册',
			registerInfo:registerInfo
		});
	}
	//检查输入的密码是否为空
	if(password.trim().length<6){
		//console.log('密码不能为空且长度不能小于6');
		registerInfo='密码不能为空且长度不能小于6';
		//return res.redirect('/register');
		return res.render('register', {
			user:req.session.user,
			title:'注册',
			registerInfo:registerInfo
		});
	}
    if(!(password.trim().match(reg1)&&password.trim().match(reg2)&&password.trim().match(reg3))){
		console.log('密码必须同时包含数字、大小写字母');
		registerInfo='密码必须同时包含数字、大小写字母';
		//return res.redirect('/register');
		return res.render('register', {
			user:req.session.user,
			title:'注册',
			registerInfo:registerInfo
		});
	}
	//检查两次密码的一致性
	if(password!=passwordRepeat){
		console.log('两次密码不一致');
		registerInfo='两次密码不一致';
		//return res.redirect('/register');
		return res.render('register', {
			user:req.session.user,
			title:'注册',
			registerInfo:registerInfo
		});
	}
		var value=username;
		connection.query('SELECT * FROM users where username="'+value+'"', function (err, result) {
			console.log(result);
			if(err){
				console.log(err);
				registerInfo=err;
				return res.render('register', {
					user:req.session.user,
					title:'注册',
					registerInfo:registerInfo
				});
			}
			if(result.length!=0){
				console.log('用户名已经存在');
				registerInfo='用户名已经存在';
				return res.render('register', {
					user:req.session.user,
					title:'注册',
					registerInfo:registerInfo
				});
			}
			//对密码进行MD5加密
			var md5=crypto.createHash('md5');
			var md5password=md5.update(password).digest('hex');

            var userInfo=[username,md5password];
			connection.query('INSERT INTO users(username,passwd) values(?,?)',userInfo, function (err, user) {
					if (err) throw err;
					console.log(user);
				});
			return res.redirect('/login');
	});
});

app.get('/login',function(req,res){
	if(req.session.user){
		return res.redirect('/');
	}else{
		console.log('登录页面');
		res.render('login',{
			title:'登录',
			loginInfo:""
		});
	}
});

//post login请求
app.post('/login',function(req,res){
	var loginInfo="";
	var username=req.body.username;
	var password=req.body.password;
		connection.query('select * from users where username=?',username, function (err, user) {
			if(err){
				console.log(err);
				loginInfo=err;
				return res.render('login',{
					title:'登录',
					loginInfo:loginInfo
				});
			}
			if(!user){
				console.log('用户不存在');
				loginInfo='用户不存在'
				//return res.redirect('/login')
				return res.render('login',{
					title:'登录',
					loginInfo:loginInfo
				});
			}
			console.log(user);

			//对密码进行加密
			var md5=crypto.createHash('md5');
			var md5password=md5.update(password).digest('hex');
			console.log(md5password);
			if(user[0].passwd!=md5password){
				console.log('密码错误！');
				loginInfo='密码错误！';
				return res.render('login',{
					title:'登录',
					loginInfo:loginInfo
				});
			}
			console.log('登录成功！');
			user[0].passwd=null;
			delete user[0].passwd;
			req.session.user=user[0];
			return res.redirect('/');
			console.log(result);
		});
});

app.get('/quit',function(req,res){
	req.session.user=null;
	console.log('退出');
	return res.redirect('/login');
});

app.get('/post',function(req,res){
	if(req.session.user) {
		console.log('发布');
		res.render('post',
			{
				user:req.session.user,
				title:'发布'
			});
	}
	else{
		console.log('请您先登录，再发布笔记！');
		return res.redirect('/login');
	}
});

app.post('/post',function(req,res){
	var noteInfo=[req.body.title,req.session.user.username,req.body.tag,req.body.content];
	connection.query('INSERT INTO notes(title,author,tag,content) values(?,?,?,?)',noteInfo, function (err, note) {
			if(err){
				console.log(err);
				return res.redirect('/post');
			}
		    console.log(note);
			console.log('文章发表成功');
			return res.redirect('/');
	});
});

app.get('/detail',function(req,res){
	if(req.session.user){
		console.log('日记详情');
		console.log('这是：'+req.query.id);
		var noteId=req.query.id;
		connection.query('select * from notes where id=?',noteId, function (err, noteResult) {
			if(err){
				console.log(err);
				return res.redirect('/');
			}
			return res.render('detail',{
				user:req.session.user,
				noteResult:noteResult[0],
				title:'日记详情',
				moment:moment
			});
		});
	}else{
		console.log('请您先登录，再查看笔记！');
		return res.redirect('/login');
	}
});

app.get('/delete',function(req,res){
	if(req.session.user){
		var noteId=req.query.id;
		connection.query('delete from notes where id=?',noteId, function (err) {
			if(err){
				console.log(err);
				return res.redirect('/');
			}
			return res.redirect('/');
		});
	}else{
		console.log('请您先登录，再查看笔记！');
		return res.redirect('/login');
	}
});

//分页实现
app.get('/page',function(req,res){
	var currentPage=req.query._pageid;
	var value=req.session.user.username;
	connection.query('SELECT * FROM notes where author="'+value+'"', function (err, result) {
			if (err)
				console.log(err);
			console.log(result);
		    var count=result.length;
		    connection.query('SELECT * FROM notes where author="'+value+'"limit '+(currentPage-1)*3+','+3, function (err, result) {
				if (err)
					console.log(err);
				res.render('index', {
					user: req.session.user,
					result: result,
					current: currentPage,
					count: count,
					title: '首页',
					moment: moment
				});
			});
		});
})


//listen 3000端口
app.listen(3000,function(req,res){
	console.log('app is running at port 3000');
})