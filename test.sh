http get :8080/seed/all/light --session=./session.json
http post :8080/auth/forceLogin --session=./session.json
http get :8080/cache/sync/counter --session=./session.json
echo '{ "courseIds": [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30] }' | http post :8080/registration --session=./session.json --verbose
