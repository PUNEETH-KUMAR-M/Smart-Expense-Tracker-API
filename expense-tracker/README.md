# Smart Expense Tracker API

Smart Expense Tracker is a REST API for recording and reviewing personal expenses. It supports creating expenses, listing them, filtering them by category, calculating totals, and deleting records through simple HTTP endpoints.

The application uses a layered Spring Boot design. Expenses are stored in a local `expenses.json` file rather than a database, making the project easy to run locally while keeping persistence separate from the API and business layers.

## Features

- Add an expense
- View all expenses
- Filter expenses by category
- Calculate total expenses
- Calculate category-wise totals
- Delete an expense
- JSON file persistence
- Input validation
- Global exception handling
- JUnit 5 test support
- Swagger/OpenAPI UI through Springdoc

## Tech Stack

- Java 21
- Spring Boot 3
- Maven
- Jackson
- JUnit 5

## Project Structure

```text
src/main/java/com/expensetracker/
├── config/       Application-level configuration placeholders
├── controller/   REST endpoint definitions
├── dto/          Immutable request, response, total, and error payloads
├── exception/    Custom exceptions and global exception handling
├── model/        Expense domain model
├── repository/   JSON-backed persistence abstraction and implementation
└── service/      Business logic for expense operations

tests/            Maven test source directory for JUnit tests
```

## Installation

Prerequisites: Java 21 and Maven 3.9 or later.

Run these commands from the project root:

```bash
mvn clean install
mvn spring-boot:run
mvn test
```

The API starts at `http://localhost:8080`. The Swagger UI is available at `http://localhost:8080/swagger-ui/index.html` when the application is running.

## API Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/expenses` | Create an expense |
| GET | `/expenses` | Return all expenses |
| GET | `/expenses/category/{category}` | Return expenses in a category |
| GET | `/expenses/total` | Return the overall expense total |
| GET | `/expenses/total/{category}` | Return the total for a category |
| DELETE | `/expenses/{id}` | Delete an expense by ID |

## Example Requests

Create an expense:

```http
POST /expenses
Content-Type: application/json

{
  "title": "Weekly groceries",
  "amount": 82.45,
  "category": "Food",
  "date": "2026-07-31"
}
```

Get all expenses:

```http
GET /expenses
```

Get expenses in a category:

```http
GET /expenses/category/Food
```

Get totals:

```http
GET /expenses/total
GET /expenses/total/Food
```

Delete an expense:

```http
DELETE /expenses/b2df5bd2-2ee4-4bd0-a4d7-9362d293a83f
```

## Example Responses

Created expense (`201 Created`):

```json
{
  "id": "b2df5bd2-2ee4-4bd0-a4d7-9362d293a83f",
  "title": "Weekly groceries",
  "amount": 82.45,
  "category": "Food",
  "date": "2026-07-31"
}
```

All expenses (`200 OK`):

```json
[
  {
    "id": "b2df5bd2-2ee4-4bd0-a4d7-9362d293a83f",
    "title": "Weekly groceries",
    "amount": 82.45,
    "category": "Food",
    "date": "2026-07-31"
  }
]
```

Category total (`200 OK`):

```json
{
  "totalAmount": 82.45,
  "category": "Food"
}
```

An overall total returns the same structure with `category` set to `null`.

## Error Responses

Validation failure (`400 Bad Request`):

```json
{
  "timestamp": "2026-07-31T09:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "amount: Amount must be greater than zero",
  "path": "/expenses"
}
```

Missing expense (`404 Not Found`):

```json
{
  "timestamp": "2026-07-31T09:31:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Expense with id b2df5bd2-2ee4-4bd0-a4d7-9362d293a83f was not found",
  "path": "/expenses/b2df5bd2-2ee4-4bd0-a4d7-9362d293a83f"
}
```

Storage failure (`500 Internal Server Error`):

```json
{
  "timestamp": "2026-07-31T09:32:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Expense storage is unavailable",
  "path": "/expenses"
}
```

## Design Decisions

- **Layered architecture:** keeps HTTP concerns, business rules, and persistence separate so each layer remains focused and easier to maintain.
- **DTOs:** define a stable API contract and prevent the domain model from being exposed directly to clients.
- **Repository pattern:** allows the service layer to depend on a persistence abstraction instead of JSON-file details.
- **JSON file storage:** meets the assignment requirement for local persistence without introducing database setup.
- **Constructor injection:** makes dependencies explicit and supports simpler testing and maintenance.

## Future Improvements

- Replace JSON storage with a relational database
- Add authentication and authorization
- Add pagination and sorting for large result sets
- Add an expense update endpoint
- Add Docker support
- Add CI/CD checks for build, test, and code quality

## Conclusion

This project provides a compact, maintainable foundation for expense tracking. Its layered structure makes it suitable for incremental enhancements while keeping the current local setup straightforward to run and understand.
