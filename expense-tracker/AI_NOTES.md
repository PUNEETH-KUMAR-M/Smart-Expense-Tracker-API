# AI Usage Report

## AI Tools Used

ChatGPT and Codex were used as development assistants during project setup and implementation. GitHub Copilot was not used in this work session.

## AI-Generated Work

AI accelerated several early development tasks, including:

- Initial Maven project scaffolding and package organization
- Draft DTO and domain-model templates
- Suggestions for JSON repository structure and safe file persistence
- Controller, service, and exception-handling patterns
- Validation and API error-response structure
- README organization and example payloads

The generated output was treated as a starting point, not as final code. Each part needed review against the assignment requirements and the existing project structure.

## Manual Work Performed

The implementation was reviewed and adjusted during development. This included checking the API design, refining validation messages, reviewing repository behavior, and improving naming and layer boundaries. A compile issue in the JSON repository—an extra closing brace and duplicated import—was identified and corrected.

The project was built, packaged, and started locally. A read-only request to `GET /expenses` was made successfully and returned `200 OK` with an empty JSON array. The JSON persistence approach, endpoint contracts, and HTTP status-code handling were also reviewed from the implemented code.

## Validation and Testing

The Maven `clean test` lifecycle completed successfully, and the application was packaged and launched successfully on port 8080. The currently committed `tests` directory is configured for JUnit 5 but does not yet contain test classes, so the successful test lifecycle does not represent functional unit-test coverage.

Manual runtime verification in this session confirmed application startup and the `GET /expenses` response. The remaining endpoints should be exercised with Postman or automated JUnit tests before treating the API as fully regression-tested. JSON persistence and expected HTTP status codes were reviewed from the implementation; full end-to-end persistence coverage was not recorded in this session.

## AI Suggestions Not Used

- **Use a database instead of JSON storage:** rejected because the assignment explicitly requires local JSON-file storage and no database.
- **Add authentication and user accounts:** rejected because it is outside the assignment scope and would add unnecessary setup for a single-user local API.
- **Introduce caching or extra design patterns:** rejected because the current data size and requirements do not justify additional complexity.
- **Expose the domain model directly from controllers:** rejected in favor of DTOs so the public API remains independent of internal model changes.

## Lessons Learned

- AI is most useful for accelerating drafts, not replacing requirement review.
- Small, focused layers make it easier to isolate and fix defects.
- DTOs help maintain a clear boundary between API payloads and domain objects.
- Local file persistence still needs careful handling for reads, writes, and startup state.
- Validation errors should be consistent and useful to API clients.
- A successful build does not replace meaningful automated test coverage.
- Runtime checks are valuable, but they should be documented accurately and expanded over time.
- Final implementation decisions should remain grounded in the assignment scope and human review.
