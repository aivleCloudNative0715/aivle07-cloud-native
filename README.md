# 

## Model
www.msaez.io/#/88013406/storming/de2bd2f106a41aaac5496fda1cf063f9

## Before Running Services
### Make sure there is a Kafka server running
```
cd kafka
docker-compose up
```
- Check the Kafka messages:
```
cd infra
docker-compose exec -it kafka /bin/bash
cd /bin
./kafka-console-consumer --bootstrap-server localhost:9092 --topic
```

## Run the backend micro-services
See the README.md files inside the each microservices directory:

- user
- author
- aiservice
- platform
- writing
- point


## Run API Gateway (Spring Gateway)
```
cd gateway
mvn spring-boot:run
```

## Test by API
- user
```
 http :8088/users id="id"email="email"userName="userName"hasActiveSubscription="hasActiveSubscription"message="	message"subscriptionDueDate="subscriptionDueDate"isKt="isKT"
```
- author
```
 http :8088/authors id="id"authorEmail="authorEmail"authorName="authorName"bio="bio"representativeWork="representativeWork"portfolio="portfolio"isApproved="isApproved"
```
- aiservice
```
 http :8088/bookWorks id="id"manuscriptId="manuscriptId"title="title"summary="summary"keywords="keywords"coverImageUrl="coverImageUrl"ebookUrl="ebookUrl"category="category"price="price"status="status"
```
- platform
```
 http :8088/books id="id"title="title"authorName="authorName"summary="summary"category="category"coverImageUrl="coverImageUrl"ebookUrl="ebookUrl"price="price"viewCount="viewCount"
 http :8088/bookViews bookId="bookId"title="title"authorName="authorName"summary="summary"category="category"viewCount="viewCount"isbestseller="isbestseller"
```
- writing
```
 http :8088/manuscripts id="id"authorId="authorId"title="title"content="content"status="status"lastModifiedAt="lastModifiedAt"summary="summary"keywords="keywords"
```
- point
```
 http :8088/points id="id"currentPoints="currentPoints"userId="userId"isKTmember="isKTmember"isKTmember="isKTmember"
```


## Run the frontend
```
cd frontend
npm i
npm run serve
```

## Test by UI
Open a browser to localhost:8088

## Required Utilities

- httpie (alternative for curl / POSTMAN) and network utils
```
sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie
```

- kubernetes utilities (kubectl)
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

- aws cli (aws)
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- eksctl 
```
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
```
