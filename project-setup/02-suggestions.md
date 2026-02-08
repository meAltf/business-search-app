## Suggestions API Implementation



### Index Details

- Index name: `suggestions`
- Index mapping

```json
{
  "mappings": {
    "properties": {
      "search_term": {
        "type": "completion"
      }
    }
  }
}
```

- To override this mapping
```yaml
  data-setup:
    image: vinsdocker/elasticsearch-business-datasetup
    profiles:
      - data-setup
    environment:
    - ELASTICSEARCH_HOST=http://elastic:9200
    volumes:
    - ./suggestion-index-settings.json:/usr/share/app/business-search/suggestion-index-settings.json
```


### API Details

| Parameter       | Type          | Description                                           |
|------------------|---------------|-------------------------------------------------------|
| **Request Type**  | GET           | The HTTP method used for the request.                |
| **Request Endpoint** | `/api/suggestions?prefix=res&limit=10` | The API endpoint to retrieve suggestions based on a prefix and limit. |
| **Query Parameters** |  - `prefix`  | A string used to filter suggestions (Example: `res`).   |
|                    |  - `limit`   | The maximum number of suggestions to return (Example: `10`). |
| **Response**      | `List<String>` | A list of suggestions based on the provided parameters. |
