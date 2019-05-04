# Maintaining Entity Relationships Across Microservices

* Maintaining a many-to-many relationship using a *composing gateway service*.
  * HTTP via [Micronaut](https://micronaut.io)
  * Non-blocking I/O via [RxJava](https://github.com/ReactiveX/RxJava)
  * Data access via [JDBI](https://jdbi.org)
  * Data storage via [Postgres](https://postgresql.org)

> **CAVEAT LECTOR**: This is just an example of one design choice, which is almost certaintly not appropriate for all systems.
Consider your use-case and maintenance budget carefully before using this design.

## Discussion

Recently, [microservice architectures](https://microservices.io/) have been getting a lot of hype. Their merits and
[flaws](https://www.dwmkerr.com/the-death-of-microservice-madness-in-2018/) have been thoroughly [discussed elsewhere](https://martinfowler.com/articles/microservice-trade-offs.html), so I
won't revisit them here. One thing that I've been asked is how to maintain entity relationships across microservice
boundaries. The issue is keeping both sides of the relationship in-sync whenever entities are owned by separate
services.

To illustrate, consider two microservices, an Author microservice and a Conference microservice. Authors attend 
Conferences, and their **attendance** can be modeled as a many-to-many relationship. If we were modeling Authors and
Conferences in the same database, the foreign-key relationships might look something like this:

```
TABLE: Authors
  id: UUID   -- Primary Key
  
TABLE: Conferences
  id: UUID   -- Primary Key

TABLE Attendance
  author_id: UUID       -- Foreign Key (Authors.id)
  conference_id: UUID   -- Foreign Key (Conferences.id)    
```

In a microservices world, sharing a database decreases scalability, so we would prefer to keep the Authors and
Conferences tables in isolated instances. The presence of the Attendance table causes a bit of trouble here, as we
cannot maintain foreign key relationships across database instances. We could assign the Attendance table to either the
Author or Conference microservice, but whichever service we choose, we will lose the ability to maintain a foreign key
with one of our entities.

Concerns about referential integrity aside, we would also prefer each microservice to have enough data to perform joins
without having to coordinate with its peers. Communication among microservices in the same layer quickly gets out of
control. This leads us to maintain a copy of the Attendance table in each microservice, and keep the two synchronized
via an external controller which composes the Author and Conference microservices.

Below is the schema we have decided on.

```
DATABASE : Authors DB
    TABLE: Authors
      id: UUID   -- Primary Key
    
    TABLE Attendance
      author_id: UUID       -- Foreign Key (Authors.id)
      conference_id: UUID
      
DATABASE: Conference DB          
    TABLE: Conferences
      id: UUID   -- Primary Key

    TABLE Attendance
      author_id: UUID       
      conference_id: UUID   -- Foreign Key (Conference.id)
```

This repository demonstrates a way to achieve **eventual consistency** of a many-to-many relationship using
**non-blocking I/O**, and a discipline for querying this data which makes it appear **strongly consistent**. At a 
high-level, both the Author and Conference services each expose *internal* endpoints for creating an Attendance record. 
A Gateway service exposes an *external* endpoint for creating an attendance record. The gateway internally calls the endpoints
at the Author and Conference service and handles any error conditions that may arise.

These error conditions are expected to be common. A request to insert the Attendance of an existing Author at a
non-existent Conference would yield a successful `INSERT` in the `Author.Attendance` table, whereas the corresponding
`INSERT` to the `Conference.Attendance` table would fail. The gateway sees these failures as HTTP response codes, and
then rolls back the successful request by performing a `DELETE` on the `Authors.Attendance` table, returning a
 `404 Not Found` response back to the client.

If the above error-handling strategy makes you queasy, you're not alone. After the successful `INSERT` into
`Author.Attendance`, it is possible that some query to the `Author` service may yield a result which refers to a
non-existing `Conference`. However, **if we design our Gateway to perform its queries carefully, we can avoid these
dangling pointers** before we return a message back to the client.

Our gateway will make simultaneous requests to the Author and Conference microservice for each query
which involves the Attendance table. In case a spurious entry is requested by a client, the corresponding microservice
will detect it, and the Gateway will handle it. As a bonus, we have an API which always eagerly fetches details
of the entities it returns, thus cutting down on the number of round-trips expected from the client.

Let's consider the possible outcomes when each microservice contains spurious entries. There are two types of queries
we would like to perform on the Attendance table. Ignoring joins, they look like this: 

```
SELECT * FROM Attendance WHERE author_id = :id
SELECT * FROM Attendance WHERE conference_id = :id
```

In addition, there are two microservices these queries could be executed in.  Symmetry will allow us to ignore two of
the total four cases.

###### Case A) Querying Author service by Author.Id
In this case, we want to learn the Conferences attended by a certain Author. To achieve this, we

1. Ask the Author Service for the Author details for this author id, and
1. Ask the Conference service for the list of Conferences which this author has attended.

If the Author.Id is incorrect, our query (1) will fail due to foreign key constraints in the Author table. We are
further assured that every conference we return from query (2) is correct, by the foreign key constraints on the
Conference table.

###### Case B) Querying Author service by Conference.Id 
In this case, we want to learn the Authors who have attended a certain Conference. To achieve this, we

1. Ask the Conference service for the conference details for this conference id, and
1. Ask the Author service for the list of Authors which have attended this conference.

As in the previous case, if the Conference.Id is incorrect, our query (1) fails. Likewise, the Authors returned from (2)
will all exist, because of the foreign key constraints on the Author table.

The last two cases "Querying Conference service by Conference.Id" and "Querying Conference service by Author.Id" are
symmetric.

### Summary

The tedium above can be summarized simply by the phrase **"Only Ask the Owner"**. In our design, no microservice should ask
the Author service for a list of Conferences because the Author service does not own the Conference list. In any case,
this query would just dump a list of IDs in the service's lap, which would force the client to follow up by sending extra requests.
Writing a microservice to make simultaneous request in this constrained way avoids these issues and ensures data
integrity.

While this technique works, maintaining a many-to-many relationship across a microservice boundary is not a decision to be
taken lightly. This should only be done for relationships whose queries are expected to remain extremely simple. As richer
queries begin to be added, the benefits gained by separating the Authors and Conferences tables begin to outweigh the 
complications. To accommodate richer queries, additional data will need to be added to the join table. In the design used
here, the task of maintaining this data falls to the Gateway service. This decision should be reconsidered in the context of
richer queries, and weighed against the added complexity of peer-to-peer communication among the Author and Conference service.

You may be concerned that the load across the system appears to be doubled in the scheme we have described. Two services
are needed to handle a single request, and we would be justified in asking if we can do any better. Of course, we can,
but the added efficiency comes at additional complexity (see
[two-phase commit](https://en.wikipedia.org/wiki/Two-phase_commit_protocol)), or additional hops (see 
[daisy-chaining](https://microservices.io/patterns/data/saga.html)).

The remainder of the README provides a walkthrough explaining the implementation. It makes a few low-level design 
choices you might not want to make in a Production service. This is a conscious choice, since the point is to make it 
easy to see how the services coordinate.

## Services and Modules

The code is organized into three microservices and a shared common library.

 * **author-service**: Provides RESTful CRD for Authors.
 * **conference-service**: Provides RESTful CRD for Conferences.
 * **gateway-service**: Composes endpoints exposed by author-service and conference-service to maintain the attendance
   relationship.

 * **commons**: Library containing data models and API interfaces used by both client and server [as part of Micronaut's declarative HTTP client](https://docs.micronaut.io/latest/guide/index.html#clientAnnotation).

In the data layer, Authors and Conferences are stored in separate databases. For testing purposes, they are deployed on
the same instance.

### Walkthrough

> Spin up a postgres instance via Docker and make it available via localhost by running `docker run --expose 5432 -p 5432:5432 -e POSTGRES_PASSWORD=password postgres:11.2-alpine` 

Create the database schemas using the SQL from the [conference-service]() and the [author-service]().

Start all three services on localhost.

Seed the database by executing create requests.

```curl https://localhost:8070/author -d '{"full_name":"Alan Turing"}'```
```curl https://localhost:8080/conference -d '{"acronym":"SWAT"}'```
 Note the `Location` header from the above request, it will be needed to create an `Event` below.
 ```curl https://localhost:8080/event -d '{"conference_id":"${CONFERENCE_ID}", "seq":1}'```
 
 Now record Alan Turing's (fictional) attendance at the first SWAT conference using the POST request below.
 
 ```curl https://localhost:8443/event/${EVENT_ID}/attendance/${AUTHOR_ID} -X POST```
 
 Experiment by using non-existent values for EVENT_ID and AUTHOR_ID and check the logs of each service to note the response.
