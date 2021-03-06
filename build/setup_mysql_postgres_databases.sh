#!/usr/bin/env bash

set -e

echo "### Bringing Down Any Docker Containers that May Be Running ###"
time docker-compose down --rmi all

echo "### Bringing Up postgres, mysql Images ###"
time docker-compose up -d postgres mysql

echo "### Checking Docker Images"
docker ps

# import setup functions
. build/setup_db_scripts.sh

# run setup scripts for local databases
time setup_mysql $MYSQL_SCRIPT 127.0.0.1 13306 grant
time setup_postgres $POSTGRES_SCRIPT 127.0.0.1 15432

echo "Postgres and MySQL Databases are ready!"