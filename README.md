# SURL-AWS

ShortURL (a URL Shortener) application implemented on AWS.

## Architecture

![Architecture Diagram](https://i.imgur.com/5sEKWub.png)

Generated using the [CloudFormation Designer](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/working-with-templates-cfn-designer.html).

## Components

- APIGateway
  - Handles routing and decides which Lambda should handle the request, if any.
- Lambda
  - Three in total. One for each operation: create/delete/redirect
- DynamoDB
  - Holds saved shortlinks along with their attributes (destination, expiration date, current date)

## Other Tools Used

- Apache Maven
  - Multi module, one for each lambda and one holding common utilities
- Java 11 ([Amazon Corretto](https://aws.amazon.com/corretto/))
- Checkstyle
  - Uses [Google's](https://google.github.io/styleguide/javaguide.html) Checkstyle specifications for code formatting
- Serverless Application Model (SAM)
- Docker
  - For testing locally
  
## Endpoints

| Endpoint | HTTP Method | Headers | Info |
| -------- | ----------- | ------- | ---- |
| /{niceName} | GET | N/A | Goes to the specified short link |
| /{niceName} | DELETE | N/A | Deletes the short link |
| /create | PUT | niceName,destination,expireAt(optional) | Creates a new short link. expireAt value is a long, time since epoch |
  
## To Do

- Implement authentication/authorization with [Amazon Cognito](https://aws.amazon.com/cognito/)
- Add a UI to easily view created links and delete them
- Log analytics such as number of clicks, geolocation, etc
- Document with the OpenAPI specification