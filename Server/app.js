var mysql = require('mysql');
var express = require('express');
var bodyParser = require('body-parser');

//서버 객체 생성
var PORT = 3000;
var app = express();

//DB 생성
var connection = mysql.createConnection({
    host: "localhost",
    user: "root",
    database: "example",
    password: "Capstone0!",
    port: 3306
});
//Middlewares
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

//라우팅
app.get('/', function(req, res) {
	res.send("Hello world!");
	connection.query('select * from Users', function(err, rows, fields) {
		if (err) throw err;
		console.log('users rows : ', rows);
	});
	connection.end();
});

app.post('/user/join', function (req, res) {
        var userEmail = req.body.userEmail;
        var userPwd = req.body.userPwd;
        var userName = req.body.userName;

        // 삽입을 수행하는 sql문.
        var sql = 'INSERT INTO users (UserEmail, UserPwd, UserName) VALUES (?, ?, ?)';
        var params = [userEmail, userPwd, userName];
        connection.query(sql, params, function (err, result) {
            var resultCode = 404;
            var message = '에러가 발생했습니다';
    
            if (err) {
                console.log(err);
            } else {
                resultCode = 200;
                message = '회원가입에 성공했습니다.';
            }
    
            res.json({
                'code': resultCode,
                'message': message
            });
        });
});

app.post('/user/login', function (req, res) {
    var userEmail = req.body.userEmail;
    var userPwd = req.body.userPwd;
    var sql = 'select * from Users where UserEmail = ?';

    connection.query(sql, userEmail, function(err, result) {
      var resultCode = 404;
      var message = '에러가 발생했습니다.';

      if(err){
        console.log(err);
      }else{
        if(result.length === 0){
          resultCode = 204;
          message = '존재하지 않는 계정입니다!';
        }
        else if(userPwd !== result[0].UserPwd) {
          resultCode = 204;
          message = '비밀번호가 틀렸습니다!';
        }
        else{
          resultCode = 200;
          message = '로그인 성공! ' + result[0].UserName + '님 환영합니다!';
        }
      }

      res.json({
        'code': resultCode,
        'message': message
      });
    });
});

//Server listening
app.listen(3000, function () {
    console.log('Server is listening : ', PORT);
});
