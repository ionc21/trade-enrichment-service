# Trade data enrichment
Service to enrich trade data with product names

## Requirements
- Java 21
- Maven 3.9.6

## Build and execute instructions
- Build
 ```shell
mvn clean install 
```
- Run
```shell
mvn spring-boot:run
```

## CURL request
- Enrich trade data with product names
```shell
curl --request POST -F file=@{trade.csv file location} http://localhost:8082/api/v1/enrich --header 'Content-Type:text/csv' --header 'Accept:text/csv'
```

## Future steps to improve
- Logging
- Advanced Exception Handling
- Advanced Testing: unit, integration, non-functional tests
- Transactions
- Caching
- Security
- API Documentation (Swagger)
- Dockerization

For very large product files that exceed the available memory, the ProductRepository interface may be a Mongo Repository which offers fast data retrieval.

I would implement such service in Spring boot Kotlin, because langauge itself is promoting immutability and functional programing style. 
It offers reactive style of programming using coroutines which are perfect for such kind of data processing.
It is well integrated with Webflux which offers a great flexibility in handling large data sets.
