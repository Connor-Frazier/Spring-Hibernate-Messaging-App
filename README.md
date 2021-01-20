# Spring-Hibernate-Messaging-App

### Authors: Connor Frazier, Neel Deshprande, Zoheb Nawaz, Sameer Sinha

### Summary:
As a group we were tasked with creating messaging app backend application that would include typical features found in modern day messaging apps. This included users creating an account, those users to create groups, news feeds of various activity on the app, real time messaging between users and/or groups, as well as messaging to offline users. Additional notable features included allowing for a "goverment agent" to submit a subpoena to see the messages of a certain user, groups to be within groups, JWT tokens for security using the spring framework, Cross Site Scripting protection, and more. This project was completed over one month using weekly sprints that included requirement gathering, architecture designing, scrums, planning sessions, and reviews with management(the teaching staff). Our group chose to create a spring hibernate application to serve this purpose with layers that included controller, service, data, model, and more. 


### My Responsibilites:

I had three main responsibilities for this project. 

The first was the messages creation and management from the model layer to the endpoint level. This included contributing to the messages databse table design, writing the message object model and related models, the data layer to access the database, the service layer to control the manipulation of messages and their data, creating ways for the websocket controller to interact with the messages infrasturcture, and finally writing endpoints that client side apps would need for access to message related data.

The second was contributing to the handling of the messages and user connections through the websocket(used for delivering messages in real time) on both the server side and client side applications. This included adding previous domain knowledge, and contributing to the implementing of websocket features like user session management and message delivery/recieving.

Lastly I was responsible for the creation/management of the cloud infrastucutre for our project. One of the last requirements for the project was to run the app live in the cloud. For this I chose to use AWS applications including S3 buckets, EC2 instances, ELastic Beanstalk, and RDS.


