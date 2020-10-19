#!/bin/sh
unset http_proxy

d="{\"amount\": \"${RANDOM}.$(( RANDOM % 100 ))\", \"timestamp\": \"2020-10-18T$(date -u +%H:%M:%S).000Z\"}"


curl -i -X POST -H "Content-Type: application/json" -H "Accept: application/json" --data "$d" http://localhost:8080/transactions
