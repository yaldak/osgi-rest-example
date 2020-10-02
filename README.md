# REST Example

## Synopsis

This is an "enterprise-almost"-class RESTful microservice built on top of Apache Karaf.
The build toolchain is docker-ready, allowing you to create images ready for deployment
on any environment that supports docker.

The purpose of this demo is to showcase a robust solution using OSGi and
popular Java EE technologies.

The architecture is made in good faith, but can benefit from further iteration (see the Wishlist).

Of course, there are much easier ways to create a simple REST API. I went all out
because I wanted to show a variety of EE tech. If I were in a time challenge, this could
have been made very quickly with Express and Node.

## Technologies

* Apache Karaf (Felix-based OSGi environment)
* Apache Aries (HTTP & JPA whiteboard services)
* Lucene (via Hibernate Search)
* Hibernate (ORM via JPA)
* H2 Database (in-memory persistence)
* JAX-RS + Jackson (via Jetty whiteboard)
* Java 8 (a solid choice, but I prefer Java 11)

Originally, this project was developed using EclipseLink for ORM.
I flipped to Hibernate because it is very popular, and let me throw a Lucene engine on top quickly.
This was not a terribly hard change thanks to Aries JPA whiteboard.

P.S.:
I know how to use both Felix and Lucene more directly - wanted to keep this conventional.

## The Multipart Problem

Unfortunately, due to a known issue in Pax Web (the underlying HTTP service for Karaf),
I could not implement a multipart file upload servlet to support contact photo images.

(I know how to and have implemented several multiple multipart situations).

Proof:
- <http://paste.debian.net/hidden/1f14a9fe/>
- <https://www.mail-archive.com/user@karaf.apache.org/msg21173.html>

I have made a "best effort" to show what I can there. Any attempt to use the contact photo
POST endpoint will persist a pre-canned image from the server bundle's resources as the
photo blob. This does get persisted as per JPA, and the retrieved image is actually
that from the database.

Jetty had recently removed some of their multipart facilities from their OSGi bundle
descriptors (specifically, from Export-Package) causing my implementation with Servlets 3
to fail. There is a solution in place for an unreleased version of Pax Web. Dependency and
classpath hell makes it challenging and not worth to "hack" around this myself.

**Ideally**, I would not have done this with servlets at all. Instead, I would use JAX-RS
along with the multipart annotations for my respective HTTP container. With direct Jersey or CXF based
solutions, this is rather straight forward. However, as I am using Apache Aries to whiteboard HTTP,
it is not quite that simple for me to access multipart support. I could have simply used CXF to
accomplish this, but favored the whiteboard pattern Aries provides for JAX-RS service registration
over doing so. Long live SCR!

## What's up with `Consumer<Exception>`?

In my data provider interface, I end each method signature with an argument for a
`Consumer<Exception>`. This is by design, and if I had more time, I would have made efforts
to propagate error chains from JPA into the `onError` consumer and translate them to
more semantic HTTP status codes.

The philosophy behind this is to avoid try/catch blocks altogether using `Optional`
and encourage a truly functional or `Stream`-pipeline based approach with semantics
introduced in Java 8+.

See `cc.kako.examples.rest.api.util.Try` for some functional Exception fun.

## Build

Minimum requirements: JDK 8+, Maven 3, Docker, Mac or Linux preferred

The build uses Apache Maven. From the root directory, simply use:

```
mvn clean install
```

This will install the example to your local Maven repository.

## Deployment

The toolchain is ready for docker. To create the image after a build, run:

```
mvn docker:build
```

To test:

```
docker run -p 8181:8181 -t yalda:restexample
```

## Usage

Import `postman_collection.json` with Postman to get started.

or, curl:

### Create a Contact

```sh
curl -X POST http://localhost:8181/contact -H 'Content-Type: application/json' \
-d '{
    "name": "Yalda Kako",
    "company": "somewhere",
    "birthDate": "1992-01-01",
    "emailAddress": "yalda@kako.cc",
    "phoneNumberPersonal": "7731243334",
    "phoneNumberWork": "7731243333",
    "address": {
        "lineOne": "Test123",
        "lineTwo": "",
        "city": "Chicago",
        "state": "IL",
        "zipCode": "123456"
    }
}'
```

### Get All Contacts

```sh
curl http://localhost:8181/contact
```

### Get a Contact by ID

```sh
curl http://localhost:8181/contact/1
```

### Update a Contact by ID

```sh
curl -X PUT http://localhost:8181/contact/1 -H 'Content-Type: application/json' \
-d '{
    "id": 1,
    "name": "Yalda Taco",
    "company": "somewhere",
    "birthDate": "1992-05-04",
    "emailAddress": "yalda@kako.cc",
    "phoneNumberPersonal": "7731243333",
    "phoneNumberWork": "7731243334",
    "address": {
        "lineOne": "Test123",
        "lineTwo": "",
        "city": "Chicago",
        "state": "IL",
        "zipCode": "123456"
    }
}'
```

### Delete Contact by ID

```sh
curl -X DELETE http://localhost:8181/contact/1
```

### Get a Photo Resource by Contact ID

Returns mediatype `image/png` if found.

```sh
curl http://localhost:8181/contact/1/photo
```

### Set a Photo Resource by Contact ID

This is not working, see "The Multipart Problem" above.

```sh
curl -X POST http://localhost:8181/contact/1/photo -d ''
```

### Delete a Photo Resource by Contact ID

Returns mediatype `image/png` if found.

```sh
curl -X DELETE http://localhost:8181/contact/1/photo
```

### Search Contacts by Email or Phone

```sh
curl http://localhost:8181/contact/search/emailOrPhone?q=7731243333
```

### Search Contacts by City or State

```sh
curl http://localhost:8181/contact/search/city?q=chicago
curl http://localhost:8181/contact/search/state?q=il
```

## Wishlist

- **Error handling** could be improved - the HTTP response error codes are not always semantic.
This could be resolved with more time on the project.

- **Input validation, normalization, and Swagger** are entirely missing - time constraints.
libphonenumber would have been nice for phone number cleanup, for example.

- **surefire and checkstyle**

- **AAA** is missing.

- **Generation and generics** could very well be outfitted from this code.

- **Gradle** - I actually prefer Gradle (Groovy is my favorite language), but maven works great too.

- **JavaDoc** - definitely could have used some on the interfaces

- **Async JAX-RS**

## Development

One shot, dockerized build and run:
```sh
docker rm -f restexample; mvn clean install docker:build \
    && docker run -p 8181:8181 --name restexample -t yalda:restexample
```
