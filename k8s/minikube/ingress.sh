kubectl apply -f ingress
minikube addons enable kong
export PROXY_IP=$(minikube service -n kong kong-proxy --url | head -1)
echo $PROXY_IP


# example: curl -i $PROXY_IP/api/v1/user/test


