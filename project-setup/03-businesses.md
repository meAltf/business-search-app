## Business Search API Implementation



### Index Details

- Index name: `businesses`
- Index mapping

```json
{
  "settings": {
    "analysis": {
      "analyzer": {
        "custom_description_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "stop"
          ]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      },
      "address": {
        "type": "text"
      },
      "description": {
        "type": "text",
        "analyzer": "custom_description_analyzer"
      },
      "state": {
        "type": "keyword"
      },
      "location": {
        "type": "geo_point"
      },
      "category": {
        "type": "text",
        "fields": {
          "raw": {
            "type": "keyword"
          }
        }
      },
      "offerings": {
        "type": "text",
        "fields": {
          "raw": {
            "type": "keyword"
          }
        }
      },
      "avg_rating": {
        "type": "float"
      },
      "num_of_reviews": {
        "type": "integer"
      },
      "url": {
        "type": "keyword",
        "index": false
      }
    }
  }
}
```

### Sample Data
- Note: This is test data. It might not be accurate.

```json
{
  "name": "Walmart",
  "address": "Walmart, 2750 S Woodlands Village Blvd...",
  "description": null,
  "state": "Arizona",
  "location": {
    "lat": 35.1764629,
    "lon": -111.66476829999999
  },
  "category": [
    "Toy store",
    "Clothing store",
    "Supermarket"
  ],
  "offerings": [
    "Passport photos"
  ],
  "avg_rating": 3.8,
  "num_of_reviews": 3200,
  "url": "https://www.google.com/maps/place/...."
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
    - ./business-index-settings.json:/usr/share/app/business-search/business-index-settings.json
```

## Understanding the Data Structure

The data is designed so that `suggestions → search_term` corresponds to `businesses → category`. In other words, suggested terms like *restaurants* or *auto repair service* represent business categories.

## How Users Search

- Users can search using a suggested term (category) or enter their own search term manually.
- Facets/Filters allow users to refine their search based on:
    - **Location**: Example – within **10 miles**
    - **State**: Restrict results to a specific **U.S. state**
    - **Rating**: Example – businesses rated **4 stars and above**
    - **Service Offerings**: Filter based on `offerings`  (Aggregate)

## Example Search Scenario 1

Consider the following search query:

**"Find 'Grocery Store'"**

This search does not have any filters:
- **Search Query**: Grocery Store

In this case, Elasticsearch Query would look like this.

```json
{
  "bool": {
    "filter": [
    ],
    "must": [
      {
        "multi_match": {
          "fields": [
            "name^2",
            "category^1.5",
            "offerings^1.5",
            "address^1.2",
            "description"
          ],
          "query": "Grocery Store",
          "type": "most_fields",
          "operator": "and",
          "fuzziness": "1",
          "prefix_length": 2
        }
      }
    ],
    "should": [
      {
        "term": {
          "category.raw": {
            "boost": 5, 
            "value": "Grocery Store",
            "case_insensitive": true
          }
        }
      }
    ]
  }
}
```

## Example Search Scenario 2

Consider the following search query:

**"Find 'Grocery Store' within a 10-mile radius, with a business rating of 3 or higher, that offers 'Quick Bite'."**

This search applies multiple filters:
- **Search Query**: Grocery store
- **Location**: Within **10 miles**
- **Rating**: **3 stars and above**
- **Offerings**: Includes **Quick Bite**

In this case, Elasticsearch Query would look like this.

```json
{
  "bool": {
    "filter": [
      {
        "range": {
          "avg_rating": {
            "gte": 3
          }
        }
      },
      {
        "geo_distance": {
          "location": {
            "lat": 34.02449,
            "lon": -118.225618
          },
          "distance": "10mi"
        }
      },
      {
        "term": {
          "offerings.raw": {
            "value": "Quick Bite",
            "case_insensitive": true
          }
        }
      }
    ],
    "must": [
      {
        "multi_match": {
          "fields": [
            "name^2",
            "category^1.5",
            "offerings^1.5",
            "address^1.2",
            "description"
          ],
          "query": "Grocery Store",
          "type": "most_fields",
          "operator": "and",
          "fuzziness": "1",
          "prefix_length": 2
        }
      }
    ],
    "should": [
      {
        "term": {
          "category.raw": {
            "boost": 5, 
            "value": "Grocery Store",
            "case_insensitive": true
          }
        }
      }
    ]
  }
}
```
- Filters are applied based on user selection.


### Aggregate Request

```json
{
  "terms": {
    "field": "offerings.raw",
    "size": 10
  }
}
```

### API Details

- GET Request
- Request Endpoint: `/api/search` with below parameters

| Parameter  | Type      | Description                          |
|------------|-----------|--------------------------------------|
| `query`    | String    | The search query string. **Cannot be null.** Example: `Grocery Store` |
| `distance` | String    | The distance parameter for location-based searches. Example: `10mi` |
| `latitude` | Double    | The latitude of the user's location.       |
| `longitude`| Double    | The longitude of the user's location.      |
| `rating`   | Double    | To filter businesses by rating. Example: `rating >= 4`   |
| `state`    | String    | To filter businesses within a U.S. state.    |
| `offerings`| String    | The offerings that the service provides. Example: `Quick Bite` |
| `page`     | Integer   | The page number for pagination. Default: `0`.  |
| `size`     | Integer   | The number of results per page. Default: `10`.  |

- Response

```json
{
  "results": [
    {
      "id": "V2b51JQBpYUXtTcih37N",
      "name": "Walmrt",
      "description": null,
      "address": "Walmrt, walmart, 4444 W Vine St...",
      "category": [
        "Grocery store"
      ],
      "offerings": [],
      "rating": 4.5,
      "reviewsCount": 55,
      "url": "https://www.google.com/maps/place/...."
    },
    {
      "id": "hGT51JQBpYUXtTcidqaN",
      "name": "Walmart",
      "description": null,
      "address": "Walmart, 263 Walmart Dr...",
      "category": [
        "Shopping mall",
        "Grocery store"
      ],
      "offerings": [],
      "rating": 5,
      "reviewsCount": 5,
      "url": "https://www.google.com/maps/place/...."
    },
    {
      "id": "U5H71JQBpYUXtTciVDlr",
      "name": "Walmart",
      "description": null,
      "address": "Walmart, 300 Walmart Dr...",
      "category": [
        "Toy store",
        "Clothing store",
        "Grocery store",     
        "Hardware store"
      ],
      "offerings": [ "Repair services", "Oil change" ],
      "rating": 3.9,
      "reviewsCount": 1041,
      "url": "https://www.google.com/maps/place/...."
    }
    ...
    ...
  ],
  "facets": [
    {
      "name": "offerings-term-aggregate",
      "items": [
        {
          "key": "Repair services",
          "count": 2814
        },
        {
          "key": "Service guarantee",
          "count": 935
        },
        {
          "key": "Check cashing",
          "count": 657
        },
        {
          "key": "Oil change",
          "count": 295
        },
        ...
        ...
      ]
    }
  ],
  "pagination": {
    "page":3,
    "size":10,
    "totalElements":14937,
    "totalPages":1494
  },
  "timeTaken":27
}
```

