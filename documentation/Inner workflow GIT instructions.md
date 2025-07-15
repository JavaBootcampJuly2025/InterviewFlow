# 👩‍💻 Git Workflow Guide (Best Practices)

**This guide will help you work effectively as a team and keep the codebase clean and organized.**

---

## 1. Clone the Repository (First Time Only)

```bash
git clone https://github.com/yourusername/interviewflow.git
cd interviewflow
```

---

## 2. Always Pull Latest Changes Before You Start

**Sync your local repo with the latest code:**

```bash
git checkout main
git pull origin main
```

---

## 3. Create a New Branch for Each Task or Feature

**Never commit directly to `main`.**
Create a new branch for each new feature, bugfix, or task. Name your branch clearly:

```bash
git checkout -b feature/short-description
# Example: git checkout -b feature/add-login-page
```

---

## 4. Make Changes and Commit Locally

* Work on your branch.
* Write clear, descriptive commit messages.

```bash
# Stage the files you changed
git add .

# Commit with a meaningful message
git commit -m "Add login page UI"
```

---

## 5. Pull Latest Main Before You Push

**This avoids conflicts by making sure your branch is up-to-date with the main branch:**

```bash
git checkout main
git pull origin main
git checkout feature/your-branch
git merge main
```

*Resolve any conflicts if necessary.*

---

## 6. Push Your Branch to GitHub

```bash
git push origin feature/your-branch
```

---

## 7. Create a Pull Request (PR)

* Go to GitHub.
* Find your recently pushed branch.
* Click “Compare & pull request”.
* Fill in details (what you changed, why) and submit.

---

## 8. Code Review & Merge

* Team reviews the PR, leaves comments or requests changes.
* Once approved, **merge** into `main` (usually via GitHub).

---

## 9. Pull Latest Main Again Before Starting New Task

**Repeat the cycle for every new task or feature.**

---

# 🏆 Best Practices

* **One branch, one task:** Always create a new branch for each separate piece of work.
* **Descriptive names:** Use clear branch and commit names (`feature/add-dashboard`, `fix/typo-header`).
* **Small PRs:** Make pull requests focused and small for easier reviews.
* **Sync often:** Frequently pull `main` to stay up to date.
* **Never commit directly to main:** Always use feature branches and PRs.
* **Resolve conflicts early:** If you see a conflict, address it as soon as possible.
* **Write clear PR descriptions:** Explain what and why, not just what you changed.

---
