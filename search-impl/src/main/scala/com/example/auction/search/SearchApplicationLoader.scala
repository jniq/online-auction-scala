package com.example.auction.search

import com.example.auction.bidding.api.BiddingService
import com.example.auction.item.api.ItemService
import com.example.auction.search.api.SearchService
import com.example.auction.search.impl.{ BrokerEventConsumer, SearchServiceImpl }
import com.example.elasticsearch.response.SearchResult
import com.example.elasticsearch.{ ElasticSearchIndexedStore, Elasticsearch }
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaClientComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.status.MetricsServiceComponents
import com.lightbend.lagom.scaladsl.server.{ LagomApplication, LagomApplicationContext, LagomApplicationLoader }
import com.softwaremill.macwire._
import com.lightbend.rp.servicediscovery.lagom.scaladsl.LagomServiceLocatorComponents
import play.api.libs.ws.ahc.AhcWSComponents

abstract class SearchApplication(context: LagomApplicationContext) extends LagomApplication(context)
  with AhcWSComponents
  with LagomKafkaClientComponents
  with MetricsServiceComponents {

  lazy val itemService = serviceClient.implement[ItemService]
  lazy val bidService = serviceClient.implement[BiddingService]

  lazy val elasticSearch = serviceClient.implement[Elasticsearch]

  lazy val indexedStore:IndexedStore[SearchResult] = wire[ElasticSearchIndexedStore]

  override lazy val lagomServer = serverFor[SearchService](wire[SearchServiceImpl])

  wire[BrokerEventConsumer]

}

class SearchApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext) =
    new SearchApplication(context) with LagomServiceLocatorComponents

  override def loadDevMode(context: LagomApplicationContext) =
    new SearchApplication(context) with LagomDevModeComponents
  
  override def describeService = Some(readDescriptor[SearchService])
}
