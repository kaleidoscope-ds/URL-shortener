# URL Shortener API

The URL Shortener API provides functionality for shortening URLs, managing short URL redirects, and tracking access counts for shortened URLs. It offers a RESTful interface for users to interact with.


## Features

- Generate a short URL from long URL.
- Redirecting a short URL to a long URL with 301 redirect.
- Listing the number of times(access counts) a short URL has been accessed in the last 24 hours, past week, and all time.
- Data persistence (survives computer restarts). This is handled by modifying the redis conf to support snapshots for recovery. More details in prerequisite section.
- Deleting short links.
- Logging for troubleshooting and alerting.

## Prerequisites

Before running the application, ensure you have the following prerequisites installed:

1. [x] Java Development Kit (JDK) version 8 or higher.
2. [x] Maven for building teh project.
3. [x] Redis Server for data storage.
  Install redis via brew if using macOS. Here are more instructions on setting up Redis server.
  https://redis.io/docs/install/install-redis/install-redis-on-mac-os/
4. [x] Inorder to make sure we support data persistence and disaster recovery, we need to enable this feature in the redis configuration file. 
This is located in cd /usr/local/etc/ typically if you have installed Redis using Homebrew. Open this file and uncomment the following:
  `save 900 1
  save 300 10
  save 60 10000`
These specify the frequency when snapshots are taken. Additionally you can also specify the location where you want to store these snapshots but by default it will be stored in the same directory as the config file.
Once thses changes are made restart the redis server to make sure changes take effect.


## Building the Project
Run the following Maven command to build the project:
   mvn clean package



## Running the Application

After building the project, you can run the application using the following command:
java -jar url-shortener-api.jar


## Testing the API

You can test the API endpoints using tools like Postman or cURL. Optionally you can use the swagger UI which provides a more intuitive and appealing interface to interact with this API.
http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/

- **Shorten URL**: Send a POST request to `/v1/shorten` with the long URL in the request body. 
  curl example:  `curl -X POST http://localhost:8080/v1/shorten \
  -H "Content-Type: application/json" \
  -d 'https://leetcode.com/problems/power-of-two/'`
- **Redirect URL**: Access a shortened URL by sending a GET request to `/v1/redirect/{shortUrl}`.
  curl example: `curl -X GET "http://localhost:8080/v1/redirect/JFYGQVJ" -H "accept: */*"`
  
- **Get long URL**: Send a GET request to `/v1/expand/{shortUrl}` to get original long URL for a short URL.
  curl example: `curl -X GET "http://localhost:8080/v1/expand?shortURL=NARJIUA" -H "accept: */*"`
- **Get Access Counts**: Send a GET request to `/v1/access-counts/{shortUrl}` to get access counts for a short URL.
  curl example:`curl -X GET "http://localhost:8080/v1/accessCount?shortUrl=JFYGQVJ" -H "accept: */*"`
- **Delete Short URL**: Send a DELETE request to `/v1/delete/{shortUrl}` to delete short URL.
  curl example: `curl -X DELETE "http://localhost:8080/v1/delete?shortURL=NARJIUA" -H "accept: */*"`

## Assumptions and Design Decisions

- Duplicate short URLs are not allowed.
- Short links can expire after one month(this is hardcoded on creation). Once expired one cannot fetch/redirect these short URLs
- URLs are stored in Redis with the short URL as the key and URL object as the value. Unless invalidated/flushed these will stay. In a production
environment we would want to scale our redis server to be distributed there-by handling replication in case of disasters.
- Access counts are tracked using a separate field in the URL object. 
- Error handling is implemented for scenarios like invalid URLs, expired URLs, and missing resources.
- Data persistence is handled by modifying the redis conf to support snapshots for recovery.
- Logging is implemented for tracking application events and errors using SLF4j.
- how do one ensure that the shortened URL is unique? Inorder to generate short URL we are using Base 62 conversion, which ensures that there are 62 possible characters for hash value
which means with a length of 7 for the unique short URL we can generate upto 62^7 which is ~trillions of unique short links. As we increase the length we can generate more unique values but
that would defeat the purpose of shortening the longURL in the first place.

## Future considerations and scaling
To be able to handle millions of requests at scale as well as concurrently in a production set up, this application can be part of a web server which can be scaled easily as it 
is stateless. To handle non trusted users we can have a rate limiter to prevent DDOS. A load balancer can be set up to forward these requests to the web servers.
An additional resource I would include is a cache that can do quick lookups rather than making a call to the store. For Analytics we are only considering the accessCounts but this can be extended to include user information as well. 
## Contributors

- Deepthi Shekar



