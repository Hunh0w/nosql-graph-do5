# Stackexchange

```
WITH "https://api.stackexchange.com/2.2/questions?pagesize=100&order=desc&sort=creation&tagged=neo4j&site=stackoverflow&filter=!5-i6Zw8Y)4W7vpy91PMYsKM-k9yzEsSC1_Uxlf" as url
CALL apoc.load.json(url) YIELD value
UNWIND value['items'] as item
    MERGE (question:Question {title: item["title"], content: item["body_markdown"], is_answered: item["is_answered"]})
    MERGE (user:User {id: item['owner']['user_id']})-[:ASKED]-(question)
    WITH item, question, [t in item['tags']] as tags
        UNWIND tags as tag
            MERGE (question)-[:TAGGED]->(t:Tag {tag: tag})
    WITH question, [a in item['answers']] as answers
        UNWIND answers as answer
            MERGE (a:Answer{id: answer['answer_id'], is_accepted: answer['is_accepted'], content: answer['body_markdown']})-[:ANSWERS]->(question)
            MERGE (u:User{id: answer["owner"]['user_id']})-[:PROVIDED]->(a)   
```

## Schema visualisation
```
CALL db.schema.visualization
```

