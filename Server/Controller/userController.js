/*
 * 경로에 따라서 실행될 함수들
 * 명명규칙 : 분류 + 기능 + restful방식
 */

 var connection = require("../db/db");
 var statusCode = require("../config/serverStatusCode");
 const serverConfig = require("../config/serverConfig");

 const crypto = require('crypto');

 const s3 = require("../config/s3");

 var userController = {
     // 프로필
     userProfilePost : function (req, res) {
       const id = req.body.id;
       const status = req.body.status;

       var params = [id, id, id, id];
       var follower_cnt;
       var follow_cnt;
       var post_info = [];
       var profile_info = [];

       var sql1 = 'SELECT COUNT(*) AS cnt FROM user JOIN follow ON follow.follower_id = user.id WHERE user.login_id = ?;'; // 팔로우 수
       var sql2 = 'SELECT COUNT(*) AS cnt FROM user JOIN follow ON follow.follow_target_id = user.id WHERE user.login_id = ?;'; // 팔로워 수
       var sql3 = 'SELECT post.id, post.image FROM user JOIN post ON user.id = post.user_id WHERE user.login_id = ? order by post_date desc, post_time desc;'; // 게시글목록
       var sql4 = 'SELECT * FROM user WHERE login_id = ?;'; // 프로필 데이터(포인트, 소개글, sns 주소)
       var sql5 = 'SELECT COUNT(*) AS flag FROM user JOIN follow ON follow.follower_id = user.id WHERE user.login_id = ? AND follow_target_id IN (SELECT id FROM user WHERE login_id = ?);'
       var sql = sql1 + sql2 + sql3 + sql4;

       if(status !== 1){ // 선택한 사용자의 프로필일 경우
         const user_id = req.body.user_id;
         console.log(id + " " + user_id);

         sql += sql5;
         params = [];
         params.push(user_id, user_id, user_id, user_id, id, user_id);
       }

         connection.query(sql, params, function(err, results){
           var resultCode = statusCode.CLIENT_ERROR;
           console.log(id)

           if (err) {
             console.log(err);
             res.json({
               'code': resultCode
             })
           }
           else{
             resultCode = statusCode.OK;
             follow_cnt = results[0][0].cnt;
             follower_cnt = results[1][0].cnt;

             for(let i = 0; i < results[2].length; i++){
               var pdata = {
                   'image': results[2][i].image,
                   'id': results[2][i].id
               };
               post_info.push(pdata);
             }
               console.log(post_info);
             var prodata = {
               'img_profile': results[3][0].img_profile,
               'intro': results[3][0].intro,
               'sns': results[3][0].sns_url,
               'userId': results[3][0].login_id,
               'sns_check':results[3][0].sns_check
             }

             if(status === 1){ // 로그인한 사용자의 프로필일 경우
               prodata.point = results[3][0].point;
             }
             else{
               prodata.flag = results[4][0].flag;
             }

             profile_info.push(prodata);
             console.log(profile_info);
             res.json({
               'code': resultCode,
               'followerCnt': follower_cnt,
               'followCnt': follow_cnt,
               'postInfo': post_info,
               'profileInfo': prodata
             })
           }
           console.log(resultCode);
         })
     },

     // 팔로우
     userFollowPost: function(req,res){
       const login_id = req.body.loginId;
       const user_id = req.body.userId;

       var params = [login_id, user_id];
       var sql1 = 'SELECT id FROM user WHERE login_id = ?;';
       var sql2 = 'SELECT id FROM user WHERE login_id = ?;';
       connection.query(sql1 + sql2, params, function(err, rows){
         var resultCode = statusCode.CLIENT_ERROR;

         if(err){
           console.log(err);
           res.json({
             'code': resultCode
           })
         }
         else{
           resultCode = statusCode.OK;
           var sql = 'INSERT INTO follow VALUES(?,?);';
           connection.query(sql, [rows[0][0].id, rows[1][0].id], function(err, rows){
             if(err){
               console.log(err);
               resultCode = statusCode.CLIENT_ERROR;
               res.json({
                 'code': resultCode
               })
             }
           })
           res.json({
             'code': resultCode
           })
         }
         console.log(resultCode);
       })
     },

     // 언팔로우
     userUnfollowPost : function(req,res){
       const login_id = req.body.loginId;
       const user_id = req.body.userId;

       var params = [login_id, user_id];
       var sql1 = 'SELECT id FROM user WHERE login_id = ?;';
       var sql2 = 'SELECT id FROM user WHERE login_id = ?;';

       connection.query(sql1 + sql2, params, function(err, rows){
         var resultCode = statusCode.SERVER_ERROR;

         if(err){
           console.log(err);
           res.json({
             'code': resultCode
           })
         }
         else{
           resultCode = statusCode.OK;
           var sql = 'DELETE FROM follow WHERE follower_id = ? AND follow_target_id = ?;';
           connection.query(sql, [rows[0][0].id, rows[1][0].id], function(err, rows){
             if(err){
               console.log(err);
               resultCode = statusCode.CLIENT_ERROR;
               res.json({
                 'code': resultCode
               })
             }
           })
           res.json({
             'code' : resultCode
           })
         }
         console.log(resultCode);
       })
     },

     // 로그인한 사용자의 팔로우 리스트 얻을 경우 --> userlFollowList
     // 선택한 사용자의 팔로우 리스트 얻을 경우 --> userFollowList

     // 로그인한 사용자의 팔로우 리스트
     userLFollowList: function(req, res){
       const id = req.body.id;
       var param = [id];
       var follow_info = [];

       var sql = 'SELECT * FROM user JOIN follow ON follow.follow_target_id = user.id WHERE follow.follower_id = (SELECT id FROM user WHERE login_id = ?);'; // 로그인한 사용자의 팔로우 리스트
       connection.query(sql, param, function(err, rows){
         var resultCode = statusCode.CLIENT_ERROR;

         if(err){
           console.log(err);
           res.json({
             'code': resultCode
           })
         }
         else{
           resultCode = statusCode.OK;

           for(let i = 0; i < rows.length; i++){
             var followInfo = {
               'image': rows[i].img_profile,
               'userId': rows[i].login_id
             };
              console.log(rows[i].login_id);
             follow_info.push(followInfo);
           }

           res.json({
             'code': resultCode,
             'followinfo': follow_info
           })
         }
         console.log(resultCode);
       })
     },

     // 팔로우 리스트
     userFollowList : function(req, res){
       const id = req.body.id;
       const user_id = req.body.user_id;
       var params = [id, user_id];
       var login_follow_info = [];
       var user_follow_info = [];

       var sql = 'SELECT * FROM user JOIN follow ON follow.follow_target_id = user.id WHERE follow.follower_id = (SELECT id FROM user WHERE login_id = ?);'; // 선택한 사용자의 팔로우 리스트
       sql += sql;
       connection.query(sql, params, function(err, rows){
         var resultCode = statusCode.CLIENT_ERROR;

         if(err){
           console.log(err);
           res.json({
             'code': resultCode
           })
         }
         else{
           resultCode = statusCode.OK;

           for(let i = 0; i < rows[0].length; i++){
             var loginFollowInfo = {
               'userId': rows[0][i].login_id
             };
             login_follow_info.push(loginFollowInfo);
           }

           for(let i = 0; i < rows[1].length; i++){
             var userFollowInfo = {
               'image': rows[1][i].img_profile,
               'userId': rows[1][i].login_id
             };
             user_follow_info.push(userFollowInfo);
           }

           res.json({
             'code': resultCode,
             'loginFollowInfo': login_follow_info,
             'userFollowInfo': user_follow_info
           })
         }
         console.log(resultCode);
       })
     },

     // 팔로워 리스트
     userFollowerList : function(req, res){
       const id = req.body.id;
       const status = req.body.status;
       var params = [id, id];
       var follower_info = [];
       var following_info = [];

       var sql = 'SELECT * FROM user JOIN follow ON follow.follower_id = user.id WHERE follow.follow_target_id = (SELECT id FROM user WHERE login_id = ?);'; // 사용자의 팔로워 리스트
       var sql2 = 'SELECT * FROM user JOIN follow ON follow.follow_target_id = user.id WHERE follow.follower_id = (SELECT id FROM user WHERE login_id = ?);'
       if(status !== 1){
         params = [];
         params.push(req.body.user_id, id);
       }
       connection.query(sql+sql2, params, function(err, rows){
         var resultCode = statusCode.CLIENT_ERROR;

         if(err){
           console.log(err);
           res.json({
             'code': resultCode
           })
         }
         else{
           resultCode = statusCode.OK;

           for(let i = 0; i < rows[0].length; i++){
             var followerInfo = {
               'image': rows[0][i].img_profile,
               'userId': rows[0][i].login_id
             };
             follower_info.push(followerInfo);
           }

           for(let i = 0; i < rows[1].length; i++){
             var followingInfo = {
               'userId': rows[1][i].login_id
             };
             following_info.push(followingInfo);
           }

           res.json({
             'code': resultCode,
             'followerInfo': follower_info,
             'followingInfo': following_info
           })
         }
         console.log(resultCode);
       })
     },

     // 보관함
     profileKeep : function(req, res){
       const id = req.body.login_id;
       var params = [id, id];
       var keep_info = [];
       var keep_cnt;
       var profile_img;

       var sql = 'SELECT * FROM post JOIN keep ON post.id = keep.post_id WHERE keep.user_id = (SELECT id FROM user WHERE login_id = ?);'; // 보관함목록
       var sql2 = 'SELECT * FROM user WHERE login_id = ?;';
       connection.query(sql+sql2, params, function(err, rows){
         var resultCode = statusCode.SERVER_ERROR;

         if(err){
           console.log(err);
           res.json({
             'code': resultCode
           })
         }
         else{
           resultCode = statusCode.OK;

           for(let i = 0; i < rows[0].length; i++){
             var kdata = {
               'image' : rows[0][i].image,
               'id': rows[0][i].post_id
             };
             keep_info.push(kdata);
           }
           keep_cnt = rows[0].length;
           profile_img = rows[1][0].img_profile;
           console.log(keep_info);
           console.log(keep_cnt);
           res.json({
             'code' : resultCode,
             'keepInfo' : keep_info,
             'keepCnt': keep_cnt,
             'profileImg': profile_img
           })
         }
       })
     },

     // 아이디 변경
     userIdChange : function(req, res) {
       const id = req.query.login_id;
       const new_id = req.query.new_id;
       var params = [new_id, id];

       console.log(id, new_id);

       var sql = 'UPDATE user SET login_id = ? WHERE login_id = ?;';
       connection.query(sql, params, function(err, rows){
         var resultCode = statusCode.SERVER_ERROR;

         if(err){
           console.log(err);
         }
         else{
           resultCode = statusCode.OK;
         }

         res.json({
           'code': resultCode
         })
       })
     },

     // 비밀번호 변경
     userPwChange : function(req, res){
       const id = req.body.userid;
       const pw = req.body.password;
       var resultCode = statusCode.SERVER_ERROR;

       crypto.randomBytes(64, function(err, buf){
         if(err){
           console.log(err);
           res.json({
             'code' : resultCode
           })
         }
         crypto.pbkdf2(pw, buf.toString('base64'), 100, 64, 'sha512', function(err, key){
           if(err){
             console.log(err);
             res.json({
               'code' : resultCode
             })
           }
           var hashedPassword = key.toString('base64');
           var salt = buf.toString('base64');

           var sql = 'UPDATE user SET password = ?, salt = ? WHERE id = ?;';
           var params = [hashedPassword, salt, id];
           connection.query(sql, params, function(err, rows){
             if(err){
               console.log(err);
             }
             else{
               resultCode = statusCode.OK;
             }

             res.json({
               'code': resultCode
             })
           })
         })
       })
     },

     // 프로필수정
     profileEdit : function(req, res) {
       const is_sns_check = Number(req.body.sns_check_flag);
       const is_img_check = Number(req.body.img_check_flag);
       const id = req.body.login_id;
       const new_intro = req.body.new_intro;
       const new_sns = req.body.new_SNS;
       var new_image;
       var param = [id];
       var check_cnt = 0;

       // 수정
       // var fileString = `${req.file.path}`;
       // new_image = "/"+fileString.replace(/\\/g, '/');
       // //new_image = "/public/default/user.png";
       //
       // console.log("here");
       // console.log(new_image);
       // 수정 end

       var sql = 'SELECT * FROM user WHERE login_id = ?;';
       connection.query(sql, param, function(err, rows){
         var resultCode = statusCode.CLIENT_ERROR;

         if(err){
           console.log(err);
           res.json({
             'code': resultCode
           })
         }
         else{
           resultCode = statusCode.OK;
           sql = ""
           param = [];

           // 기존 소개글과 비교 후 db갱신
           if(rows[0].intro !== new_intro){
             sql += 'UPDATE user SET intro = ? WHERE login_id = ?;';
             param.push(new_intro, id);
             check_cnt += 1;
           }

           // 기존 SNS계정과 비교 후 db갱신
           if(is_sns_check === rows[0].sns_check){
             if(is_sns_check === 1){
               if(new_sns !== rows[0].sns_url){
                 sql += 'UPDATE user SET sns_url = ? WHERE login_id = ?;';
                 param.push(new_sns, id);
                 check_cnt += 1;
               }
             }
           }
           else if(is_sns_check !== rows[0].sns_check){
             if(is_sns_check === 1){
               sql += 'UPDATE user SET sns_url = ?, sns_check = ? WHERE login_id = ?;';
               param.push(new_sns, is_sns_check, id);
               check_cnt += 1;
             }
             else{
               sql += 'UPDATE user SET sns_url = ?, sns_check = ? WHERE login_id = ?;';
               param.push("", is_sns_check, id);
               check_cnt += 1;
             }
           }

           // 기존 프로필 이미지와 비교 후 db갱신
           if (req.file != undefined) {
             
             var fileName = req.body.old_profile_img;
             if (fileName != serverConfig.defaultUserProfile) {
               s3.deleteObject({
                 Bucket : serverConfig.s3BucketName,
                 Key: serverConfig.s3BucketProfileFolderName + fileName.split('/')[4]
                }, function (err, data) {
                  if (err) {
                    console.log(err);
                    res.json({
                      'code' : statusCode.SERVER_ERROR
                    })
                  }
                })
             }
            new_image = req.file.location;
            sql += 'UPDATE user SET img_profile = ? WHERE login_id = ?;';
            param.push(new_image, id)
            check_cnt += 1;
           }
           console.log(sql, param, check_cnt);

           if(check_cnt > 0){
            connection.query(sql, param, function(err, rows){
              if(err){
                resultCode = statusCode.SERVER_ERROR;
                return res.json({
                  'code': resultCode
                })
              }
              else{
                return res.json({
                  'code': resultCode
                })
              }
            })
          }
          else{
            res.json({
              'code': resultCode
            })
          }
         }
       })
     },

     // 회원 탈퇴
     userDataDelete : function(req, res) {
       const loginId = req.body.id;

       if (loginId == undefined || loginId == ''){
         res.json({
           'code' : statusCode.CLIENT_ERROR
         })
       } else {

         var sql = `select * from user where login_id = '${loginId}';`;
         connection.query(sql, function (err, rows) {
           if (err){
             console.log(err);
             res.json({
               'code' : statusCode.SERVER_ERROR
             })
           } else {
             var userRecordId = rows[0].id;
             var imgProfile = rows[0].img_profile;

             sql = `select image from post where user_id = ${userRecordId};`;
             sql += `select image from market where user_id = ${userRecordId};`;
             connection.query(sql, function (err, rows) {
               if (err){
                console.log(err);
                res.json({
                  'code' : statusCode.SERVER_ERROR
                })
               } else {
                 //Delete user profile, user uploaded posts in S3.
                var params = {
                  Bucket: serverConfig.s3BucketName, 
                  Delete: {
                   Objects: [], 
                   Quiet: false
                  }
                 };
                 for(var i = 0;i < rows[0].length; i++) {
                   var object = {
                     Key : serverConfig.s3BucketPostFolderName + rows[0][i].image.split('/')[4]
                   }
                   params.Delete.Objects.push(object);
                 }
                 for(var i = 0;i < rows[1].length; i++) {
                  var object = {
                    Key : serverConfig.s3BucketMarketFolderName + rows[1][i].image.split('/')[4]
                  }
                  params.Delete.Objects.push(object);
                }
                 if (imgProfile != serverConfig.defaultUserProfile) {
                   var profileObject = {
                     Key : serverConfig.s3BucketProfileFolderName +  imgProfile.split('/')[4]
                   }
                   params.Delete.Objects.push(profileObject);
                 }
                console.log(params.Delete);
                 if (params.Delete.Objects.length > 0) {
                   s3.deleteObjects(params, function(err, data) {
                     if (err){
                       console.log(err) // an error occurred
                       res.json({
                        'code' : statusCode.SERVER_ERROR
                      })
                     } else {
                       //Delete user record in mysqlDB.
                       sql = `DELETE FROM user WHERE login_id = '${loginId}';`
                       connection.query(sql, function(err, rows){
                         var resultCode = statusCode.SERVER_ERROR;
                
                         if(err) {
                           console.log(err);
                           res.json({
                             'code': resultCode
                           })
                         }
                         else{
                           resultCode = statusCode.OK;
                           res.json({
                             'code': resultCode
                           })
                         }
                       })
                    }
                  });
                 } else {
                   //Delete user record in mysqlDB.
                   sql = `DELETE FROM user WHERE login_id = '${loginId}';`
                   connection.query(sql, function(err, rows){
                     var resultCode = statusCode.SERVER_ERROR;
            
                     if(err) {
                       console.log(err);
                       res.json({
                         'code': resultCode
                       })
                     }
                     else{
                       resultCode = statusCode.OK;
                       res.json({
                         'code': resultCode
                       })
                     }
                   })
                 }
                }
             })
           }
         })
       }


    }
 }

 module.exports = userController;
