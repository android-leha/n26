#!/bin/sh

unset http_proxy

curl -i -X DELETE http://localhost:8080/transactions
