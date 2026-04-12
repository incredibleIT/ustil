#!/bin/bash

# 批量添加题目的脚本
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidHlwZSI6ImFjY2VzcyIsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlcyI6WyJST0xFX0FETUlOIl0sImlhdCI6MTc3NTYxMzI2MSwiZXhwIjoxNzc1Njk5NjYxfQ.WbgrIInLPjSUEoHms8XqvCd88chlNDVhM5l9wpv82rI"

echo "=== 批量添加单选题 ==="

# 单选题 1-8
curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"Java中哪个关键字表示继承关系?","questionType":"single_choice","options":["extends","implements","inherits","super"],"correctAnswer":"A","score":4}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"以下哪个是Java的基本数据类型?","questionType":"single_choice","options":["String","int","ArrayList","Object"],"correctAnswer":"B","score":4}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"Python中用于定义函数的关键字是?","questionType":"single_choice","options":["function","func","def","define"],"correctAnswer":"C","score":4}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"HTML中用于创建超链接的标签是?","questionType":"single_choice","options":["<link>","<a>","<href>","<url>"],"correctAnswer":"B","score":4}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"CSS中设置字体大小的属性是?","questionType":"single_choice","options":["text-size","font-size","size","font-style"],"correctAnswer":"B","score":4}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"JavaScript中声明常量的关键字是?","questionType":"single_choice","options":["var","let","const","final"],"correctAnswer":"C","score":4}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"MySQL中用于查询数据的SQL语句是?","questionType":"single_choice","options":["INSERT","UPDATE","SELECT","DELETE"],"correctAnswer":"C","score":4}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"Git中用于创建新分支的命令是?","questionType":"single_choice","options":["git new","git branch","git create","git add"],"correctAnswer":"B","score":4}'

echo ""
echo "=== 批量添加多选题 ==="

# 多选题 1-3
curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"以下哪些是Java的访问修饰符?","questionType":"multiple_choice","options":["public","private","protected","static","final"],"correctAnswer":"ABC","score":4}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"以下哪些是前端开发框架?","questionType":"multiple_choice","options":["Vue","React","Angular","Spring","Django"],"correctAnswer":"ABC","score":4}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"以下哪些是HTTP请求方法?","questionType":"multiple_choice","options":["GET","POST","PUT","PATCH","FETCH"],"correctAnswer":"ABCD","score":4}'

echo ""
echo "=== 批量添加判断题 ==="

# 判断题 1-10
curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"Java是一种面向对象的编程语言。","questionType":"true_false","correctAnswer":"对","score":2}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"HTML是一种编程语言。","questionType":"true_false","correctAnswer":"错","score":2}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"CSS用于设置网页的样式和布局。","questionType":"true_false","correctAnswer":"对","score":2}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"JavaScript和Java是同一种编程语言。","questionType":"true_false","correctAnswer":"错","score":2}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"Git是一个分布式版本控制系统。","questionType":"true_false","correctAnswer":"对","score":2}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"MySQL是一个关系型数据库管理系统。","questionType":"true_false","correctAnswer":"对","score":2}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"RESTful API必须使用XML格式传输数据。","questionType":"true_false","correctAnswer":"错","score":2}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"HTTPS比HTTP更安全。","questionType":"true_false","correctAnswer":"对","score":2}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"Python是一种解释型语言。","questionType":"true_false","correctAnswer":"对","score":2}'

curl -s -X POST http://localhost:8080/api/admin/questions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"questionText":"Docker是一种虚拟机技术。","questionType":"true_false","correctAnswer":"错","score":2}'

echo ""
echo "=== 批量添加完成 ==="
