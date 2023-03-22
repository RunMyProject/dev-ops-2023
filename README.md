# dev-ops-2023
Check mime type of a file using WS REST
---

# [Contents](#contents)
1. - [Problem Solving](#problem-solving)
2. - [Instructions For Use](#instructions-for-use)
3. - [Architecture Introduction](#architecture-introduction)
4. - [Motivation](#motivation)
5. - [Image Orchestration](#image-orchestration)
6. - [Docker build Steps](#docker-build-steps)
7. - [DBeaver Configuration](#dbeaver-configuration)
8. - [Spring Architecture Drawing](#spring-architecture-drawing)
9. - [Structure Source Codes](#structure-source-codes)

---

**Requirements:**

jdk:**>=8**

Framework: if possible **Spring >= 4.0**, but other solutions are also accepted

Database: **MySql**

---

## Problem Solving
[|-> go back contents](#contents)

**Problem:** Verify **mime type** of a file using **ws Rest.**

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

## Instructions For Use
[|-> go back contents](#contents)

### Shell:

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

![Fig. 1 ](images/fig_1.png)

6. test the new authentication with the Application's test hello call:
    1. in GET call: http://localhost:6868/hello
    2. in HEADERS insert in the Authorization field a string Bearer and the value of the token:
       - Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlZG9hcmRvIiwiZXhwIjoxNjc5NDQxODEzLCJpYXQiOjE2Nzk0NDEyMTN9.LljC3TSK0Tj42ihwq6ZRFe06EvhHITUUo5eyW6NJCnjnm5HOzJ-RXU30TU_JtwgX8JjQ4GF3R_8dc9BH3RxvDA
    3.  you should see Hello World! with Status 200 OK

![Fig. 2 ](images/fig_2.png)

7. if you want to exit just invoke the GET logout API with the token in the HEADER::
    1. in GET call: http://localhost:6868/logout
    2. in HEADERS insert in the Authorization field a string Bearer and the value of the token:
        - Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlZG9hcmRvIiwiZXhwIjoxNjc5NDQxODEzLCJpYXQiOjE2Nzk0NDEyMTN9.LljC3TSK0Tj42ihwq6ZRFe06EvhHITUUo5eyW6NJCnjnm5HOzJ-RXU30TU_JtwgX8JjQ4GF3R_8dc9BH3RxvDA
    3.  you should see a white screen with Status 200 OK
    4. you can test the hello API again to see it in "unauthorized" status

![Fig. 3 ](images/fig_3.png)
 
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

![Fig. 4 ](images/fig_4.png)

9. to upload a file, use the upload API:
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

![Fig. 5 ](images/fig_5.png)

10. you can always see and monitor the uploaded files:
    1. in GET call: http://localhost:6868/wsrest/file/getAll
    2. in HEADERS enter in the Authorization field like the other steps
    3. you should see a JSON structure:
       - [{"fileName": "simple.txt", "fileUrl": "http://localhost:6868/wsrest/download/simple.txt", "message": "List all files with success!" }]
    
![Fig. 6 ](images/fig_6.png)

11. if you don't want to waste much time, you can always use the multi-upload API to upload multiple files at the same time!
    1. in POST call: http://localhost:6868/wsrest/file/uploadfiles
    2. in HEADERS enter in the Authorization field like the other steps
    3. in BODY insert in the file field and choosing the FILE type and
       selecting the file to load in the value field (n. files selected)
    4. you should see a long JSON structure based on number files:
       - [{ "fileName": ......
       - ...]
       
![Fig. 7 ](images/fig_7.png)

12. finally, to validate all files as required, use validation:
    1. in GET call: http://localhost:6868/wsrest/validation
    2. in HEADERS enter in the Authorization field like the other steps
    3. you should see a long JSON structure based on number files:
       - [ {"validationList": [
         { "filename": "simple.txt", "validated": true } ],
         "page": 1 }]

![Fig. 8 ](images/fig_8.png)

---
Architecture Solution and Software Development Life-Cycle (SDLC)
---
---

## Architecture Introduction
[|-> go back contents](#contents)

The architecture is a classic **SecDevOps model**, where **Security** is the priority to develop at its best.

Why are more companies choosing the **new SecDevOps model** today?

The transition from the **Waterfall** software development model to **Agile**,
to finally reach **DevOps**, makes it necessary to profoundly change the ways 
in which the organization can/must exercise the **operational management of security** 
within one of the most **delicate processes** (for example the life cycle).

The **Agile evolution** sees its main innovation in the iterative approach
to development, allowing changes to product requirements at any time.

### DevOps takes it one step further, bringing together development and operations into a single lifecycle in favor of greater efficiency and automation of the aforementioned processes.

As a result of these changes, the **time** it takes to build projects 
has **decreased significantly!!! :-)** 
from a release **every six months**, to a release **every week or two**, 
to a **release every few days.....!**

**DevOps**, in fact, undermines the traditional concept of service 
by **adapting** it to the **speed of business** change!

In DevOps, very **few processes and services** are viewed and maintained
as **static entities** to compete in a growing market where the ability 
to rapidly adapt **to ever changing needs is growing!**

Therefore, **security issues** can no longer be addressed only at the end
of the cycle which is tolerable in the **waterfall model!** 

So, the implementations and security fixes addressed 
only **before the release of the software** become too burdensome, 
too expensive and actually prevent the full implementation of the new model.

According to **SecDevOps**, **all DevOps professionals** must also be
**security professionals**. 

In this way, as many **security tasks** as possible can be performed 
within the daily routine of the **DevOps team**, supported by a minimal
specialist team that focuses on defining policies, 
overseeing **continuous delivery** and **executing complex tasks**.

---

## Motivation
[|-> go back contents](#contents)

The **real challenge of this project** is not so much linked to the 
selection of a single solution that alone satisfies the **Security component** 
of SecDevOps, as to the ability to understand that a correct and 
reasoned use of every means available contributes decisively to 
**the objective to make Security an ever-present and indispensable condition!**

---

## Image Orchestration
[|-> go back contents](#contents)

The architecture is composed via **Docker-Compose**, 
a **MySQL image (mysqldb port)** and a Web Application image in **Spring-Boot 2.2.1.RELEASE (myapp port)**.

A "hidden" **.env** file takes care of the environment variables to pass them to the two systems.

For **mysqldb** *(unless-stopped)* when the container is stopped (manually or otherwise), it is not restarted even after Docker daemon restarts.

For **myapp** *(on-failure)* Docker daemon restarts the container if it exits due to an error, which manifests as a non-zero exit code.

## Docker build Steps
[|-> go back contents](#contents)

When you invoke **docker-compose build** Docker will build the Spring build via the Dockerfile
same folder: 

1. first step: to avoid maven library load delay latency, it copies only pom.xml of checkmime project and download dependencies;
2. second step: Docker copies all other checkmime project files and creates the final project with Maven;

### Note to developer (application.properties) - running the system locally:

- Once you have mounted the two images, you can always run the tests with Maven from the source folder and access the MySQL DB;
-  (or run the single image launch command **docker-compose start mysqldb** );
- to do this, follow the steps below:
    1. invoke command from shell **docker ps**;
  2. search for the MySQL container identifier **(MYSQL_container id)**;
  3. run **docker inspect <MYSQL_container id>**;
  4. then look for the IP address of the virtual machine, you can freely use filter commands like **grep**;
  5. you should find a string like: **"IPAddress": "172.18.0.2"**;
  6. change line no. 40 of application.properties under Spring Project Resources;
  7. spring.datasource.url=jdbc:mysql://**172.18.0.2**:3306/checkmime_db?useSSL=false&allowPublicKeyRetrieval=false&serverTimezone=UTC;
  8. while if if you work inside docker images then you will use access of address mapped in **docker-compose.yml (mysqldb)**;
  9. spring.datasource.url=jdbc:mysql://**mysqldb**:3306/checkmime_db?useSSL=false&allowPublicKeyRetrieval=false&serverTimezone=UTC;
  10. now you can run **mvn test** or **mvn spring-boot:run** to run your code locally!

---

## DBeaver Configuration
[|-> go back contents](#contents)

To configure your Client DB (example DBeaver) follow the same steps of the previous step!

![DBeaver_config_1.png]( images/DBeaver_config_1.png)

![DBeaver_config_2.png]( images/DBeaver_config_2.png)

---

## Spring Architecture Drawing
[|-> go back contents](#contents)

Below is a simple diagram of the communication between the various blocks of the application.

![Architect.png]( images/Architect.png)

---

## Structure Source Codes
[|-> go back contents](#contents)

TODO
