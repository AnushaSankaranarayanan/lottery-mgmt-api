## Introduction
lotter-mgmt-api is a SpringBoot Application that implements a simple lottery system backed by MongoDB.
It facilitates creation of lottery tickets with n lines , check the status of lines on a ticket and updation of tickets with n
additional lines. Once the status of a ticket has been checked, it forbids further updation on the ticket.

## This API exposes the below methods
1. Create a lottery ticket with userId and lottery lines. Each line consists of 3 numbers(0, 1 or 2).
2. Get list of lottery tickets
3. Get a lottery ticket based on Id.
4. Update a lottery ticket using Id. Lottery lines can be amended with n additional lines 
5. Update/Check the status of a lottery ticket with the Id. Once the status of a ticket has been checked it will not be possible to update the ticket.Lines are sorted in the outcome based on the sortDirection provided.

Lottery resuts are computed using the below rule:
## Lottery Rules
You have a series of lines on a ticket with 3 numbers, each of which has a value of 0, 1, or 2. For each ticket if the sum of the values on a line is 2, the result for that line is 10.
Otherwise if they are all the same, the result is 5. Otherwise so long as both 2nd and 3rd numbers are different from the 1st, the result is 1. Otherwise the result is 0.

######## Swagger Docs can be found in /swagger-ui.html

## Design of the Mongo DB Table
******************
LotteryTicket:
******************
#######id  - Id of the Lottery Ticket. Auto Generated Primary Key
#######userId - User Id who created the ticket
#######createdDateTime - Datetime when the ticket was created
#######updatedDateTime - Datetime when the ticket was updated last
#######List<Line> - List of Lottery Lines
#######statusEnquired - Flag that checks the status of the lottery ticket

******************
Line:
******************
#######number - Lottery numbers of individual lines
#######result - Result of the above number


## List of end points

Once the application is running, navigate to http://localhost:8080/swagger-ui.html to see the end points that the API exposes

## Package Structure
All the java classes are organized in com.example.lotterymgmtapi under the respective sub packages.
#######LotteryTicket.java and Line.java  - represents the DB Model
#######LotteryTicketRequest.java and LotteryTicketResponse.java - represents the request and response entities. They are different from the DB Model because the status/result field should not be sent back in the response except for the status check PUT request.
#######LotteryRepository.java - DAO Class which uses Springboot's inbuilt Repository implementation for common DB operations.
#######LotteryService.java - service layer that handles the repository calls
#######LotteryController.java - controller layer that handles the calls to service methods.
#######LotteryMgmtApplication - Main Springboot Application
#######SwaggerConfig - handles the swagger 2 configuration
#######SecurityConfig - handles the security configuration
*************************
All test classes are under the src/test/java folder under the respective sub packages.
*************************

## Development Environment

Developed using IntelliJ IDEA Community Edition 20.1 on Ubuntu.

## Requirements

For building and running the application you need:

- [JDK 1.11](https://www.oracle.com/in/database/technologies/112010-win64soft.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

1. Set the environment variable SPRING_DATA_MONGODB_URI to point to a running MongoDB Instance.

```shell
export SPRING_DATA_MONGODB_URI=mongodb+srv://<username>:<password>@<cluster URL>/<database_name>?retryWrites=true&w=majority
```
2. There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.example.lotterymgmtapi.LotteryMgmtApiApplication` class from your IDE(Do not forget to set the ENV variable as mentioned above).

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

Once the application is up and running, issue requests to http://localhost:8080/

## Sample request and reponse
***********************
Sample JSON Request - Create a Ticket(POST) - http://localhost:8080/lotteryapi/v1/ticket/
***********************

	{
      "userId": "test3",
      "lines": [
      	"000",
      	"002",
      	"011",
      	"012"
      ]
    }
 
***********************
Sample JSON Response - Create a Ticket
***********************

	{
         "id": "5f2a525fd782b81ae150476d",
         "lines": [
             "000",
             "002",
             "011",
             "012"
         ],
         "userId": "test3",
         "createdDateTime": "2020-08-05T06:31:59.903+00:00",
         "updatedDateTime": "2020-08-05T06:31:59.903+00:00",
         "statusEnquired": false
     }


***********************
Sample JSON Response - Get Tickets By Id: http://localhost:8080/lotteryapi/v1/ticket/5f29e3c036e952151b624571
***********************

	{
         "id": "5f29e3c036e952151b624571",
         "lines": [
             "000",
             "002",
             "011",
             "022"
         ],
         "userId": "test",
         "createdDateTime": "2020-08-04T22:40:00.622+00:00",
         "updatedDateTime": "2020-08-04T22:44:47.142+00:00",
         "statusEnquired": false
     }
 
***********************
Sample JSON Response - Get All Tickets - http://localhost:8080/lotteryapi/v1/ticket/
***********************

	[
         {
             "id": "5f29e3c036e952151b624571",
             "lines": [
                 "000",
                 "002",
                 "011",
                 "022"
             ],
             "userId": "test",
             "createdDateTime": "2020-08-04T22:40:00.622+00:00",
             "updatedDateTime": "2020-08-05T06:29:16.002+00:00",
             "statusEnquired": true
         },
         {
             "id": "5f29e53636e952151b624572",
             "lines": [
                 "000",
                 "002",
                 "011"
             ],
             "userId": "test1",
             "createdDateTime": "2020-08-04T22:46:14.261+00:00",
             "updatedDateTime": "2020-08-04T22:46:46.717+00:00",
             "statusEnquired": true
         },
         {
             "id": "5f29e854ca9b7c758a57e5dd",
             "lines": [
                 "000",
                 "002",
                 "011"
             ],
             "userId": "test2",
             "createdDateTime": "2020-08-04T22:59:32.514+00:00",
             "updatedDateTime": "2020-08-04T23:05:53.507+00:00",
             "statusEnquired": false
         },
         {
             "id": "5f2a525fd782b81ae150476d",
             "lines": [
                 "000",
                 "002",
                 "011",
                 "012"
             ],
             "userId": "test3",
             "createdDateTime": "2020-08-05T06:31:59.903+00:00",
             "updatedDateTime": "2020-08-05T06:32:40.924+00:00",
             "statusEnquired": true
         }
     ]

***********************
Sample JSON Response - Amend Ticket Lines - PUT - http://localhost:8080/lotteryapi/v1/ticket/5f29e854ca9b7c758a57e5dd
***********************

	{
         "id": "5f29e854ca9b7c758a57e5dd",
         "lines": [
             "000",
             "002",
             "011",
             "010"
         ],
         "userId": "test2",
         "createdDateTime": "2020-08-04T22:59:32.514+00:00",
         "updatedDateTime": "2020-08-05T15:26:39.740+00:00",
         "statusEnquired": false
     }
 
***********************
Sample JSON Response - Retrieve status of ticket - PUT - http://localhost:8080/lotteryapi/v1/status/5f29e854ca9b7c758a57e5dd
***********************
	{
         "id": "5f29e854ca9b7c758a57e5dd",
         "lines": [
             {
                 "numbers": "002",
                 "result": 10
             },
             {
                 "numbers": "011",
                 "result": 10
             },
             {
                 "numbers": "000",
                 "result": 5
             },
             {
                 "numbers": "010",
                 "result": 0
             }
         ],
         "userId": "test2",
         "createdDateTime": "2020-08-04T22:59:32.514+00:00",
         "updatedDateTime": "2020-08-05T15:27:54.701+00:00",
         "statusEnquired": true
     }
     
## Known caveats
1. The API is not guarded using Authentication mechanisms. This has to be implemenetd in the future

2. The API does not use pagination for listing lottery tickets. This has to be implemented in the future. 

