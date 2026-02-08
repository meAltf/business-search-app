## Data setup

### docker-compose

```yaml
services:
  elastic:
    image: docker.elastic.co/elasticsearch/elasticsearch:9.0.2
    ports:
    - 9200:9200
    environment:
    - discovery.type=single-node
    - xpack.security.enabled=false
    - xpack.security.http.ssl.enabled=false
  kibana:
    image: docker.elastic.co/kibana/kibana:9.0.2
    ports:
    - 5601:5601
    environment:
    - ELASTICSEARCH_HOSTS=http://elastic:9200
  data-setup:
    image: vinsdocker/elasticsearch-business-datasetup
    profiles:
      - data-setup
    environment:
    - ELASTICSEARCH_HOST=http://elastic:9200
```

### Instructions

- pull this image as it might take some time - `docker pull vinsdocker/elasticsearch-business-datasetup:latest`
- Run this command `docker compose up`
- This will start `elasticsearch` and `kibana`. NOT `data-setup`.
- Wait for elasticsearch and kibana to be up and running.
- Open another terminal and navigate to the path where you have this yaml file.
- Run this command `docker compose run --rm data-setup`. This will start the data-setup service to setup the indices and add the data.