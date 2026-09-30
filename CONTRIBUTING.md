# Contributing to SlotHub

This document describes how we work: from picking a task to shipping a release.
If something here is unclear or outdated, open a `type:chore` issue — process is code too.

## Language

- Issues and discussions: **Russian**.
- Code, comments, commit messages, branch names, PR titles and descriptions: **English**.

## Workflow overview

```
Backlog → Ready → In Progress → In Review → Done
```

1. **Pick a task** from the `Ready` column of the sprint board. Assign it to yourself and move it to `In Progress`.
   Do not take more than one task at a time unless you are blocked.
2. **Clarify first.** If the task is ambiguous, ask in the issue comments *before* writing code.
   Asking questions is part of the job, guessing is not.
3. **Create a branch** from an up-to-date `main`.
4. **Commit** in small, meaningful steps.
5. **Open a pull request**, fill in the template, make sure CI is green.
6. **Request review** from the code owner. Address every comment.
7. **Merge** (squash) after approval. The issue closes automatically and moves to `Done`.

If you are blocked, add the `blocked` label and write in the issue what exactly blocks you.

## Branches

`main` is always releasable. Direct pushes to `main` are forbidden — every change goes through a pull request.

Branch name format: `<type>/<issue-number>-<short-description>`

| Type | When | Example |
|---|---|---|
| `feature/` | new functionality | `feature/12-cancel-booking` |
| `bugfix/` | bug fix | `bugfix/31-wrong-booking-price` |
| `hotfix/` | urgent production fix | `hotfix/40-double-booking` |
| `refactor/` | refactoring without behavior change | `refactor/25-extract-price-calculator` |
| `chore/` | build, CI, dependencies, docs | `chore/18-update-readme` |

Keep your branch up to date with `main` by **rebasing**:

```bash
git fetch origin
git rebase origin/main
git push --force-with-lease
```

Never use `--force` without `-with-lease`, and never force-push to someone else's branch.

## Commit messages

We use [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <summary in imperative mood>

<optional body: what and why, not how>
```

- **type**: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `ci`, `perf`
- **scope**: feature area — `venue`, `space`, `booking`, `common`, `db`, etc.
- **summary**: imperative, lowercase, no period, up to ~72 chars.

Good:

```
feat(booking): add booking cancellation
fix(booking): round price up to started hour
test(venue): cover city filter with integration test
```

Bad: `fix`, `changes`, `WIP`, `fixed bug with bookings.`

## Pull requests

- One PR = one issue. Keep PRs small; if a PR grows over ~400 changed lines, consider splitting it.
- PR title follows the commit message format — it becomes the squash commit message.
- Link the issue in the description: `Closes #12`.
- Explain **how to test** the change: requests, test data, expected result.
- CI must be green before requesting review.
- Do not mix refactoring with feature work in one PR unless it is required for the feature.

### Code review

Reviewers use these prefixes in comments:

| Prefix | Meaning |
|---|---|
| `blocking:` | must be fixed before merge |
| `suggestion:` | worth considering, author decides |
| `question:` | reviewer wants to understand; answer in the thread |
| `nit:` | minor style issue, optional |
| `praise:` | something done well |

As an author:

- Reply to every comment: fix it, or explain why not. Do not silently resolve threads.
- Push fixes as new commits during review (easier to re-review), they are squashed on merge anyway.
- After addressing all comments, re-request review.

## Definition of Ready

A task can be moved to `Ready` when:

- [ ] The goal and business value are clear.
- [ ] Acceptance criteria are written.
- [ ] Dependencies are resolved or known.
- [ ] The task is estimated.

## Definition of Done

A task is done when:

- [ ] All acceptance criteria are met.
- [ ] Code is covered by tests (unit and/or integration), `./mvnw verify` passes.
- [ ] Database changes are made via a new Liquibase changeset (never edit applied changesets).
- [ ] API changes are visible in Swagger (annotations are up to date).
- [ ] README / docs are updated if behavior or setup changed.
- [ ] PR is approved and merged, the branch is deleted.

## Database migrations

- One logical change = one changeset file: `NNN-short-description.sql` in `db/changelog/changes`, included in `db.changelog-master.yaml`.
- **Never modify a changeset that has already been merged to `main`.** Create a new one.
- Provide a `--rollback` section where possible.
- Think about backward compatibility: the old version of the app may still be running during deployment.

## Estimates

We estimate in story points (relative complexity, not hours): `1, 2, 3, 5, 8`.
Anything bigger than `8` must be split.

## Releases

- Every sprint ends with a release, if there is something to release.
- Releases are git tags `vMAJOR.MINOR.PATCH` on `main` with notes in [CHANGELOG.md](CHANGELOG.md) and GitHub Releases.
- Hotfixes: branch `hotfix/*` from `main`, PR, review, merge, patch release.
