Summary of Changes
-SQL Operator Precedence (Backend & Oracle DB): Fixed missing parentheses around `(LOWER(title) LIKE :term OR LOWER(description) LIKE :term)` in `TaskRepository.java`, `search_tasks.sql`, and `task_search_package.sql`. Prevents archived task leaks and ensures status filtering applies when title matches.
- Backend Delay & DB Pagination: Removed artificial `Thread.sleep` delay in `TaskController.java`. Replaced memory-slicing pagination with Spring Data JPA `Pageable` database-level `LIMIT`/`OFFSET` queries.
- Input Validation & Exception Handling**: Added parameter sanitization in `TaskController.java` to return HTTP 400 Bad Request on invalid status enums and clamp `page`/`pageSize` parameters safely.
- Frontend Race Conditions & State Fixes: Added an `isCancelled` cleanup flag in `useTasks.js`, reset `error` state on fetch, and resolved stuck loading spinners with `.finally()`.
- UX Improvements: Debounced search query by 300ms in `App.jsx` and reset pagination to page 1 on filter/search updates.
- Automated Tests: Added JUnit 5 / MockMvc integration tests (`TaskControllerTest.java`).

What I Chose Not to Change
- H2 In-Memory DB: Kept H2 database for zero-config local runs instead of introducing PostgreSQL/Docker setup.
- Global State Management: Retained React local hook state (`useTasks`) rather than adding Redux/Zustand, keeping the diff minimal and focused.

Biggest Remaining Risk
- No Database Indexing: `LOWER(title)` and `LOWER(description)` wildcard queries (`%term%`) force full table scans. On large datasets, performance will degrade without functional/trigram indexes or a dedicated full-text search index (e.g. Elasticsearch).

Tools & AI Used
- Antigravity AI Assistant: Used to inspect SQL operator precedence, write JUnit 5 test cases, draft React hook debouncing, and verify test builds.
