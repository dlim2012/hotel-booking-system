
kubectl create secret generic hb-elasticsearch-es-elastic-user --from-literal=elastic=changeme

kubectl apply -f bootstrap/cassandra
kubectl apply -f bootstrap/kafka
kubectl apply -f bootstrap/mysql-booking
kubectl apply -f bootstrap/mysql-hotel
kubectl apply -f bootstrap/mysql-user
kubectl apply -f bootstrap/redis
kubectl apply -f bootstrap/elastic-search/crds.yaml
kubectl apply -f bootstrap/elastic-search/operator.yaml
kubectl apply -f bootstrap/elastic-search/elasticsearch-config.yaml
kubectl apply -f bootstrap/elastic-search/kibana-config.yaml
