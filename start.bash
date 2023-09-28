#!/usr/bin/env bash


kubectl delete namespace hands-on

./gradlew build && docker-compose build

kubectl apply -f kubernetes/hands-on-namespace.yml

kubectl config set-context $(kubectl config current-context) --namespace=hands-on

for f in kubernetes/helm/components/*; do helm dep up "$f"; done

for f in kubernetes/helm/environments/*; do helm dep up "$f"; done

helm install hands-on-dev-env kubernetes/helm/environments/dev-env -n hands-on

