---
name: commit-and-pr
description: Flightwatching workflow to branch, commit with Conventional Commits, pass the pre-commit hooks and open a pull request. Use when asked to commit, push, or open or update a pull request in a Flightwatching repository.
---

# Commit and pull request

## 1. Branch

If the current branch is `main`, `master` or `prod`, create one first:

```bash
git switch -c <type>/<short-topic>   # e.g. fix/acars-empty-block
```

## 2. Check before committing

```bash
pre-commit run --files $(git diff --name-only HEAD) $(git ls-files --others --exclude-standard)
```

Hooks that fix files (formatters) exit non-zero after fixing: run the command again. If it still fails, fix the code. Never skip hooks.

Run the repository's tests as its `AGENTS.md` describes.

## 3. Commit

Stage only the files of the change (no `git add -A` with unrelated files), then:

```bash
git commit -m "<type>(<scope>): <description>"
```

- Types: `feat`, `fix`, `refactor`, `perf`, `test`, `docs`, `chore`, `build`, `ci`, `style`, `revert`.
- Description: imperative, lower case, no final period, under 72 characters.
- Breaking change: `!` after the type or scope, and a `BREAKING CHANGE:` footer.
- One logical change per commit.

The `commit-msg` hook rejects any other format: rewrite the message, do not bypass it.

## 4. Pull request

```bash
git push -u origin HEAD
gh pr create --fill --base <default branch>
```

The PR description says what changed, why, and how it was verified (commands and results). Never merge it: a human does.
