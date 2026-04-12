#!/bin/bash
BASE="http://localhost:8080/api"

echo "=== 1. Register ==="
REG=$(curl -s -X POST "$BASE/auth/register" -H "Content-Type: application/json" -d '{"studentId":"2024000099","username":"ExamAuto","email":"exam_auto@ustil.com","password":"Test123456!","confirmPassword":"Test123456!"}')
echo "$REG"

echo "=== 2. Set PROBATION role ==="
mysql -uroot -pyy3908533 syit_cpc -e "UPDATE users SET roles='ROLE_PROBATION' WHERE email='exam_auto@ustil.com';" 2>/dev/null

echo "=== 3. Login ==="
LOGIN=$(curl -s -X POST "$BASE/auth/login" -H "Content-Type: application/json" -d '{"email":"exam_auto@ustil.com","password":"Test123456!"}')
echo "$LOGIN"
TOKEN=$(echo "$LOGIN" | python3 -c "import sys,json;print(json.load(sys.stdin).get('data',{}).get('token',''))" 2>/dev/null)
if [ -z "$TOKEN" ]; then echo "FAIL"; exit 1; fi

echo "=== 4. Apply Promotion ==="
curl -s -X POST "$BASE/promotion/apply" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"motivation":"test"}'

echo -e "\n=== 5. Generate Paper ==="
PAPER=$(curl -s -X GET "$BASE/exam/generate-paper" -H "Authorization: Bearer $TOKEN")
echo "$PAPER" | python3 -c "import sys,json;d=json.load(sys.stdin);print(f'Code:{d[\"code\"]} Qs:{len(d.get(\"data\",{}).get(\"questions\",[]))}')" 2>/dev/null
PAPER_ID=$(echo "$PAPER" | python3 -c "import sys,json;print(json.load(sys.stdin).get('data',{}).get('paperId',''))" 2>/dev/null)

echo "=== 6. Submit ==="
PAYLOAD=$(echo "$PAPER" | python3 -c "
import sys,json
d=json.load(sys.stdin); data=d.get('data',{})
qs=data.get('questions',[])
ans=[{'questionId':q['id'],'answer':'A' if q['questionType']=='single_choice' else ('对' if q['questionType']=='true_false' else 'AB')} for q in qs]
print(json.dumps({'paperId':data.get('paperId'),'answers':ans}))
" 2>/dev/null)

SUBMIT=$(curl -s -X POST "$BASE/exam/submit" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "$PAYLOAD")
echo "$SUBMIT" | python3 -c "
import sys,json
d=json.load(sys.stdin); r=d.get('data',{})
print(f'Score:{r.get(\"score\")}/{r.get(\"maxScore\")} Passed:{r.get(\"passed\")}')
print(f'Single:{r.get(\"singleChoiceScore\")} Multi:{r.get(\"multipleChoiceScore\")} TF:{r.get(\"trueFalseScore\")}')
print(f'Details:{len(r.get(\"details\",[]))}')
" 2>/dev/null

echo -e "\n=== 7. Get Result ==="
RESULT=$(curl -s -X GET "$BASE/exam/result" -H "Authorization: Bearer $TOKEN")
echo "$RESULT" | python3 -c "
import sys,json
d=json.load(sys.stdin); r=d.get('data',{})
print(f'Score:{r.get(\"score\")} Passed:{r.get(\"passed\")} Attempts:{r.get(\"attemptCount\")}')
" 2>/dev/null

echo -e "\n=== 8. DB Check ==="
mysql -uroot -pyy3908533 syit_cpc -e "SELECT id,score,max_score,passed FROM exam_records ORDER BY id DESC LIMIT 1;" 2>/dev/null
mysql -uroot -pyy3908533 syit_cpc -e "SELECT exam_score,status FROM promotion_applications ORDER BY id DESC LIMIT 1;" 2>/dev/null

echo -e "\n✅ Tests done!"
