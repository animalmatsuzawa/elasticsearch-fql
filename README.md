Elasticsearc FastQueryLanguage compatible plugin
==
# fql
Fastの検索文をelasticsearchに置き換えて検索するplugin
検索結果もFastの検索結果に近い形でxmlで出力されます。

全ての機能をカバーしてはいません。
私に必要な機能のみを実装していますwww

Query and Result Protocol Specification
http://download.microsoft.com/download/3/4/C/34C47498-E69F-4CEF-9B58-5CEB77E1553D/[MS-FSQR].pdf
Fast Query Language Version 2 Protocol
http://interoperability.blob.core.windows.net/files/MS-FQL2/[MS-FQL2].pdf

# build
```
gradle assemble
```

# install
```
bin/elasticsearch-plugin install file:///path/to/elasticsearch-fql.zip
```

# 検索 

## request
```
GET _fast/{index}/{tyoe}/search?qyerty=and(field:"xxx",id:1)
```

## response
```
<SEGMENTS>
  <SEGMENT NAME="webcluster">
    <RESULTPAGE>
      <QUERYTRANSFORMS>
        <QUERYTRANSFORM NAME="Original query" ACTION="NOP" CUSTOM="" MESSAGE="Original query" MESSAGEID="1" QUERT="and(field:&quot;xxx&quot;,id:1)"/>
        <QUERYTRANSFORM NAME="FastQT Kansuji" ACTION="nop" CUSTOM="" MESSAGE="Query was not modified." MESSAGEID="2" INSTANCE="kansuji" QUERY="Trere were no CJK numerals in the query."/>
        <QUERYTRANSFORM NAME="FastQT_Lemmatizer" ACTION="nop" CUSTOM="No change to query" MESSAGE="Lemmatization turned off for current query" MESSAGEID="16" INSTANCE="lemmatizer" QUERY=""/>
        <QUERYTRANSFORM NAME="Final query" ACTION="NOP" CUSTOM="" MESSAGE="Final query" MESSAGEID="1" QUERY=""/>
      </QUERYTRANSFORMS>
      <NAVIGATION ENTRIES="0"/>
      <CLUSTERS/>
      <RESULTSET TOTALHITS="1" FIRSTHIT="1" HITS="1" LASTHIT="1" MAXRANK="0" TIME="342ms">
        <HIT NO="1" FCOCOUNT="0" MOREHITS="0" RANK="0" SITEID="0">
          <FIELD name="field">xxx</FIELD>
          <FIELD name="id">1</FIELD>
        </HIT>
      </RESULTSET>
      <PAGENAVIGATION/>
    </RESULTPAGE>
  </SEGMENT>
</SEGMENTS>
```

## Parameters
| Name                    | Description                              |
|:------------------------|:----------------------------------------:|
|query|検索文字列 FQLにて指定|
|sortby|ソートを条件を指定 単純なfield毎のASC/DESCの指定のみ可能|
|navigation|filter queryに置き換えます|
|offset|検索結果の開始位置 default 0|
|hits|検索数 default 10|

# NAVIGATION
FASTのNAVIGATION(Elasticsearchで言う所のAggregation)はスキーマの定義にて行います。
本実装では、Aggregstionの定義を保存し、検索実行時に自動で付与して結果を返します。
index/type毎に一つのNAVIGATIONを指定可能
情報はElasticsearch状の.fastインデックスに格納される。

## request
検索結果に付与するAggregationの条件を設定
```
PUT /_fast/test/type/_navigation
{ "aggs" : { "fields" : { "terms" : { "field" : "field" , "size":2} } } }
```

## response
```
{
  "_index" : ".fast",
  "_type" : "dGVzdA==",
  "_id" : "dHlwZQ==",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```

## 検索結果
### request
```
GET _fast/{index}/{tyoe}/search?qyerty=field:"xxx"
```
### response
結果に<NAVIGATION>が追加されている。
```
<SEGMENTS>
  <SEGMENT NAME="webcluster">
    <RESULTPAGE>
      <QUERYTRANSFORMS>
        <QUERYTRANSFORM NAME="Original query" ACTION="NOP" CUSTOM="" MESSAGE="Original query" MESSAGEID="1" QUERT="field:&quot;xxx&quot;"/>
        <QUERYTRANSFORM NAME="FastQT Kansuji" ACTION="nop" CUSTOM="" MESSAGE="Query was not modified." MESSAGEID="2" INSTANCE="kansuji" QUERY="Trere were no CJK numerals in the query."/>
        <QUERYTRANSFORM NAME="FastQT_Lemmatizer" ACTION="nop" CUSTOM="No change to query" MESSAGE="Lemmatization turned off for current query" MESSAGEID="16" INSTANCE="lemmatizer" QUERY=""/>
        <QUERYTRANSFORM NAME="Final query" ACTION="NOP" CUSTOM="" MESSAGE="Final query" MESSAGEID="1" QUERY=""/>
      </QUERYTRANSFORMS>
      <NAVIGATION ENTRIES="1">
        <NAVIGATIONENTRY NAME="fields" DISPAYNAME="fields" MODIFIER="fields">
          <NAVIGATIONELEMENTS COUNT="1">
            <NAVIGATIONELEMENT NAME="xxx" MODIFIER="^&quot;xxx&quot;$" COUNT="2"/>
          </NAVIGATIONELEMENTS>
        </NAVIGATIONENTRY>
      </NAVIGATION>
      <CLUSTERS/>
      <RESULTSET TOTALHITS="2" FIRSTHIT="1" HITS="2" LASTHIT="2" MAXRANK="0" TIME="275ms">
        <HIT NO="1" FCOCOUNT="0" MOREHITS="0" RANK="0" SITEID="0">
          <FIELD name="field">xxx</FIELD>
          <FIELD name="id">1</FIELD>
        </HIT>
        <HIT NO="2" FCOCOUNT="0" MOREHITS="0" RANK="0" SITEID="0">
          <FIELD name="field">xxx</FIELD>
          <FIELD name="id">3</FIELD>
        </HIT>
      </RESULTSET>
      <PAGENAVIGATION/>
    </RESULTPAGE>
  </SEGMENT>
</SEGMENTS>

```

