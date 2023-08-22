
minikube start


kubectl apply -f ingress
minikube addons enable kong
export PROXY_IP=$(minikube service -n kong kong-proxy --url | head -1)
echo $PROXY_IP


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

sleep 450


sh bootstrap/cassandra/init.sh

sleep 50


kubectl apply -f services/user
kubectl apply -f services/hotel
kubectl apply -f services/search-consumer
kubectl apply -f services/search
kubectl apply -f services/booking
kubectl apply -f services/booking-management
kubectl apply -f services/archival
kubectl apply -f services/notification

sleep 100


#kubectl port-forward -n kong --address 0.0.0.0 service/kong-proxy 8001:80

