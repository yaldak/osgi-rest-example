# Karaf REST Example

## Foreword

Hello code reviewer,

Thank you for taking the time to look at my example.
If you have any questions, simply email me (see EOF).

Yalda

## Synopsis

This is an enterprise-class RESTful microservice built on top of Apache Karaf.
The build toolchain is docker-ready, allowing you to create images ready for deployment
on any environment that supports docker.

The purpose of this demo is to showcase a robust solution using OSGi and
popular Java EE technologies.

## Technologies

* Apache Karaf (Felix-based OSGi environment)
* Apache Aries (HTTP & JPA whiteboard services)
* Lucene (via Hibernate Search)
* Hibernate (ORM via JPA)
* H2 Database (in-memory persistence)
* JAX-RS + Jackson (via Jetty whiteboard)

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
to fail. There is a solution in place for an unreleased version of Pax Web.

**Ideally**, I would not have done this with servlets at all. Instead, I would use JAX-RS
along with the multipart annotations for my respective HTTP container. With direct Jersey or CXF based
solutions, this is rather straight forward. However, as I am using Apache Aries to whiteboard HTTP,
it is not quite that simple for me to access multipart support. I could have simply used CXF to
accomplish this, but favored the whiteboard pattern Aries provides for JAX-RS service registration
over doing so. Long live SCR!

## Build

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

### Create a Contact
```sh
curl -X POST http://localhost:8181/contact -H 'Content-Type: application/json' \
-d '{
    "name": "Yalda Kako",
    "company": "somewhere",
    "emailAddress": "yalda@kako.cc"
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
    "name": "Yalda Taco",
    "company": "somewhere",
    "emailAddress": "yalda@kako.cc"
}'
```


## Wishlist

- **Error handling** is currently immature - the HTTP error codes used are not semantic.
This could be resolved with more time to work on the project.

- **Checkstyle** would be nice.

- **AAA** is missing, but I felt it out of scope for an interview exercise.

- ***Generation and generics*** could very well be outfitted from this code.

- ***Gradle*** - I actually prefer Gradle (Groovy is my favorite language), but maven works great too.

## Development

One shot build and run:
```sh
docker rm -f restexample; mvn clean install docker:build \
    && docker run -p 8181:8181 --name restexample -t yalda:restexample
```

# Questions?

Email me at yalda.kako@gmail.com :-)
