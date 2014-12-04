#!/bin/sh

PGPASSWORD=Passw0rd psql -h localhost -d senescyt -U thoughtworks -a -c "$1"