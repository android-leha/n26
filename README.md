
# Transaction Statistics

## Description

Calculate transactions statistics from last 60 seconds

## API

POST /transactions - add transaction

DELETE /transactions - delete all transactions

GET /statistics - get Sum, Avg, Min, Max and Count

## Run UT

mvn clean test

## Run build with UT and IT

mvn clean install


## Problems with IT

Because required complexity was O(1), statistic updates asynchronously with request threads.

So, sometimes, borderline values not calculates correctly.

For example, IT test #3 sometimes fails because of this issue
