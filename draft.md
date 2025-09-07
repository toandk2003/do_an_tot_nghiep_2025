1) Cài snap
2) sudo snap install charmed-mongodb

SsVaSkaI2cAAxcLlS2KKyDC10yNcJdeuyK5gXeXo+oA=


4ac55a4a4188d9c000c5c2e54b628ac830b5d3235c25d7aec8ae605de5e8fa80

curl -X POST \
'http://localhost:8080/api/files/upload' \
-H 'Content-Type: multipart/form-data' \
-F 'file=@./1abadfb0846736396f76.jpg' \
-F 'checkSum=4ac55a4a4188d9c000c5c2e54b628ac830b5d3235c25d7aec8ae605de5e8fa80' \
-v



curl -X PUT \
'http://127.0.0.1:9000/avatar-private-bucket/0198e6fe-dac8-70c2-bc2f-7a259964d510_1abadfb0846736396f76.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20250826T152838Z&X-Amz-SignedHeaders=content-length%3Bcontent-type%3Bhost&X-Amz-Expires=86399&X-Amz-Credential=minioadmin%2F20250826%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=ceaa16bd12f6298877a9b98069cb66d50d78b5f78b34e2e45b225a3b2de9bde9'  \
-H 'content-type: image/jpeg' \
--data-binary @./1abadfb0846736396f76.jpg \
-v
  
  
  
  
  



  
