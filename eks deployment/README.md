Deployment instructions

1. aws-vault exec data-beta -- eksctl create cluster -f cluster_create.yml

2. enable logging if not present in cluster create: aws-vault exec data-beta -- eksctl utils update-cluster-logging --enable-types=all --region=us-east-2 --cluster=airbyte-dev

3. point kubectl to deployed cluster

4.  aws-vault exec data-beta -- aws eks update-kubeconfig --name airbyte-dev --region us-east-2 or  aws-vault exec data-beta -- aws eks update-kubeconfig --name airbyte-dev --region us-east-2 --role-arn arn:aws:iam::406316936099:role/kriti.kathuria

5. aws-vault exec data-beta -- kubectl create ns airbyte

6. aws-vault exec data-beta -- kubectl apply -k kube/overlays/stable --namespace="airbyte" , after changing `namespace in https://github.com/airbytehq/airbyte/blob/57d17ef25edb98071b28fee063fc87010b422c8d/kube/overlays/stable/kustomization.yaml to airbyte`

7. aws-vault exec data-beta -- eksctl create nodegroup --config-file=cluster_create.yml (if node group added later)

8. aws-vault exec data-beta -- kubectl port-forward svc/airbyte-webapp-svc -n airbyte 8080:80

9. add users to admin group - 
aws-vault exec data-beta -- kubectl edit configmap aws-auth -n kube-system
```
mapRoles: |
    - groups:
      - system:masters
      rolearn: arn:aws:iam::aws-account-id:role/aws-username
      username: aws-username
```

10. for exposing: https://kubernetes.io/docs/tasks/access-application-cluster/create-external-load-balancer/
 aws-vault exec data-beta -- kubectl edit service  airbyte-webapp-svc -n airbyte
 aws-vault exec data-beta -- kubectl expose service airbyte-webapp-svc --port=443 --target-port=80 --name=airbyte-webapp-elb -n airbyte -- not needed
 aws-vault exec data-beta -- kubectl describe services  -n airbyte




p81 - 159.89.168.163/32 172.104.206.252/32
