"""Initialize service-owned SQLite databases without importing legacy accounts.

Run: python database/init_databases.py
SQL scripts are repeatable; this is not a migration tool for existing old schemas.
"""
from pathlib import Path
import sqlite3


def main():
    directory = Path(__file__).resolve().parent
    for service in ("shop", "employee", "product", "order"):
        path = directory / ("mall_" + service + ".db")
        sql = (directory / (service + ".sql")).read_text(encoding="utf-8")
        connection = sqlite3.connect(str(path))
        try:
            connection.execute("PRAGMA foreign_keys = ON")
            connection.executescript("BEGIN;\n" + sql + "\nCOMMIT;")
            integrity = connection.execute("PRAGMA integrity_check").fetchone()[0]
            if integrity != "ok":
                raise RuntimeError(str(path) + ": " + integrity)
            if connection.execute("PRAGMA foreign_key_check").fetchall():
                raise RuntimeError(str(path) + ": foreign key violations")
            print(str(path) + ": ok")
        finally:
            connection.close()


if __name__ == "__main__":
    main()
