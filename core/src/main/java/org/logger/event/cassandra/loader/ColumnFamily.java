package org.logger.event.cassandra.loader;

public enum ColumnFamily {

	APIKEY("app_api_key"),
	
	EVENTDETAIL("event_detail"),
	
	EVENTTIMELINE("event_timeline"),
	
	ACTIVITYSTREAM("activity_stream"),
	
	DIMEVENTS("dim_events_list"),
	
	DIMDATE("dim_date"),
	
	DIMTIME("dim_time"),
	
	DIMUSER("dim_user"),
	
	EXTRACTEDUSER("extracted_user"),
	
	EXTRACTEDCODE("extracted_code"),
	
	DIMCONTENTCLASSIFICATION("dim_content_classification"),
	
	DIMRESOURCE("dim_resource"),
	
	TABLEDATATYPES("table_datatypes"),
	
	STAGING("staging_event_detail"),
	
	EVENTFIELDS("event_fields"),
	
	CONFIGSETTINGS("job_config_settings"),
	
	REALTIMECONFIG("real_time_operation_config"),
	
	RECENTVIEWEDRESOURCES("recent_viewed_resources"),
	
	LIVEDASHBOARD("live_dashboard"),
	
	MICROAGGREGATION("micro_aggregation"),
	
	ACITIVITYSTREAM("activity_stream"),
	
	REALTIMECOUNTER("real_time_counter"),
	
	REALTIMEAGGREGATOR("real_time_aggregator"),
	
	QUESTIONCOUNT("question_count"),
	
	COLLECTIONITEM("collection_item"),
	
	COLLECTION("collection"),
	
	CLASSPAGE("classpage"),
	
	LICENSE("license"),
	
	RESOURCETYPES("resource_type"),
	
	CATEGORY("category"),
	
	RESOURCE("resource"),
	
	TABLE_DATATYPE("table_datatypes"),
	
	USER("user"),
	
	RESOURCEFORMAT("resource_format"),
	
	INSTRUCTIONAL("instructional_use"),
	
	ANONYMIZEDUSERDATA("anonymized_user_data"),
	
	USER_EVENT_INDEX_QUEUE("user_event_index_queue")
	;
	
	String name;

	
	private ColumnFamily(String name) {
		this.name = name;
	}


	public String getColumnFamily(){
		return name;
	}

}
