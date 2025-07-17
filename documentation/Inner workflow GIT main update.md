# How to Update Your Local Project with Latest Changes from Origin

Follow these steps **before starting any new work** to ensure your local project is up-to-date with the latest changes from the remote repository.

---

## 1. Open Terminal and Go to Your Project Directory

```sh
cd path/to/your/project
```

---

## 2. (Optional) Check Your Current Branch

```sh
git branch
```

*Make sure you are on the branch where you want to apply updates or create your feature branch if needed.*

---

## 3. Fetch All Latest Changes from Remote

```sh
git fetch origin
```

---

## 4. Pull Latest Changes into Your Current Branch

If you want to update your branch with the latest changes from `main`:

```sh
git pull origin main
```

*Replace `main` with your base branch if needed (e.g., `develop`).*

---

## 5. (Recommended) Rebase Your Feature Branch (if working on a feature branch)

To update your feature branch with the latest from `main`:

```sh
git fetch origin
git rebase origin/main
```

*Or use `git rebase origin/develop` if your base branch is `develop`.*

---

## 6. Resolve Any Merge or Rebase Conflicts (if prompted)

If you see any conflicts, follow Git’s instructions to resolve them before proceeding.

---

Now your local project is up-to-date and ready for development!
