# AirTasker Coding Challenge - RateLimiter

### Documentation

#### Assumption
- The caller of the API is sending their API Key in the header of the http request as "X-api-key".

#### Rate Limiting Strategy
The algorithm I have chosen for rate limiting in this coding challenge is _"Sliding Window"_ meaning I will do the followings once a request is received: 
 - Store the timestamp at which the request was received in a sorted set (per each API Key)
 - Remove the old timestamps (requests from that API Key) that were received earlier than 60 minutes (configurable time) ago. 
 - Count the number of remaining timestamps (requests from that API Key received within the last hour) and compare it with the limit (configurable number).
 - Find the first timestamp received within the last hour in order to calculate the remaining time to retry.
 
 - Note: I have used Redis server as a distributed cache and utilised the Redis built-in Sorted Set. To make it thread-safe I have done the above steps inside a Redis transaction to make them work like an atomic command.
   - This application has been dockerized so you **don't** need to separately install and run Redis Server. 
     But you are free to use your own instance of Redis Server if you like (as I did before dockerizing the application). 
     You would just need to specify the host and port number in the application.properties file and run the application without docker.
   - I have also used an embedded Redis Server for unit tests. This means you **won't** need to have a Redis Server up and running just to abe able to run the tests.
   
 - I have used Strategy Design Pattern and Spring DI (dependency injection) to select the algorithm.
The context is the Controller class and the Service class is the actual strategy.
This means any new algorithm (strategy / service) can be easily introduced by extending the abstract class and injecting it to the controller.
#### How to run
 - docker-compose build
 - docker-compose up
 
 Or simply run the "RatelimiterApplication" class via your IDE. In this case you will need an instance of Redis Server to be up and running. As mentioned above, the port number and host name of the Redis Server must be defined in the src/main/resources/applicatoin.properties file.
#### Possible improvements
 - Using sliding window with counter to reduce the memory footprint. In that case we will aggregate and store the number of received requests per each API Key in different buckets (for example in 1-minute duration buckets)
 - Moving the rate limiting logic from the service class (SlidingWindowRateLimitStrategy.java) to an interceptor.
 - Configuring the dockerized Redis Server to store data on disk.
