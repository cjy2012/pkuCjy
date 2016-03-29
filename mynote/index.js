/**编写入口文件*/
//加载依赖库
var express=require('express');
var path=require('path');
var bodyParser=require('body-parser');
var crypto=require('crypto');
var session=require('express-session');
var moment = require('moment');
//引入mongoose
var mongoose=require('mongoose');
//引入模型
var models=require('./models/models');
var User=models.User;
var Note=models.Note;
//使用mongoose连接服务
mongoose.connect('mongodb://localhost:27017/notes');
mongoose.connection.on('error',console.error.bind(console,'连接数据库失败'));
//create express instance
var app=express();


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



//response index request
app.get('/',function(req,res){
	if(req.session.user){
		var notes=Note.find({author:req.session.user.username});
		var count;
		notes.exec(function(err,rs){
			if(err){
				console.log(err);
			}
			else{
				count=rs.length;
				console.log(count);
			}
		})
		var noteResults=Note.find({author:req.session.user.username});
		noteResults.limit(5);
		noteResults.exec(function(err,result){//查询数据库中笔记
				if(err){
					console.log(err);
				}
				else{
					res.render('index',{
						user:req.session.user,
						result:result,
						current:1,
						count:count,
						title:'首页',
						moment:moment
					});
				}
		});
	}else{
		res.render('index',{
			user:req.session.user,
			title:'首页'
		});
	}
});

app.get('/page',function(req,res){
    var currentPage=req.query._pageid;
	var notes=Note.find({author:req.session.user.username});
	var count;
	notes.exec(function(err,rs){
		if(err){
			console.log(err);
		}
		else{
			count=rs.length;
		}
	})
	var noteLists=Note.find({author:req.session.user.username});
	noteLists.skip((currentPage - 1) * 5);
	noteLists.limit(5);
	noteLists.exec(function(err,result){//查询数据库中笔记
		if(err){
			console.log(err);
		}
		else{
			res.render('index',{
				user:req.session.user,
				result:result,
				current:currentPage,
				count:count,
				title:'首页',
				moment:moment
			});
		}
	});
})

function getNotes(){
	var notes=Note.find({author:req.session.user.username});
	var count;
	notes.exec(function(err,rs){
		if(err){
			console.log(err);
			return ;
		}
		else{
			count=rs.length;
			return count;
		}
	})
}

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
	//检查用户是否已经存在，如果不存在，则保存记录
	User.findOne({username:username},function(err,user){
		if(err){
			console.log(err);
			registerInfo=err;
			//return res.redirect('./register');
			return res.render('register', {
				user:req.session.user,
				title:'注册',
				registerInfo:registerInfo
			});
		}
		if(user){
			console.log('用户名已经存在');
			registerInfo='用户名已经存在';
			//return res.redirect('./register');
			return res.render('register', {
				user:req.session.user,
				title:'注册',
				registerInfo:registerInfo
			});
		}
		//对密码进行MD5加密
		var md5=crypto.createHash('md5');
		var md5password=md5.update(password).digest('hex');

		//新建user对象用于保存数据
		var newUser=new User({
			username:username,
			password:md5password
		});

		newUser.save(function(err,doc){
			if(err){
				console.log(err);
				registerInfo=err;
				//return res.redirect('./register');
				return res.render('register', {
					user:req.session.user,
					title:'注册',
					registerInfo:registerInfo
				});
			}
			console.log('注册成功');
			return res.redirect('/login');
		});
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
	User.findOne({username:username},function(err,user){
		if(err){
			console.log(err);
			loginInfo=err;
			//return res.redirect('/login');
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
		//对密码进行加密
		var md5=crypto.createHash('md5');
		var md5password=md5.update(password).digest('hex');
		if(user.password!==md5password){
			console.log('密码错误！');
			loginInfo='密码错误！';
			//return res.redirect('/login');
			return res.render('login',{
				title:'登录',
				loginInfo:loginInfo
			});
		}
		console.log('登录成功！');
		user.password=null;
		delete user.password;
		req.session.user=user;
		return res.redirect('/');
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
	var note=new Note({
		title:req.body.title,
		author:req.session.user.username,
		tag:req.body.tag,
		content:req.body.content
	});
	note.save(function(err,doc){
		if(err){
			console.log(err);
			return res.redirect('/post');
		}
		console.log('文章发表成功');
		return res.redirect('/');
	});
});

app.get('/detail',function(req,res){
	if(req.session.user){
		console.log('日记详情');
		console.log('这是：'+req.query._id);
		var noteId=req.query._id;
		Note.findOne({_id:noteId},function(err,noteResult){
			if(err){
				console.log(err);
				return res.redirect('/');
			}
			return res.render('detail',{
				user:req.session.user,
				noteResult:noteResult,
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
		var noteId=req.query._id;
		Note.remove({_id:noteId},function(err,noteResult){
			if(err){
				console.log(err);
				return res.redirect('/');
			}
			else{
				res.redirect('/');
			}
		});
	}else{
		console.log('请您先登录，再查看笔记！');
		return res.redirect('/login');
	}
});

//listen 3000端口
app.listen(3000,function(req,res){
	console.log('app is running at port 3000');
})