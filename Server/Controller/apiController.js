const http = require("http");

const crypto = require('crypto');

const smtpTransport = require("../config/email");
const statusCode = require("../config/serverStatusCode");
const connection = require("../db/db");

function generateCurrentDate(type) {
    var date = new Date();

    var month = date.getMonth() + 1;
    var day = date.getDate();
    month = month >= 10 ? month : '0' + month;
    day = day >= 10 ? day : '0' + day;

    return (date.getFullYear() + "-" + month + "-" + day);
}

function attendanceCheck(req, res, userId) {
    var sql = `update user set last_login = NOW() where id = ${userId};`
        + `update user set point = point + 20  where id = ${userId};`;

    connection.query(sql, function (err, result) {
        if (err) {
            return false;
        }
        else {
            console.log("First login !");
            return true;
        }
    })
}

function updatePoint(req, res, point, userId) {
    var sql = `update user set point = point + ${point} where id = ${userId};`;

    connection.query(sql, function (err, result) {
        if (err) {
            return false;
        }
        else {
            console.log("point += " + point);
            return true;
        }
    })
}

var generateRandom = function (min, max) {
    var randomNum = Math.floor(Math.random() * (max - min + 1)) + min;
    console.log(randomNum);
    return randomNum;
}

const apiController = {
    //아이디 중복체크
    dupIdCheck : function (req, res) {
        const id = req.body.login_id;
        console.log(id);

        if (id == undefined || id == ""){
            res.json({
                'code' : statusCode.CLIENT_ERROR
            })
        }

        var sql = "SELECT login_id from user;";
        connection.query(sql, function (err, result) {
            var resultCode = statusCode.OK;
            if (err){
                console.log(err);
                resultCode = statusCode.SERVER_ERROR;
            } else {
                for(var i = 0;i < result.length;i++){
                    if (id == result[i].login_id){
                        resultCode = statusCode.CLIENT_ERROR;
                        break;
                    }
                }
            }
            console.log(resultCode);
            res.json({
                'code' : resultCode
            })
        })
    },
    //이메일인증보내기
    sendEmail : async function (req, res) {
        const number = generateRandom(111111, 999999);
        const email = req.body.email;

        if (email == undefined || email == ""){
            res.json({
                'code' : statusCode.CLIENT_ERROR
            })
        }

        const mailOptions = {
            from:"paper.pen.smu@gmail.com",
            to : email,
            subject : "회원가입 인증메일입니다.",
            text : "오른쪽 숫자 6자리를 입력해주세요 : " + number
        }

        console.log(number);

        const result = await smtpTransport.sendMail(mailOptions, function (err, responses) {
            var resultCode = statusCode.SERVER_ERROR;
            if (err){
               console.log(err);
            } else {
                resultCode = statusCode.OK;
            }
            res.json({
                'code' : resultCode,
                'authNumber' : number
            })
            smtpTransport.close();
        })
    },
    //좋아요 api
    setPostLike : function (req, res) {
        const postId = req.query.postid;
        const loggedUser = req.query.userid;
        const apiLikeCheckSql = `select * from likes where post_id=${postId} and user_id=${loggedUser};`;

        if (postId == undefined || postId == "" || loggedUser == undefined || loggedUser == ""){
            res.json({
                'code' : statusCode.CLIENT_ERROR
            })
        }
        connection.query(apiLikeCheckSql, function (err, result) {
            if (err){
                console.log(err);
                res.json({
                    'code' : statusCode.SERVER_ERROR
                })
            } else {
                var apiLikeSql = "";
                if (result.length == 0){
                    //사용자가 좋아요를 누른 상황
                    apiLikeSql = `insert into likes (post_id, user_id) values (${postId}, ${loggedUser});`;
                } else {
                    //사용자가 좋아요 취소를 누른 상황
                    apiLikeSql = `delete from likes where post_id=${postId} and user_id=${loggedUser};`;
                }
                connection.query(apiLikeSql, function (err, result) {
                    var code = statusCode.OK;
                    if (err){
                        console.log(err);
                        code = statusCode.SERVER_ERROR;
                    }
                     res.json({
                         'code' : code
                     })
                })
            }
        })

    },
    setPostKeep : function (req, res) {
        const postId = req.query.postid;
        const loggedUser = req.query.userid;
        const apiKeepCheckSql = `select * from keep where post_id=${postId} and user_id=${loggedUser};`;

        if (postId == undefined || postId == "" || loggedUser == undefined || loggedUser == ""){
            res.json({
                'code' : statusCode.CLIENT_ERROR
            })
        }
        connection.query(apiKeepCheckSql, function (err, result) {
            if (err){
                console.log(err);
                res.json({
                    'code' : statusCode.SERVER_ERROR
                })
            } else {
                var apiKeepSql = "";
                if (result.length == 0){
                    //사용자가 보관하기를 누른 상황
                    apiKeepSql = `insert into keep (post_id, user_id) values (${postId}, ${loggedUser});`;
                } else {
                    //사용자가 보관하기 취소를 누른 상황
                    apiKeepSql = `delete from keep where post_id=${postId} and user_id=${loggedUser};`;
                }
                connection.query(apiKeepSql, function (err, result) {
                    var code = statusCode.OK;
                    if (err){
                        console.log(err);
                        code = statusCode.SERVER_ERROR;
                    }
                    res.json({
                        'code' : code
                    })
                })
            }
        })

    },
    insertPostComment : function (req, res) {
        var postId = req.body.postid;
        var userId = req.body.userid;
        var text = req.body.comment; //공백으로 오는 것을 어디서 걸러줄 것인가.

        var commentInsertSql = `insert into comment(user_id, post_id, text, c_time, c_date) values(?, ?, ?, curtime(), curdate());`
        var commentParams = [userId, postId, text];

        if (postId == undefined || postId == "" || userId == undefined || userId == ""){
            res.json({
                'code' : statusCode.CLIENT_ERROR
            })
        }
        connection.query(commentInsertSql, commentParams, function (err, result) {
            var code = statusCode.OK;
            if (err){
                console.log(err);
                code = statusCode.SERVER_ERROR;
            }
             res.json({
                 'code' : code
             })
        })
    },
    deletePostComment : function (req, res) {
        var postId = req.query.postid;
        var commentId = req.query.commentid;

        var commentDeleteSql = `delete from comment where id=? and post_id=?;`
        var commentParams = [commentId, postId];
        connection.query(commentDeleteSql, commentParams, function (err, result) {
            var code = statusCode.OK;
            if (err){
                console.log(err);
                code = statusCode.CLIENT_ERROR;
            }
             res.json({
                 'code' : code
             })
        })
    },

    //출석체크
    dailyAttendance: function(req, res) {
        console.log("dailyAttendance");

        var userId = req.body.id;
        var sql = `select last_login from user where id = ${userId}`

        connection.query(sql, function (err, result) {

            if (err) {
                console.log(err);
                res.json({
                    'code': statusCode.SERVER_ERROR
                })
                return;
            }
            if (userId == undefined || result.length == 0) { //없는 아이디거나..
                res.json({
                    'code': statusCode.CLIENT_ERROR
                })
                return;
            }
            const lastYearMonthDate = generateCurrentDate();
            if (result[0].last_login != lastYearMonthDate) {
                if (attendanceCheck(req, res, userId) == false) { // 업데이트 실패함
                    res.json({
                        'code': statusCode.SERVER_ERROR
                    })
                }
                else { //출석체크 성공
                    res.json({
                        'code': statusCode.OK,
                        'id': userId
                    })
                }
            }
            else { //첫출석 아님
                res.json({
                    'code': statusCode.NO,
                    'id': userId
                })
            }
        })

    },
    dailyAttendance_id: function (req, res, userId) {

        var sql = `select last_login from user where id = ${userId}`

        connection.query(sql, function (err, result) {

            if (err) {
                console.log(err);
                res.json({
                    'code': statusCode.SERVER_ERROR
                })
                return;
            }

            const lastYearMonthDate = generateCurrentDate();
            if (result[0].last_login != lastYearMonthDate) {
                if (attendanceCheck(req, res, userId) == false) { //업데이트 실패함
                    res.json({
                        'code': statusCode.SERVER_ERROR
                    })
                    return;
                }
                else { //출석체크 성공
                    res.json({
                        'code': statusCode.OK,
                        'id': userId
                    })
                }
            }
            else { //첫출석 아님
                res.json({
                    'code': statusCode.NO,
                    'id': userId
                })
            }
        })
    },
    postCheckPassword : function (req, res) {
        const userId = req.body.userid;
        const password = req.body.password;

        if (userId == undefined || password == undefined || password == ""){
            res.json({
                'code' : statusCode.CLIENT_ERROR, //no data
            })
        } else {
            var postSql = `select * from user where id = ${userId};`;
            connection.query(postSql, function (err, result) {
                if (err){
                    console.log(err);
                    res.json({
                        'code' : statusCode.SERVER_ERROR,
                    })
                } else {
                    if (result.length == 0){
                        res.json({
                            'code' : statusCode.CLIENT_ERROR, //no user
                        })
                    } else {
                        crypto.pbkdf2(password, result[0].salt, 100, 64, 'sha512', function (err, key) {
                            if (key.toString('base64') !== result[0].password) {
                                res.json({
                                    'code': statusCode.CLIENT_ERROR, //incorrect message
                                })
                            } else {
                                res.json({
                                    'code': statusCode.OK,
                                })
                            }
                        })
                    }
                }
            })
        }


    }
}

module.exports = apiController;
