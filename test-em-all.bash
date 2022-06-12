#!/usr/bin/env bash
#
# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
: ${HOST=localhost}
: ${PORT=8080}
: ${PROD_ID_REVS_RECS=1}
: ${PROD_ID_NOT_FOUND=13}
: ${PROD_ID_NO_RECS=113}
: ${PROD_ID_NO_REVS=213}

function setupTestdata() {
  body=\
    '{"productId":1,"name":"product 1","weight":1, "recommendations":[
    {"recommendationId":1,"author":"author
    1","rate":1,"content":"content 1"},
    {"recommendationId":2,"author":"author
    2","rate":2,"content":"content 2"},
    {"recommendationId":3,"author":"author
    3","rate":3,"content":"content 3"}
    ], "reviews":[
    {"reviewId":1,"author":"author 1","subject":"subject
    1","content":"content 1"},
    {"reviewId":2,"author":"author 2","subject":"subject
    2","content":"content 2"},
    {"reviewId":3,"author":"author 3","subject":"subject
    3","content":"content 3"}
    ]}'
  recreateComposite 1 "$body"
  body=\
    '{"productId":113,"name":"product 113","weight":113, "reviews":[
    {"reviewId":1,"author":"author 1","subject":"subject
    1","content":"content 1"},
    {"reviewId":2,"author":"author 2","subject":"subject
    2","content":"content 2"},
    {"reviewId":3,"author":"author 3","subject":"subject
    3","content":"content 3"}
    ]}'
  recreateComposite 113 "$body"

  body=\
    '{"productId":213,"name":"product 213","weight":213,
    "recommendations":[
    {"recommendationId":1,"author":"author
    1","rate":1,"content":"content 1"},
    {"recommendationId":2,"author":"author
    2","rate":2,"content":"content 2"},
    {"recommendationId":3,"author":"author
    3","rate":3,"content":"content 3"}
    ]}'
  recreateComposite 213 "$body"
}

function recreateComposite() {
  local productId=$1
  local composite=$2
  assertCurl 200 "curl -X DELETE http://$HOST:$PORT/product-
    composite/${productId} -s"
  curl -X POST http://$HOST:$PORT/product-composite -H "Content-Type:
    application/json" --data "$composite"

}

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
    echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
    echo  "- Failing command: $curlCmd"
    echo  "- Response Body: $RESPONSE"
    exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

function testUrl() {
    url=$@
    if $url -ks -f -o /dev/null
    then
        return 0
    else
        return 1
    fi
}

function waitForService() {
    url=$@
    echo -n "Wait for: $url .... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 3
            echo -n ", retry #$n "
        fi
    done
    echo "DONE, continues..."
}

set -e

echo "Start tests:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test enviroment"
    echo "$ docker-compose down --remove-orphans"
    docker-compose down --remove-orphans
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

waitForService curl http://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS

setupTestdata
# Verify that a normal request works, expect three recommendations and three reviews
assertCurl 200 "curl http://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS -s"
assertEqual $PROD_ID_REVS_RECS $(echo $RESPONSE | jq .productId)
echo "$RESPONSE"
assertEqual 3 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")

# Verify that a 404 (Not Found) error is returned for a non-existing productId ($PROD_ID_NOT_FOUND)
assertCurl 404 "curl http://$HOST:$PORT/product-composite/$PROD_ID_NOT_FOUND -s"
assertEqual "No product found for productId: $PROD_ID_NOT_FOUND" "$(echo $RESPONSE | jq -r .message)"

# Verify that no recommendations are returned for productId $PROD_ID_NO_RECS
assertCurl 200 "curl http://$HOST:$PORT/product-composite/$PROD_ID_NO_RECS -s"
assertEqual $PROD_ID_NO_RECS $(echo $RESPONSE | jq .productId)
assertEqual 0 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")

# Verify that no reviews are returned for productId $PROD_ID_NO_REVS
assertCurl 200 "curl http://$HOST:$PORT/product-composite/$PROD_ID_NO_REVS -s"
assertEqual $PROD_ID_NO_REVS $(echo $RESPONSE | jq .productId)
assertEqual 3 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 0 $(echo $RESPONSE | jq ".reviews | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a productId that is out of range (-1)
assertCurl 422 "curl http://$HOST:$PORT/product-composite/-1 -s"
assertEqual "\"Invalid productId: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a productId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:$PORT/product-composite/invalidProductId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"



if [[ $@ == *"stop"* ]]
then
    echo "We are done, stopping the test enviroment..."
    echo "$ docker-compose down"
    docker-compose down
fi

echo "End, all tests OK:" `date`