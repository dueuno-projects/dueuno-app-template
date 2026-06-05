#!/bin/zsh
set -euo pipefail

USER_NAME="dueunoapp"
FULL_NAME="DueunoApp Service"
HOME_DIR="/Users/${USER_NAME}"
SHELL_BIN="/usr/bin/false"

# 1) Pre-check: sysadminctl must exist
if [[ ! -x /usr/sbin/sysadminctl ]]; then
  echo "ERROR: /usr/sbin/sysadminctl not found. Cannot proceed."
  exit 1
fi

echo "== Checking if user '${USER_NAME}' exists =="
if dscl . -read "/Users/${USER_NAME}" >/dev/null 2>&1; then
  echo "User record exists in Directory Service."

  # Detect "half-created" users: missing UniqueID
  if ! dscl . -read "/Users/${USER_NAME}" UniqueID >/dev/null 2>&1; then
    echo "User '${USER_NAME}' exists but is missing UniqueID (incomplete). Deleting and recreating..."
    sudo /usr/sbin/sysadminctl -deleteUser "${USER_NAME}" || true
    sudo rm -rf "${HOME_DIR}" || true
  else
    echo "User '${USER_NAME}' looks complete (UniqueID present)."
  fi
else
  echo "User record does not exist."
fi

# Re-check after possible deletion
if dscl . -read "/Users/${USER_NAME}" >/dev/null 2>&1; then
  echo "User '${USER_NAME}' already exists and is complete. Nothing to do."
else
  echo "== Creating user '${USER_NAME}' =="
  sudo /usr/sbin/sysadminctl -addUser "${USER_NAME}" -fullName "${FULL_NAME}" -home "${HOME_DIR}" -shell "${SHELL_BIN}"

  echo "== Creating home directory (if needed) =="
  sudo /usr/sbin/createhomedir -c -u "${USER_NAME}" >/dev/null || true

  echo "== Setting a random strong password (required by macOS policy) =="
  PASS="$(LC_ALL=C tr -dc 'A-Za-z0-9!@#%^_+=' </dev/urandom | head -c 32)"
  sudo /usr/sbin/sysadminctl -resetPasswordFor "${USER_NAME}" -newPassword "${PASS}"

  echo "Password for '${USER_NAME}': ${PASS}"
fi

echo "== Final checks =="
echo "-- id ${USER_NAME}"
id "${USER_NAME}"

echo "-- dscl attributes"
dscl . -read "/Users/${USER_NAME}" UniqueID PrimaryGroupID NFSHomeDirectory UserShell RealName

echo "DONE."
