const statusCode = require("../config/serverStatusCode");
const connection = require("../db/db");


const apiController_subFunc = {

    //checkLastLogin: function (req, res, userId) {

    //    var sql = `select last_login from user where id = ${userId}`

    //    connection.query(sql, function (err, result) {

    //        if (err) {
    //            console.log(err);
    //            res.json({
    //                'code': statusCode.SERVER_ERROR
    //            })
    //            return;
    //        }

    //        const lastYearMonthDate = generateCurrentDate();
    //        if (result[0].last_login != lastYearMonthDate) {
    //            if (attendanceCheck(req, res, userId) == false) { //������Ʈ ������ 
    //                res.json({
    //                    'code': statusCode.SERVER_ERROR
    //                })
    //                return;
    //            }
    //            else { //�⼮üũ ����         
    //                res.json({
    //                    'code': statusCode.OK,
    //                    'id': userId
    //                })
    //            }
    //        }
    //        else { //ù�⼮ �ƴ�
    //            res.json({
    //                'code': statusCode.NO,
    //                'id': userId
    //            })
    //        }
    //    })
    //},
    
}

module.exports = apiController_subFunc;