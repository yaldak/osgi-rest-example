# Karaf REST Example

## Synopsis

This is an enterprise-class RESTful server build on top of Apache Karaf.

The purpose of this demo is to showcase relevant technologies.

## Technologies

* Apache Karaf (Felix-based OSGi environment)
* Apache Aries (Whiteboard services)
* Lucene (via Hibernate Search)
* Hibernate (ORM via JPA)
* H2 Database (In-memory store)
* JAX-RS + Jackson (via Jetty whiteboard)

## Build

The build uses Apache Maven. From the root directory, simply use:

```
mvn clean install
```

## Deployment

The toolchain is ready for docker. To create the image, simply run:

```
mvn docker:build
```

To test:

```
docker run -p 8181:8181 -t yalda:restexample
```

## Usage

## Wishlist

- **Error handling** is currently immature - HTTP error codes are not semantic
- **Checkstyle** would be nice
- There is no AAA
- Generation
