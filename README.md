# dev-ops-2023
Check mime type of a file using WS REST
---

**Problem Solving:** Verify mime type of a file using **ws Rest.**

Given **a folder of the file system (input parameter)** which contains files in **various formats,** 
including the case of signed files **(p7m)**. it must be checked for each of them if the mime type coincides
with one of those present on **a table in the db.**

In case of positive verification the file is considered validated 
and an **ok** is returned, otherwise a **ko.**

The **p7m files** must be de-enveloped and the mime of the file without the envelope checked. 
So in the case of an **a.pdf.p7m file** the verification must be done on the file.

Define the structure of the table and map it and manage the queries necessary for the operation of the application via **JPA.**

The **ws rest** to be developed must also include an **input authentication mechanism.** 

The choice of the authentication mechanism is left free, but mechanisms that guarantee **maximum security are to be preferred.**

The **ws** must provide as an **input parameter the folder containing the files** and must return a list of objects in **json format** as a response. 

The answer must be **paginated.**

Each object represents a file with its **validation status.** 

## Example:

if the **a.pdf file** is validated the response will be like this:

**filename:a.pdf,**

**validated:true**

**if not**

**filename:a.pdf,**

**validated:false**

It is also required to define the tests to verify correct functioning. 

**Document the solution** and the developed code.

---
# Instructions for Use

### shell:

1. git clone https://github.com/RunMyProject/dev-ops-2023.git
2. cd dev-ops-2023
3. docker compose build
4. docker compose up or docker compose up -d in background

### open the Postman tool or similar to invoke the REST API
 
5. in POST call: http://localhost:6868/authenticate
   1. in Body RAW JSON:
      - { "username": "edoardo", "password": "password" }
   2. press "Send" key and note the result of the token, example:
      - {
      - "token":
      - "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlZG9hcmRvIiwiZXhwIjoxNjc5NDQxODEzLCJpYXQiOjE2Nzk0NDEyMTN9.LljC3TSK0Tj42ihwq6ZRFe06EvhHITUUo5eyW6NJCnjnm5HOzJ-RXU30TU_JtwgX8JjQ4GF3R_8dc9BH3RxvDA"
      - }
6. test the new authentication with the Application's test hello call:
    1. in GET call: http://localhost:6868/hello
    2. in HEADERS insert in the Authorization field a string Bearer and the value of the token:
       - Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlZG9hcmRvIiwiZXhwIjoxNjc5NDQxODEzLCJpYXQiOjE2Nzk0NDEyMTN9.LljC3TSK0Tj42ihwq6ZRFe06EvhHITUUo5eyW6NJCnjnm5HOzJ-RXU30TU_JtwgX8JjQ4GF3R_8dc9BH3RxvDA
    3.  you should see Hello World! with Status 200 OK
7. if you want to exit just invoke the GET logout API with the token in the HEADER::
    1. in GET call: http://localhost:6868/logout
    2. in HEADERS insert in the Authorization field a string Bearer and the value of the token:
        - Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlZG9hcmRvIiwiZXhwIjoxNjc5NDQxODEzLCJpYXQiOjE2Nzk0NDEyMTN9.LljC3TSK0Tj42ihwq6ZRFe06EvhHITUUo5eyW6NJCnjnm5HOzJ-RXU30TU_JtwgX8JjQ4GF3R_8dc9BH3RxvDA
    3.  you should see a white screen with Status 200 OK
    4. you can test the hello API again to see it in "unauthorized" status
8. to see the formats prepared in the db:
    1. in GET call: http://localhost:6868/wsrest/db/checkmimes/
    2. in HEADERS enter in the Authorization field like the other steps
    3. you should see a JSON structure:
       - [
       {
       "id": 1,
       "format": "PDF",
       "description": "application/pdf",
       "enabled": true
       }, ...
       - ...]
9. per caricare un file, utilizzare l'API di caricamento upload:
     1. in POST call: http://localhost:6868/wsrest/file/uploadfile
     2.  in HEADERS enter in the Authorization field like the other steps
     3. in BODY insert in the file field and choosing the FILE type and
        selecting the file to load in the value field
     4. you should see a JSON structure:
        - {
   "fileName": "simple.txt",
   "fileUrl": "http://localhost:6868/wsrest/download/simple.txt",
   "message": "File uploaded with success!"
   }    
    5.  note: in the git repository there is a folder to upload the most classic tests for the objective of the code challenge
10. you can always see and monitor the uploaded files:
    1. in GET call: http://localhost:6868/wsrest/file/getAll
    2. in HEADERS enter in the Authorization field like the other steps
    3. you should see a JSON structure:
       - [{"fileName": "simple.txt", "fileUrl": "http://localhost:6868/wsrest/download/simple.txt", "message": "List all files with success!" }]
11. if you don't want to waste much time, you can always use the multi-upload API to upload multiple files at the same time!
    1. in POST call: http://localhost:6868/wsrest/file/uploadfiles
    2. in HEADERS enter in the Authorization field like the other steps
    3. in BODY insert in the file field and choosing the FILE type and
       selecting the file to load in the value field (n. files selected)
    4. you should see a long JSON structure based on number files:
       - [{ "fileName": ......
       - ...]
12. finally, to validate all files as required, use validation:
    1. in GET call: http://localhost:6868/wsrest/validation
    2. in HEADERS enter in the Authorization field like the other steps
    3. you should see a long JSON structure based on number files:
       - [ {"validationList": [
         { "filename": "simple.txt", "validated": true } ],
         "page": 1 }]

---
