# API Gateway - Oauth2 - Form Login - Downstream Services(frontend/backend)

A showcase demonstrate how to implement Spring Cloud Gateway with Oauth2 and `token + session sharing` propagation across downstream services(`frontend and backend`).

To run `mvn clean package` and `mvn spring-boot:run` for each folder except `multiple-jwks-validation`.

To generate a docker image `mvn spring-boot:build-image` for each folder except `multiple-jwks-validation`.

Configure `OAUTH2` credentials and set the variables in `spring-cloud-gateway/src/main/resources/application.yml`

Access Gateway by http://localhost:8080 and use default users(`user/admin`) with password(`password`).

After `oauth2 login` you can get a valid bearer token access, click in `Authentication Object Tab` and copy tokenValue.

To test by curl with valid token should reply 200.

```
curl -v -H "Authorization: Bearer ..." localhost:8080/v1/orders
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /v1/orders HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.84.0
> Accept: */*
> Authorization: Bearer ...
[{"orderId":"7114e8d4-c45f-4536-a5cc-9ed73502938f","price":100.00,"user":"default"},{"orderId":"c64d3333-61ed-4005-96a3-dc92d38d3403","price":29.89,"user":"default"}]%
```

Or with invalid/expired 401.
```
curl -v -H "Authorization: Invalid Token
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> GET /v1/orders HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.84.0
> Accept: */*
> Authorization: Invalid Token
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 401 Unauthorized
< WWW-Authenticate: Bearer
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Content-Type-Options: nosniff
< X-Frame-Options: DENY
< X-XSS-Protection: 1 ; mode=block
< Referrer-Policy: no-referrer
< content-length: 0
< 
```

More details look at https://www.linkedin.com/pulse/securing-resources-apigatewayspring-cloud-gateway-rodrigo-rodrigues/?published=t

### References
[Spring Cloud Gateway Documentation](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#gateway-starter)

[Secure Spring Cloud Gateway](https://spring.io/blog/2019/08/16/securing-services-with-spring-cloud-gateway)

[Thymeleaf Spring Security Example](https://developer.okta.com/blog/2022/03/24/thymeleaf-security)

[Thymeleaf JQuery Ajax Example](https://riptutorial.com/thymeleaf/example/28530/replacing-fragments-with-ajax)

[Identity Propagation API Gateway](https://medium.com/@robert.broeckelmann/identity-propagation-in-an-api-gateway-architecture-c0f9bbe9273b)