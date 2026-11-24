<!-- wilco-ai-harness:begin (generated from wilco-ai-harness/AGENTS.base.md, do not edit) -->
## Team rules (managed by wilco-ai-harness)

These rules apply to every developer and every coding agent (Claude Code, Codex, Copilot) in Flightwatching repositories.

### Workflow

- Work on a branch named `<type>/<topic>` (e.g. `fix/acars-empty-block`), never on `main`, `master` or `prod`. A push to `prod` is a release.
- Push the branch and open a pull request. Agents never merge: a human does.
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org): `<type>(<optional scope>): <description>`, types `feat`, `fix`, `refactor`, `perf`, `test`, `docs`, `chore`, `build`, `ci`, `style`, `revert`. A breaking change takes `!` after the type or scope, or a `BREAKING CHANGE:` footer.
- Git hooks are managed by [pre-commit](https://pre-commit.com): run `pre-commit install` once after cloning. Never skip them (`--no-verify`, `SKIP=`): fix what they report.
- Format and lint run as pre-commit hooks. Before handing back, run `pre-commit run --files <changed files>` and the tests this file describes for the repository.

### Safety

- Never read, print or commit secrets: `.env`, `*.key`, `*.pem`, service account files, passwords in configuration files.
- Never touch production: no deployment, release, `kubectl`, `helm`, cloud CLI, `ssh`, and no database client pointed at a non-local host. Ask a human.
- Never run commands that lose work (`git reset --hard`, `git clean -f`, force push) without being asked.

### Think before coding

- State assumptions explicitly. When a request has several interpretations and the choice changes the result, present them or ask; when it does not, pick the sensible default and say which.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear and the code cannot resolve it, stop and name what is confusing.

### Simplicity first

- Write the minimum code that solves the problem: no features beyond what was asked, no abstraction for single-use code, no configurability nobody requested, no error handling for impossible scenarios.
- If 200 lines could be 50, rewrite them. Would a senior engineer call it overcomplicated? Then simplify.
- No comments in application code, except for what the code cannot say: a hidden constraint, a non-obvious workaround, an external quirk. Never comment what the code already says. SQL migrations and other one-shot scripts are the exception: there, the comment is the only record of why.

### Never nest

- Handle edge cases and errors first with guard clauses and early `return` / `continue` / `throw`, so the happy path stays at the lowest indentation.
- No `else` after a `return`; turn nested `if`s into a single condition or an early exit.
- Extract a well-named function rather than adding a third level of nesting. Prefer `map` / `filter` / streams over nested loops when they read better, and `async` / `await` or named functions over callback pyramids.
- More than two levels of indentation in a function body means restructure it. In a file that is already nested, apply this to the code you write or modify; do not mass-reformat it.

### Surgical changes

- Touch only what the task needs: do not improve adjacent code, comments or formatting, and do not refactor what is not broken.
- Match the existing style (except nesting).
- Mention unrelated dead code instead of deleting it. Remove the imports, variables and functions that your change made unused.
- Every changed line traces back to the request.

### Goal-driven execution

- Define success criteria, then loop until they are verified: "add validation" means tests for invalid inputs, then make them pass; "fix the bug" means a test that reproduces it first; "refactor" means tests pass before and after.
- For a multi-step task, state a brief plan: `1. <step> → verify: <check>`.
- Report outcomes faithfully: say when tests fail or a step was skipped.

### User interfaces

- UI for pilots, officers or technicians follows the aviation color code, where color carries meaning and is never decorative: see the `aviation-ui-colors` skill.
<!-- wilco-ai-harness:end -->
