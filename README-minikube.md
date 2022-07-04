# Macos Environment

## Notice
This current projet build correctly on a linux (I tested fedora 31), but it does not work "properly" on macosx using minikube has docker-engine.
//@todo check with podman on macos

### start minikube 

sine you will need a lot of ressource, I use a profile 'khool-kube-vault' with 4 cpu, 16g memory and 60g disk.
(to be precise i use hyperkit on x64_86 Macbook Pro ) (tips : ```minikube profile list```)

```shell
minikube -p khool-kube-vault start \
         --cpus='4' --memory='16g' --disk-size=60g &&\
         minikube -p khool-kube-vault addons enable ingress &&\
         minikube -p khool-kube-vault addons enable ingress-dns &&\
         eval $(minikube -p khool-kube-vault docker-env); &&\
         export DOCKER_EXPOSE_IP=$(minikube -p khool-kube-vault ip)
# 😄  [khool-kube-vault] minikube v1.26.0 on Darwin 12.4
#     ▪ MINIKUBE_ACTIVE_DOCKERD=khool-kube-vault
# ✨  Using the hyperkit driver based on existing profile
# 👍  Starting control plane node khool-kube-vault in cluster khool-kube-vault
# 🔄  Restarting existing hyperkit VM for "khool-kube-vault" ...
# ❗  This VM is having trouble accessing https://k8s.gcr.io
# 💡  To pull new external images, you may need to configure a proxy: https://minikube.sigs.k8s.io/docs/reference/networking/proxy/
# 🐳  Preparing Kubernetes v1.24.1 on Docker 20.10.16 ...
# 🔎  Verifying Kubernetes components...
#     ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
#     ▪ Using image gcr.io/k8s-minikube/minikube-ingress-dns:0.0.2
#     ▪ Using image k8s.gcr.io/ingress-nginx/controller:v1.2.1
#     ▪ Using image k8s.gcr.io/ingress-nginx/kube-webhook-certgen:v1.1.1
#     ▪ Using image k8s.gcr.io/ingress-nginx/kube-webhook-certgen:v1.1.1
# 🔎  Verifying ingress addon...
# 🌟  Enabled addons: ingress-dns, storage-provisioner, default-storageclass, ingress
# 🏄  Done! kubectl is now configured to use "khool-kube-vault" cluster and "default" namespace by default
```
tips : ```docker ps | grep -v "k8s_"```

### current issue with dynamic ip

currently you need to manually update ip value in [application.properties](integration-tests/vault-app/src/main/resources/application.properties) with ```minikube -p khool-kube-vault ip```

I tryed to have it updated dynamically, by implementing InitArg, but I could not make work properly: 

### run build 

```shell
mvn clean package -C -DDOCKER_EXPOSE_IP=$DOCKER_EXPOSE_IP
# ...
# Running io.quarkus.it.vault.VaultTest
# ...
# [INFO]
# [ERROR] Errors:
# [ERROR]   VaultTest.test » Runtime java.lang.RuntimeException: Failed to start quarkus
# [INFO]
# [ERROR] Tests run: 2, Failures: 0, Errors: 1, Skipped: 1
# [INFO]
# [INFO] ------------------------------------------------------------------------
# [INFO] Reactor Summary for Quarkus - Vault - Parent 1.1.1-SNAPSHOT:
# [INFO]
# [INFO] Quarkus - Vault - Parent ........................... SUCCESS [  0.960 s]
# [INFO] Quarkus - Vault - Model ............................ SUCCESS [  1.514 s]
# [INFO] Quarkus - Vault - Runtime .......................... SUCCESS [  4.805 s]
# [INFO] Quarkus - Vault - Deployment ....................... SUCCESS [  1.807 s]
# [INFO] Quarkus - Vault - Documentation .................... SUCCESS [  4.859 s]
# [INFO] Quarkus - Vault - Test Framework ................... SUCCESS [  0.111 s]
# [INFO] Quarkus - Vault - Integration Tests - Parent ....... SUCCESS [  0.007 s]
# [INFO] Quarkus - Vault - Integration Tests ................ SUCCESS [  0.137 s]
# [INFO] Quarkus - Vault - Integration Tests - Agroal ....... SUCCESS [  0.106 s]
# [INFO] Quarkus - Vault - Integration Tests - App .......... SUCCESS [ 01:18 min]
# [INFO] ------------------------------------------------------------------------
# [INFO] BUILD FAILURE
# [INFO] ------------------------------------------------------------------------
# [INFO] Total time:  57.343 s
# [INFO] Finished at: 2022-07-04T13:06:06+02:00
# [INFO] ------------------------------------------------------------------------
```

## weird behavious

init(String testProfileName)
https://github.com/quarkusio/quarkus/blob/bbe806e26ecffd9f19fbdf0cbb843cc837cde369/test-framework/common/src/main/java/io/quarkus/test/common/TestResourceManager.java#L93


## info :
 - https://www.testcontainers.org/supported_docker_environment/