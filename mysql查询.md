当createtime 类型为bigint时 查询近七天的内容
```
SELECT l.* FROM member_log l LEFT JOIN member m ON l.`member_id`=m.`member_id`  WHERE (l.`create_time` BETWEEN  UNIX_TIMESTAMP(NOW())-7*24*60*60 AND 1543974547) AND l.`phone`=15618970790;
```
当createtime 类型为datatime时 查询近七天的内容
```
SELECT *
FROM member_log
WHERE member_id = (SELECT member_id FROM member WHERE phone = #para(phone))
AND create_time BETWEEN CURRENT_DATE()-7 AND SYSDATE();
```
### mysql查询时间的几个函数
SELECT CURRENT_TIME(); -->20:30:20

SELECT NOW(); -->2018-02-11 20:22:22

SELECT SYSDATE(); --> 2018-02-11 20:22:22

SELECT CURRENT_DATE() FROM DUAL; --> 2018-02-11
