<!-- wilco-ai-harness:begin (generated from wilco-ai-harness/AGENTS.base.md, do not edit) -->
## Team rules (managed by wilco-ai-harness)

These rules apply to every developer and every coding agent (Claude Code, Codex, Copilot) in Flightwatching repositories.

### Workflow

- Work on a branch named `<type>/<topic>` (e.g. `fix/acars-empty-block`), never on `main`, `master` or `prod`. A push to `prod` is a release.
- Push the branch and open a pull request. Agents never merge: a human does.
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org): `<type>(<optional scope>)!: <description>`, types `feat`, `fix`, `refactor`, `perf`, `test`, `docs`, `chore`, `build`, `ci`, `style`, `revert`.
- Git hooks are managed by [pre-commit](https://pre-commit.com): run `pre-commit install` once after cloning. Never skip them (`--no-verify`, `SKIP=`): fix what they report.
- Format and lint run as pre-commit hooks. Before handing back, run `pre-commit run --files <changed files>` and the repository's tests (see below).

### Safety

- Never read, print or commit secrets: `.env`, `*.key`, `*.pem`, service account files, passwords in configuration files.
- Never touch production: no deployment, release, `kubectl`, `helm`, cloud CLI, `ssh`, and no database client pointed at a non-local host. Ask a human.
- Never run commands that lose work (`git reset --hard`, `git clean -f`, force push) without being asked.

### Coding

- State assumptions; when a request is ambiguous and the choice changes the result, ask.
- Write the minimum code that solves the problem: no speculative features, abstractions or configurability.
- Keep code flat: guard clauses and early returns, no more than two levels of indentation in a function body.
- Change only what the task needs; match the existing style; mention unrelated problems instead of fixing them.
- Comments only for what the code cannot say (hidden constraint, workaround, external quirk).
- Define how to verify the change (a test that fails before and passes after) and report results faithfully.
- UI for pilots, officers or technicians follows the aviation color code: see the `aviation-ui-colors` skill.
<!-- wilco-ai-harness:end -->
