# Infrastructure

Placeholder for deployment and environment definitions.

- **k8s/** – Kubernetes manifests (Deployments, Services, ConfigMaps) or Helm charts.
- **terraform/** – Optional: Terraform for cloud resources (Kafka, DB, networking).
- **scripts/** – Optional: Topic creation, schema setup, health checks.

Each service is deployed independently; use the same image built from `docker/Dockerfile.<service>`.
