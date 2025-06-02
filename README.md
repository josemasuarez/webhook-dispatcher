# Webhook Dispatcher

A robust and scalable webhook dispatching service built with Spring Boot that handles asynchronous webhook delivery with reliability and monitoring capabilities.

## Features

- Asynchronous webhook processing
- Reliable message delivery with RabbitMQ
- PostgreSQL database for persistence
- Reactive programming support with WebFlux
- Docker support for easy deployment
- Comprehensive validation and error handling

## Tech Stack

- Java 21
- Spring Boot 3.2.3
- Spring Data JPA
- Spring AMQP (RabbitMQ)
- PostgreSQL
- Docker & Docker Compose
- Lombok

## Prerequisites

- Java 21 or higher
- Docker and Docker Compose
- Maven

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/webhook-dispatcher.git
cd webhook-dispatcher
```

2. Build the project:
```bash
mvn clean install
```

3. Run with Docker Compose:
```bash
docker-compose up -d
```

The application will be available at `http://localhost:8080`


## Development

To run the application locally:

```bash
mvn spring-boot:run
```

## Testing

Run the test suite:

```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 