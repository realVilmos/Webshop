# Webshop
## API

A fully functional Spring Boot API designed for a webshop application. The API supports user authentication with access and refresh tokens, admin and user systems, and payments via Stripe with webhook integration.

### API Documentation

Detailed API endpoint documentation can be found in the Wiki.

### Features
* **Authentication:** Utilizes access and refresh tokens to ensure the security of the API. Every single request goes through a OncePerRequestFilter checking the validity and expiration of the tokens.
* **User Management:** Admin and user system allowing admins to create/edit items.
* **Payments:** Seamless integration with Stripe's PaymentIntent. Also contains a webhook to capture feedback from Stripe, setting the appropriate payment status thereafter. Easily expandable with more payment solutions.
* **Testing:** Tested the API with @SpringBootTest and Postman
* **Documentaion:** API endpoint documentation is semi-auto generated via @AutoConfigureRestDocs

### In Progress:
* **Email after order:** Although the user can pay, and it is registered via webhooks, email is not sent about the details.
* **Invoice:** Admins would approve of an order if everything is right and then would send an invoice. There is no such mechanism for this yet.

### Getting started
Prerequisites to install the project:
* JDK 19
* Maven
* A database e.g postgres

```bash
git clone https://github.com/realVilmos/Webshop.git
cd ./backend/target
```
* Make an application.yml file in src/main/resources. Below you see everything that is required in the yaml file. Please fill with your relevant details. The expirations can be changed as liked. The current values are 1 day for the access token, and 1 year for the refresh in milliseconds.
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:54320/<your_database>
    driver-class-name: org.postgresql.Driver
    username: <your_database_username>
    password: <your_database_password>
  jpa:
    hibernate:
      ddl-auto: create-drop
    database: postgresql
    database-platform: org.hibernate.dialect.PostgreSQLDialect
  mail:
    host: smtp.gmail.com
    port: 587
    username: <your_gmail_email>
    password: <your_gmail_password>
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
  jackson:
    serialization:
      write-dates-as-timestamps: false

stripe:
  key: private_key
  endpoint: endpoint_key

jwt:
  secret_key: jwt_secret
  access_expiration: 86400000
  refresh_expiration: 31536000000
angular_link: http://localhost:4200
```

Try postman or the frontend application below to access the app that is now hosted on localhost:8080

## Angular frontend
This is the Angular frontend designed to communicate with the API above. This project is still very much in progress, but it provides a functional shopping experience.

### Features
* **JWT Token Interceptor:** Every request attaches a JWT token, with exceptions for specific bypass URLs. On JWT expiration, an automatic attempt is made to refresh the token and resend the request.
* **Stripe Payment Integration:** After obtaining the client_secret from the backend, payments can be processed using the integrated Stripe form.
* **Authentication:** A system that retrieves user details upon login and differentiates between user and admin capabilities.
* **Route Guards:** Ensures the correct authorization level for certain routes, based on user roles.
* **Cart System:** A complete cart functionality.

### In Progress
* **Search & Filtering:** These functionalities are planned but not implemented  yet. The main page retrieves random items in pageable format, with on-scroll demand to load more.
* **Editing user details:** The user can't edit their details at the moment. 
* **Details/Features to improve user experience:** Many webshops have features like "Favourites" that could be easily implemented by saving to local storage.
* **Viewing Orders:** The user is not able to view their past orders.
* **Design:** The design is almost non-existent at this point, trying to implement functionalities first.

### Getting started

```bash
git clone https://github.com/realVilmos/Webshop.git
cd frontend
npm install
ng serve
```

Visit http://localhost:4200/ in your browser to access the application.