```
CALL apoc.load.json("file:///file.json") YIELD value;
```

```
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND keys(value) AS key
RETURN key, apoc.meta.cypher.type(value[key]);
```
```
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND  value.CVE_data_numberOfCVEs as Cnt
RETURN Cnt;
```
```
╒═══════╕
│Cnt    │
╞═══════╡
│"37388"│
└───────┘
```

# OR

## Count
```
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND  value.CVE_Items as cve
RETURN COUNT(cve);
```

## Limit data items
```
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND  value.CVE_Items as data
RETURN data limit 5;
```

# Create Nodes from JSON Files
```
CALL apoc.periodic.iterate("CALL apoc.load.json('file:///nvdcve-1.1-2024.json') YIELD value",
"UNWIND  value.CVE_Items AS data  \r\n"+
"UNWIND data.cve.references.reference_data AS references \r\n"+
"MERGE (cveItem:CVE {uid: apoc.create.uuid()}) \r\n"+
"ON CREATE SET cveItem.cveid = data.cve.CVE_data_meta.ID, cveItem.references = references.url",
 {batchSize:100, iterateList:true});
```