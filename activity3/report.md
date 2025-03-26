# Install

Install Apoc via neo4j Desktop by clicking on the database > Plugins > Apoc > Install.
Then restart your DBMS.

To avoid any errors, set `apoc.import.file.enabled=true` in the apoc.conf

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

# Steps

## Step 1
Import test_data.json file

```
CALL apoc.load.json("file:///test_data.json") YIELD value
```

Output:
```
{
  "name": "Person 1",
  "age": 25
}
{
  "name": "Person 2",
  "age": 30
}
{
  "name": "Person 3",
  "age": 35
}
{
  "name": "Person 4",
  "age": 40
}
```

## Step 2

Get all keys from the nvdcve file:
```
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND keys(value) AS key
RETURN key, apoc.meta.cypher.type(value[key]);
```

```
╒═══════════════════════╤═════════════════════════════════╕
│key                    │apoc.meta.cypher.type(value[key])│
╞═══════════════════════╪═════════════════════════════════╡
│"CVE_Items"            │"LIST OF MAP"                    │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_type"        │"STRING"                         │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_format"      │"STRING"                         │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_timestamp"   │"STRING"                         │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_numberOfCVEs"│"STRING"                         │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_version"     │"STRING"                         │
└───────────────────────┴─────────────────────────────────┘
```


## Step 3
Get the number of vulnerabilities:
```
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND  value.CVE_data_numberOfCVEs as Cnt
RETURN Cnt
```

Or,
```
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND  value.CVE_Items as cve
RETURN count (cve)
```

Both commands generate the following output: 
```
Cnt
"37388"
```


## Step 4
Return first 5 vulnerabilities:
```
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND  value.CVE_Items as data
RETURN data limit 5
```

Output is too long to show.

But, we can only look into their severity:
```
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND  value.CVE_Items as data
RETURN data.cve.CVE_data_meta.ID, data.impact.baseMetricV2.severity limit 5
```

Output:
```
╒═════════════════════════╤═════════════════════════════════╕
│data.cve.CVE_data_meta.ID│data.impact.baseMetricV2.severity│
╞═════════════════════════╪═════════════════════════════════╡
│"CVE-2024-0001"          │null                             │
├─────────────────────────┼─────────────────────────────────┤
│"CVE-2024-0002"          │null                             │
├─────────────────────────┼─────────────────────────────────┤
│"CVE-2024-0003"          │null                             │
├─────────────────────────┼─────────────────────────────────┤
│"CVE-2024-0004"          │null                             │
├─────────────────────────┼─────────────────────────────────┤
│"CVE-2024-0005"          │null                             │
└─────────────────────────┴─────────────────────────────────┘
```


## Step 5

### Import the data from this json as nodes into our db.

```
CALL apoc.periodic.iterate("CALL apoc.load.json('file:///nvdcve-1.1-2024.json') YIELD value",
"UNWIND  value.CVE_Items AS data  \r\n"+
"UNWIND data.cve.references.reference_data AS references \r\n"+
"MERGE (cveItem:CVE {uid: apoc.create.uuid()}) \r\n"+
"ON CREATE SET cveItem.cveid = data.cve.CVE_data_meta.ID, cveItem.references = references.url",
 {batchSize:100, iterateList:true});
```

Doesn't seem to work with the CVE from 2024.