var express=require('express');
var path=require('path');


var app=express();


//define EJS template and file location
app.set('views',path.join(__dirname,'views'));//__dirname表示本代码所在的目录
app.set('view engine','ejs');

//define static file menu
app.use(express.static(path.join(__dirname,'public')));

app.get('/',function(req,res){
    res.render('index',{title:'smileyun_pku:微信墙'})
})

app.listen(3007,function(req,res){
    console.log('app is running at port 3000');
})
