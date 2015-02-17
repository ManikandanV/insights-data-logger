package org.logger.event.cassandra.loader.dao;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.ednovo.data.model.EventObject;
import org.ednovo.data.model.JSONDeserializer;
import org.ednovo.data.model.TypeConverter;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.logger.event.cassandra.loader.CassandraConnectionProvider;
import org.logger.event.cassandra.loader.ColumnFamily;
import org.logger.event.cassandra.loader.Constants;
import org.logger.event.cassandra.loader.DataUtils;
import org.logger.event.cassandra.loader.ESIndexices;
import org.logger.event.cassandra.loader.IndexType;
import org.logger.event.cassandra.loader.LoaderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import com.google.gson.Gson;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;

public class ELSIndexerImpl extends BaseDAOCassandraImpl implements ELSIndexer,Constants {

	private static final Logger logger = LoggerFactory.getLogger(ELSIndexerImpl.class);
	
	
	private CassandraConnectionProvider connectionProvider;
	 
	private BaseCassandraRepoImpl baseDao;

	Map<String,String> fieldDataTypes = null ;
	
	Map<String,String> beFieldName = null ;

	public static  Map<String,String> cache;
    
    public static  Map<String,Object> licenseCache;
    
    public static  Map<String,Object> resourceTypesCache;
    
    public static  Map<String,Object> categoryCache;
    
    public static  Map<String,Object> resourceFormatCache;
    
    public static  Map<String,Object> instructionalCache;
    
    public static  Map<String,String> taxonomyCodeType;
    
    public static  String REPOPATH;
    
	public ELSIndexerImpl(CassandraConnectionProvider connectionProvider) {
		super(connectionProvider);
	    this.connectionProvider = connectionProvider;
        this.baseDao = new BaseCassandraRepoImpl(this.connectionProvider);
        
        beFieldName =  new LinkedHashMap<String,String>();
        fieldDataTypes =  new LinkedHashMap<String,String>();
        Rows<String, String> fieldDescrption = baseDao.readAllRows(ColumnFamily.EVENTFIELDS.getColumnFamily(),0);
        for (Row<String, String> row : fieldDescrption) {
        	fieldDataTypes.put(row.getKey(), row.getColumns().getStringValue("description", null));
        	beFieldName.put(row.getKey(), row.getColumns().getStringValue("be_column", null));
		} 
        
        Rows<String, String> licenseRows = baseDao.readAllRows(ColumnFamily.LICENSE.getColumnFamily(),0);
        licenseCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : licenseRows) {
        	licenseCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        Rows<String, String> resourceTypesRows = baseDao.readAllRows(ColumnFamily.RESOURCETYPES.getColumnFamily(),0);
        resourceTypesCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : resourceTypesRows) {
        	resourceTypesCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        Rows<String, String> categoryRows = baseDao.readAllRows(ColumnFamily.CATEGORY.getColumnFamily(),0);
        categoryCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : categoryRows) {
        	categoryCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        
        Rows<String, String> resourceFormatRows = baseDao.readAllRows(ColumnFamily.RESOURCEFORMAT.getColumnFamily(),0);
        resourceFormatCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : resourceFormatRows) {
        	resourceFormatCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        
        Rows<String, String> instructionalRows = baseDao.readAllRows(ColumnFamily.INSTRUCTIONAL.getColumnFamily(),0);
        
        instructionalCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : instructionalRows) {
        	instructionalCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        
        taxonomyCodeType = new LinkedHashMap<String, String>();
        
        ColumnList<String> taxonomyCodeTypeList = baseDao.readWithKey(ColumnFamily.TABLEDATATYPES.getColumnFamily(), "taxonomy_code",0);
        for(int i = 0 ; i < taxonomyCodeTypeList.size() ; i++) {
        	taxonomyCodeType.put(taxonomyCodeTypeList.getColumnByIndex(i).getName(), taxonomyCodeTypeList.getColumnByIndex(i).getStringValue());
        }
        cache = new LinkedHashMap<String, String>();
        cache.put(INDEXINGVERSION, baseDao.readWithKeyColumn(ColumnFamily.CONFIGSETTINGS.getColumnFamily(), INDEXINGVERSION, DEFAULTCOLUMN,0).getStringValue());
        REPOPATH = baseDao.readWithKeyColumn(ColumnFamily.CONFIGSETTINGS.getColumnFamily(), "repo.path", DEFAULTCOLUMN,0).getStringValue();
	}

	public void clearCache(){
		beFieldName =  new LinkedHashMap<String,String>();
        fieldDataTypes =  new LinkedHashMap<String,String>();
        Rows<String, String> fieldDescrption = baseDao.readAllRows(ColumnFamily.EVENTFIELDS.getColumnFamily(),0);
        for (Row<String, String> row : fieldDescrption) {
        	fieldDataTypes.put(row.getKey(), row.getColumns().getStringValue("description", null));
        	beFieldName.put(row.getKey(), row.getColumns().getStringValue("be_column", null));
		} 
        
        Rows<String, String> licenseRows = baseDao.readAllRows(ColumnFamily.LICENSE.getColumnFamily(),0);
        licenseCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : licenseRows) {
        	licenseCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        Rows<String, String> resourceTypesRows = baseDao.readAllRows(ColumnFamily.RESOURCETYPES.getColumnFamily(),0);
        resourceTypesCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : resourceTypesRows) {
        	resourceTypesCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        Rows<String, String> categoryRows = baseDao.readAllRows(ColumnFamily.CATEGORY.getColumnFamily(),0);
        categoryCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : categoryRows) {
        	categoryCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        
        Rows<String, String> resourceFormatRows = baseDao.readAllRows(ColumnFamily.RESOURCEFORMAT.getColumnFamily(),0);
        resourceFormatCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : resourceFormatRows) {
        	resourceFormatCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        
        Rows<String, String> instructionalRows = baseDao.readAllRows(ColumnFamily.INSTRUCTIONAL.getColumnFamily(),0);
        
        instructionalCache = new LinkedHashMap<String, Object>();
        for (Row<String, String> row : instructionalRows) {
        	instructionalCache.put(row.getKey(), row.getColumns().getLongValue("id", null));
		}
        
        taxonomyCodeType = new LinkedHashMap<String, String>();
        
        ColumnList<String> taxonomyCodeTypeList = baseDao.readWithKey(ColumnFamily.TABLEDATATYPES.getColumnFamily(), "taxonomy_code",0);
        for(int i = 0 ; i < taxonomyCodeTypeList.size() ; i++) {
        	taxonomyCodeType.put(taxonomyCodeTypeList.getColumnByIndex(i).getName(), taxonomyCodeTypeList.getColumnByIndex(i).getStringValue());
        }
        cache = new LinkedHashMap<String, String>();
        cache.put(INDEXINGVERSION, baseDao.readWithKeyColumn(ColumnFamily.CONFIGSETTINGS.getColumnFamily(), INDEXINGVERSION, DEFAULTCOLUMN,0).getStringValue());
        REPOPATH = baseDao.readWithKeyColumn(ColumnFamily.CONFIGSETTINGS.getColumnFamily(), "repo.path", DEFAULTCOLUMN,0).getStringValue();
	}
	public void indexActivity(String fields){
    	if(fields != null){
				JSONObject jsonField = null;
				try {
					jsonField = new JSONObject(fields);
				} catch (JSONException e1) {
					try {
						jsonField = new JSONObject(fields.substring(14).trim());
					} catch (JSONException e2) {
						logger.info("field : " + fields);
						e2.printStackTrace();
					}
				}
	    			if(jsonField.has("version")){
	    				EventObject eventObjects = new Gson().fromJson(fields, EventObject.class);
	    				Map<String, Object> eventMap = new HashMap<String, Object>();
						try {
							eventMap = JSONDeserializer.deserializeEventObjectv2(eventObjects);
						} catch (JSONException e) {
							e.printStackTrace();
						}    	
	    				
	    				eventMap.put("eventName", eventObjects.getEventName());
	    		    	eventMap.put("eventId", eventObjects.getEventId());
	    		    	eventMap.put("eventTime",String.valueOf(eventObjects.getStartTime()));
	    		    	if(eventMap.get(CONTENTGOORUOID) != null){		    		    		
	    		    		eventMap =  this.getTaxonomyInfo(eventMap, String.valueOf(eventMap.get(CONTENTGOORUOID)));
	    		    		eventMap =  this.getContentInfo(eventMap, String.valueOf(eventMap.get(CONTENTGOORUOID)));
	    		    	}
	    		    	if(eventMap.get(GOORUID) != null){  
	    		    		eventMap =   this.getUserInfo(eventMap,String.valueOf(eventMap.get(GOORUID)));
	    		    	}
    				   if(String.valueOf(eventMap.get(CONTENTGOORUOID)) != null){
    						ColumnList<String> questionList = baseDao.readWithKey(ColumnFamily.QUESTIONCOUNT.getColumnFamily(), String.valueOf(eventMap.get(CONTENTGOORUOID)),0);
    				    	if(questionList != null && questionList.size() > 0){
    				    		eventMap.put("questionCount",questionList.getColumnByName("questionCount") != null ? questionList.getColumnByName("questionCount").getLongValue() : 0L);
    				    		eventMap.put("resourceCount",questionList.getColumnByName("resourceCount") != null ? questionList.getColumnByName("resourceCount").getLongValue() : 0L);
    				    		eventMap.put("oeCount",questionList.getColumnByName("oeCount") != null ? questionList.getColumnByName("oeCount").getLongValue() : 0L);
    				    		eventMap.put("mcCount",questionList.getColumnByName("mcCount") != null ? questionList.getColumnByName("mcCount").getLongValue() : 0L);
   
    				    		eventMap.put("fibCount",questionList.getColumnByName("fibCount") != null ? questionList.getColumnByName("fibCount").getLongValue() : 0L);
    				    		eventMap.put("maCount",questionList.getColumnByName("maCount") != null ? questionList.getColumnByName("maCount").getLongValue() : 0L);
    				    		eventMap.put("tfCount",questionList.getColumnByName("tfCount") != null ? questionList.getColumnByName("tfCount").getLongValue() : 0L);
   
    				    		eventMap.put("itemCount",questionList.getColumnByName("itemCount") != null ? questionList.getColumnByName("itemCount").getLongValue() : 0L );
    				    	}
    					}
	    	    		this.saveInESIndex(eventMap,ESIndexices.EVENTLOGGERINFO.getIndex()+"_"+cache.get(INDEXINGVERSION), IndexType.EVENTDETAIL.getIndexType(), String.valueOf(eventMap.get("eventId")));
	    	    		if(eventMap.get(EVENTNAME).toString().matches(INDEXEVENTS)){
	    	    			indexResource(eventMap.get(CONTENTGOORUOID).toString());
	    	    			if(eventMap.containsKey(SOURCEGOORUOID)){
	    	    				  	indexResource(eventMap.get(SOURCEGOORUOID).toString());
	    	    			}
	    	    			try {
	    	    				if(!eventMap.get(GOORUID).toString().equalsIgnoreCase("ANONYMOUS")){
	    	    					getUserAndIndex(eventMap.get(GOORUID).toString());
	    	    				}
							} catch (Exception e) {
								e.printStackTrace();
							}
	    	    		}
	    			} 
	    			else{
	    				try{
	    				   Iterator<?> keys = jsonField.keys();
	    				   Map<String,Object> eventMap = new HashMap<String, Object>();
	    				   while( keys.hasNext() ){
	    			            String key = (String)keys.next();
	    			            
	    			            eventMap.put(key,String.valueOf(jsonField.get(key)));
	    			            
	    			         /*   if(key.equalsIgnoreCase("contentGooruId") || key.equalsIgnoreCase("gooruOId") || key.equalsIgnoreCase("gooruOid")){
	    			            	eventMap.put("gooruOid", String.valueOf(jsonField.get(key)));
	    			            }*/
	
	    			            if(key.equalsIgnoreCase("eventName") && (String.valueOf(jsonField.get(key)).equalsIgnoreCase("create-reaction"))){
	    			            	eventMap.put("eventName", "reaction.create");
	    			            }
	    			            
	    			            if(key.equalsIgnoreCase("eventName") 
	    			            		&& (String.valueOf(jsonField.get(key)).equalsIgnoreCase("collection-play") 
	    			            				|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("collection-play-dots")
	    			            					|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("collections-played")
	    			            						|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("quiz-play"))){
	    			            	
	    			            	eventMap.put("eventName", "collection.play");
	    			            }
	    			            
	    			            if(key.equalsIgnoreCase("eventName") 
	    			            		&& (String.valueOf(jsonField.get(key)).equalsIgnoreCase("signIn-google-login") 
	    			            				|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("signIn-google-home")
	    			            					|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("anonymous-login"))){
	    			            	eventMap.put("eventName", "user.login");
	    			            }
	    			            
	    			            if(key.equalsIgnoreCase("eventName") 
	    			            		&& (String.valueOf(jsonField.get(key)).equalsIgnoreCase("signUp-home") 
	    			            					|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("signUp-login"))){
	    			            	eventMap.put("eventName", "user.register");
	    			            }
	    			            
	    			            if(key.equalsIgnoreCase("eventName") 
	    			            		&& (String.valueOf(jsonField.get(key)).equalsIgnoreCase("collection-resource-play") 
	    			            				|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("collection-resource-player")
	    			            					|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("collection-resource-play-dots")
	    			            						|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("collection-question-resource-play-dots")
	    			            							|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("collection-resource-oe-play-dots")
	    			            								|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("collection-resource-question-play-dots"))){
	    			            	eventMap.put("eventName", "collection.resource.play");
	    			            }
	    			            
	    			            if(key.equalsIgnoreCase("eventName") 
	    			            		&& (String.valueOf(jsonField.get(key)).equalsIgnoreCase("resource-player") 
	    			            				|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("resource-play-dots")
	    			            					|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("resourceplayerstart")
	    			            						|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("resourceplayerplay")
	    			            							|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("resources-played")
	    			            								|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("question-oe-play-dots")
	    			            									|| String.valueOf(jsonField.get(key)).equalsIgnoreCase("question-play-dots"))){
	    			            	eventMap.put("eventName", "resource.play");
	    			            }
	    			            
	    			            if(key.equalsIgnoreCase("gooruUId") || key.equalsIgnoreCase("gooruUid")){
	    			            	eventMap.put(GOORUID, String.valueOf(jsonField.get(key)));
	    			            }
	    			            
	    			        }
	    				   if(eventMap.get(CONTENTGOORUOID) != null){
	    				   		eventMap =  this.getTaxonomyInfo(eventMap, String.valueOf(eventMap.get(CONTENTGOORUOID)));
	    				   		eventMap =  this.getContentInfo(eventMap, String.valueOf(eventMap.get(CONTENTGOORUOID)));
	    				   }
	    				   if(eventMap.get(GOORUID) != null ){
	    					   eventMap =   this.getUserInfo(eventMap,String.valueOf(eventMap.get(GOORUID)));
	    				   }	    	    	
	    				   
	    				   if(eventMap.get(EVENTNAME).equals(LoaderConstants.CPV1.getName()) && eventMap.get(CONTENTGOORUOID) != null){
	    						ColumnList<String> questionList = baseDao.readWithKey(ColumnFamily.QUESTIONCOUNT.getColumnFamily(), String.valueOf(eventMap.get(CONTENTGOORUOID)),0);
	    				    	if(questionList != null && questionList.size() > 0){
	    				    		eventMap.put("questionCount",questionList.getColumnByName("questionCount") != null ? questionList.getColumnByName("questionCount").getLongValue() : 0L);
	    				    		eventMap.put("resourceCount",questionList.getColumnByName("resourceCount") != null ? questionList.getColumnByName("resourceCount").getLongValue() : 0L);
	    				    		eventMap.put("oeCount",questionList.getColumnByName("oeCount") != null ? questionList.getColumnByName("oeCount").getLongValue() : 0L);
	    				    		eventMap.put("mcCount",questionList.getColumnByName("mcCount") != null ? questionList.getColumnByName("mcCount").getLongValue() : 0L);
	    				    		
	    				    		eventMap.put("fibCount",questionList.getColumnByName("fibCount") != null ? questionList.getColumnByName("fibCount").getLongValue() : 0L);
	    				    		eventMap.put("maCount",questionList.getColumnByName("maCount") != null ? questionList.getColumnByName("maCount").getLongValue() : 0L);
	    				    		eventMap.put("tfCount",questionList.getColumnByName("tfCount") != null ? questionList.getColumnByName("tfCount").getLongValue() : 0L);
	    				    		
	    				    		eventMap.put("itemCount",questionList.getColumnByName("itemCount") != null ? questionList.getColumnByName("itemCount").getLongValue() : 0L );
	    				    	}
	    					}
		    	    		this.saveInESIndex(eventMap,ESIndexices.EVENTLOGGERINFO.getIndex()+"_"+cache.get(INDEXINGVERSION), IndexType.EVENTDETAIL.getIndexType(), String.valueOf(eventMap.get("eventId")));
		    	    		if(eventMap.get(EVENTNAME).toString().matches(INDEXEVENTS)){
		    	    			indexResource(eventMap.get(CONTENTGOORUOID).toString());
		    	    		if(eventMap.containsKey(SOURCEGOORUOID)){
	    	    				  	indexResource(eventMap.get(SOURCEGOORUOID).toString());
	    	    			}
		    	    			if(!eventMap.get(GOORUID).toString().equalsIgnoreCase("ANONYMOUS")){
		    	    				getUserAndIndex(eventMap.get(GOORUID).toString());
		    	    			}
		    	    		}
	    				}catch(Exception e3){
	    					e3.printStackTrace();
	    				}
	    		     }
				
				}
		
    }
	
	public void indexResource(String ids){
    	Collection<String> idListN = new ArrayList<String>();
    	for(String id : ids.split(",")){
    //		idList.add("GLP~" + id);
    		idListN.add(id);
    	}
    	logger.info("Indexing resources : {}",idListN);
    	Rows<String,String> resourceN = baseDao.readWithKeyList(ColumnFamily.RESOURCE.getColumnFamily(), idListN,0);
    	try {
    		if(resourceN != null && resourceN.size() > 0){
    			this.getResourceAndIndexN(resourceN);
    		}else {
    			throw new AccessDeniedException("Invalid Id!!");
    		}
		} catch (Exception e) {
			logger.info("indexing failed .. :{}",e);
		}
    }
    

    public Map<String, Object> getUserInfo(Map<String,Object> eventMap , String gooruUId){
    	Collection<String> user = new ArrayList<String>();
    	user.add(gooruUId);
    	ColumnList<String> eventDetailsNew = baseDao.readWithKey(ColumnFamily.EXTRACTEDUSER.getColumnFamily(), gooruUId,0);
    	//for (Row<String, String> row : eventDetailsNew) {
    		//ColumnList<String> userInfo = row.getColumns();
    	if(eventDetailsNew != null && eventDetailsNew.size() > 0){
    		for(int i = 0 ; i < eventDetailsNew.size() ; i++) {
    			String columnName = eventDetailsNew.getColumnByIndex(i).getName();
    			String value = eventDetailsNew.getColumnByIndex(i).getStringValue();
    			if(value != null){
    				eventMap.put(columnName, value);
    			}
    		}
    		}
    	//}
		return eventMap;
    }
    public Map<String,Object> getContentInfo(Map<String,Object> eventMap,String gooruOId){
    	
    	Set<String> contentItems = baseDao.getAllLevelParents(ColumnFamily.COLLECTIONITEM.getColumnFamily(),gooruOId, 0);
    	if(!contentItems.isEmpty()){
    		eventMap.put("contentItems",contentItems);
    	}
    	ColumnList<String> resource = baseDao.readWithKey(ColumnFamily.DIMRESOURCE.getColumnFamily(), "GLP~"+gooruOId,0);
    		if(resource != null){
    			eventMap.put("title", resource.getStringValue("title", null));
    			eventMap.put("description",resource.getStringValue("description", null));
    			eventMap.put("sharing", resource.getStringValue("sharing", null));
    			eventMap.put("category", resource.getStringValue("category", null));
    			eventMap.put("typeName", resource.getStringValue("type_name", null));
    			eventMap.put("license", resource.getStringValue("license_name", null));
    			eventMap.put("contentOrganizationId", resource.getStringValue("organization_uid", null));
    			
    			if(resource.getColumnByName("instructional_id") != null){
    				eventMap.put("instructionalId", resource.getColumnByName("instructional_id").getLongValue());
    				}
    			if(resource.getColumnByName("resource_format_id") != null){
    				eventMap.put("resourceFormatId", resource.getColumnByName("resource_format_id").getLongValue());
    			}
    				
    			if(resource.getColumnByName("type_name") != null){
					if(resourceTypesCache.containsKey(resource.getColumnByName("type_name").getStringValue())){    							
						eventMap.put("resourceTypeId", resourceTypesCache.get(resource.getColumnByName("type_name").getStringValue()));
					}
				}
				if(resource.getColumnByName("category") != null){
					if(categoryCache.containsKey(resource.getColumnByName("category").getStringValue())){    							
						eventMap.put("resourceCategoryId", categoryCache.get(resource.getColumnByName("category").getStringValue()));
					}
				}
				ColumnList<String> questionCount = baseDao.readWithKey(ColumnFamily.QUESTIONCOUNT.getColumnFamily(), gooruOId,0);
				if(questionCount != null && !questionCount.isEmpty()){
					long questionCounts = questionCount.getLongValue("questionCount", 0L);
					eventMap.put("questionCount", questionCounts);
					if(questionCounts > 0L){
						if(resourceTypesCache.containsKey(resource.getColumnByName("type_name").getStringValue())){    							
							eventMap.put("resourceTypeId", resourceTypesCache.get(resource.getColumnByName("type_name").getStringValue()));
						}	
					}
				}else{
					eventMap.put("questionCount",0L);
				}
    		} 
    	
		return eventMap;
    }
    
    public Map<String,Object> getTaxonomyInfo(Map<String,Object> eventMap,String gooruOid){
    	Collection<String> user = new ArrayList<String>();
    	user.add(gooruOid);
    	Map<String,String> whereColumn = new HashMap<String, String>();
    	whereColumn.put("gooru_oid", gooruOid);
    	Rows<String, String> eventDetailsNew = baseDao.readIndexedColumnList(ColumnFamily.DIMCONTENTCLASSIFICATION.getColumnFamily(), whereColumn,0);
    	Set<Long> subjectCode = new HashSet<Long>();
    	Set<Long> courseCode = new HashSet<Long>();
    	Set<Long> unitCode = new HashSet<Long>();
    	Set<Long> topicCode = new HashSet<Long>();
    	Set<Long> lessonCode = new HashSet<Long>();
    	Set<Long> conceptCode = new HashSet<Long>();
    	Set<Long> taxArray = new HashSet<Long>();

    	for (Row<String, String> row : eventDetailsNew) {
    		ColumnList<String> userInfo = row.getColumns();
    			long root = userInfo.getColumnByName("root_node_id") != null ? userInfo.getColumnByName("root_node_id").getLongValue() : 0L;
    			if(root == 20000L){
	    			long value = userInfo.getColumnByName("code_id") != null ?userInfo.getColumnByName("code_id").getLongValue() : 0L;
	    			long depth = userInfo.getColumnByName("depth") != null ?  userInfo.getColumnByName("depth").getLongValue() : 0L;
	    			if(value != 0L &&  depth == 1L){    				
	    				subjectCode.add(value);
	    			} 
	    			else if(depth == 2L){
	    			ColumnList<String> columns = baseDao.readWithKey(ColumnFamily.EXTRACTEDCODE.getColumnFamily(), String.valueOf(value),0);
	    			long subject = columns.getColumnByName("subject_code_id") != null ? columns.getColumnByName("subject_code_id").getLongValue() : 0L;
	    			if(subject != 0L)
	    				subjectCode.add(subject);
	    			if(value != 0L)
	    				courseCode.add(value);
	    			}
	    			
	    			else if(depth == 3L){
	    				ColumnList<String> columns = baseDao.readWithKey(ColumnFamily.EXTRACTEDCODE.getColumnFamily(), String.valueOf(value),0);
		    			long subject = columns.getColumnByName("subject_code_id") != null ? columns.getColumnByName("subject_code_id").getLongValue() : 0L;
		    			long course = columns.getColumnByName("course_code_id") != null ? columns.getColumnByName("course_code_id").getLongValue() : 0L;
		    			if(subject != 0L)
		    			subjectCode.add(subject);
		    			if(course != 0L)
	    				courseCode.add(course);
		    			if(value != 0L)
	    				unitCode.add(value);
	    			}
	    			else if(depth == 4L){
	    				ColumnList<String> columns = baseDao.readWithKey(ColumnFamily.EXTRACTEDCODE.getColumnFamily(), String.valueOf(value),0);
		    			long subject = columns.getColumnByName("subject_code_id") != null ? columns.getColumnByName("subject_code_id").getLongValue() : 0L;
		    			long course = columns.getColumnByName("course_code_id") != null ? columns.getColumnByName("course_code_id").getLongValue() : 0L;
		    			long unit = columns.getColumnByName("unit_code_id") != null ? columns.getColumnByName("unit_code_id").getLongValue() : 0L;
		    				if(subject != 0L)
			    			subjectCode.add(subject);	
		    				if(course != 0L)
		    				courseCode.add(course);
		    				if(unit != 0L)
		    				unitCode.add(unit);
		    				if(value != 0L)
		    				topicCode.add(value);
	    			}
	    			else if(depth == 5L){
	    				ColumnList<String> columns = baseDao.readWithKey(ColumnFamily.EXTRACTEDCODE.getColumnFamily(), String.valueOf(value),0);
		    			long subject = columns.getColumnByName("subject_code_id") != null ? columns.getColumnByName("subject_code_id").getLongValue() : 0L;
		    			long course = columns.getColumnByName("course_code_id") != null ? columns.getColumnByName("course_code_id").getLongValue() : 0L;
		    			long unit = columns.getColumnByName("unit_code_id") != null ? columns.getColumnByName("unit_code_id").getLongValue() : 0L;
		    			long topic = columns.getColumnByName("topic_code_id") != null ? columns.getColumnByName("topic_code_id").getLongValue() : 0L;
		    				if(subject != 0L)
			    			subjectCode.add(subject);
			    			if(course != 0L)
		    				courseCode.add(course);
		    				if(unit != 0L)
		    				unitCode.add(unit);
		    				if(topic != 0L)
		    				topicCode.add(topic);
		    				if(value != 0L)
		    				lessonCode.add(value);
	    			}
	    			else if(depth == 6L){
	    				ColumnList<String> columns = baseDao.readWithKey(ColumnFamily.EXTRACTEDCODE.getColumnFamily(), String.valueOf(value),0);
		    			long subject = columns.getColumnByName("subject_code_id") != null ? columns.getColumnByName("subject_code_id").getLongValue() : 0L;
		    			long course = columns.getColumnByName("course_code_id") != null ? columns.getColumnByName("course_code_id").getLongValue() : 0L;
		    			long unit = columns.getColumnByName("unit_code_id") != null ? columns.getColumnByName("unit_code_id").getLongValue() : 0L;
		    			long topic = columns.getColumnByName("topic_code_id") != null ? columns.getColumnByName("topic_code_id").getLongValue() : 0L;
		    			long lesson = columns.getColumnByName("lesson_code_id") != null ? columns.getColumnByName("lesson_code_id").getLongValue() : 0L;
		    			if(subject != 0L)
		    			subjectCode.add(subject);
		    			if(course != 0L)
	    				courseCode.add(course);
	    				if(unit != 0L && unit != 0)
	    				unitCode.add(unit);
	    				if(topic != 0L)
	    				topicCode.add(topic);
	    				if(lesson != 0L)
	    				lessonCode.add(lesson);
	    				if(value != 0L)
	    				conceptCode.add(value);
	    			}
	    			else if(value != 0L){
	    				taxArray.add(value);
	    				
	    			}
    		}else{
    			long value = userInfo.getColumnByName("code_id") != null ?userInfo.getColumnByName("code_id").getLongValue() : 0L;
    			if(value != 0L){
    				taxArray.add(value);
    			}
    		}
    	}
    		if(subjectCode != null && !subjectCode.isEmpty())
    		eventMap.put("subject", subjectCode);
    		if(courseCode != null && !courseCode.isEmpty())
    		eventMap.put("course", courseCode);
    		if(unitCode != null && !unitCode.isEmpty())
    		eventMap.put("unit", unitCode);
    		if(topicCode != null && !topicCode.isEmpty())
    		eventMap.put("topic", topicCode);
    		if(lessonCode != null && !lessonCode.isEmpty())
    		eventMap.put("lesson", lessonCode);
    		if(conceptCode != null && !conceptCode.isEmpty())
    		eventMap.put("concept", conceptCode);
    		if(taxArray != null && !taxArray.isEmpty())
    		eventMap.put("standards", taxArray);
    	
    	return eventMap;
    }
    
    public void getResourceAndIndexN(Rows<String, String> resource) throws ParseException{
   
		Map<String,Object> resourceMap = new LinkedHashMap<String, Object>();

		for(int a = 0 ; a < resource.size(); a++){
			
		ColumnList<String> columns = resource.getRowByIndex(a).getColumns();

		String gooruOid = resource.getRowByIndex(a).getKey();

		if(columns.getColumnByName("title") != null){
			resourceMap.put("title", columns.getColumnByName("title").getStringValue());
		}
		if(columns.getColumnByName("description") != null){
			resourceMap.put("description", columns.getColumnByName("description").getStringValue());
		}
		if(columns.getColumnByName("lastModified") != null){
			resourceMap.put("lastModified", columns.getColumnByName("lastModified").getDateValue());
		}
		if(columns.getColumnByName("createdOn") != null){
			resourceMap.put("createdOn", columns.getColumnByName("createdOn").getDateValue());
		}
		if(columns.getColumnByName("creator.userUid") != null){
			resourceMap.put("creatorUid", columns.getColumnByName("creator.userUid").getStringValue());
		}
		if(columns.getColumnByName("owner.userUid") != null){
			resourceMap.put("userUid", columns.getColumnByName("owner.userUid").getStringValue());
		}
		if(columns.getColumnByName("recordSource") != null){
			resourceMap.put("recordSource", columns.getColumnByName("recordSource").getStringValue());
		}
		if(columns.getColumnByName("sharing") != null){
			resourceMap.put("sharing", columns.getColumnByName("sharing").getStringValue());
		}
		if(columns.getColumnByName("organization.partyUid") != null){
			resourceMap.put("contentOrganizationId", columns.getColumnByName("organization.partyUid").getStringValue());
		}
		if(columns.getColumnByName("thumbnail") != null && StringUtils.isNotBlank(columns.getColumnByName("thumbnail").getStringValue())){
			if(columns.getColumnByName("thumbnail").getStringValue().startsWith("http") || columns.getColumnByName("thumbnail").getStringValue().startsWith("https")){				
				resourceMap.put("thumbnail", columns.getColumnByName("thumbnail").getStringValue());
			}else{
				resourceMap.put("thumbnail", REPOPATH+"/"+columns.getColumnByName("folder").getStringValue()+"/"+columns.getColumnByName("thumbnail").getStringValue());
			}
		}
		if(columns.getColumnByName("grade") != null){
			Set<String> gradeArray = new HashSet<String>(); 
			for(String gradeId : columns.getColumnByName("grade").getStringValue().split(",")){
				gradeArray.add(gradeId);	
			}
			if(gradeArray != null && gradeArray.isEmpty() ){
				resourceMap.put("grade", gradeArray);
			}
		}
		if(columns.getColumnByName("license.name") != null){
			if(licenseCache.containsKey(columns.getColumnByName("license.name").getStringValue())){    							
				resourceMap.put("licenseId", licenseCache.get(columns.getColumnByName("license.name").getStringValue()));
			}
		}
		if(columns.getColumnByName("resourceType") != null){
			if(resourceTypesCache.containsKey(columns.getColumnByName("resourceType").getStringValue())){    							
				resourceMap.put("resourceTypeId", resourceTypesCache.get(columns.getColumnByName("resourceType").getStringValue()));
			}
		}
		if(columns.getColumnByName("category") != null){
			if(categoryCache.containsKey(columns.getColumnByName("category").getStringValue())){    							
				resourceMap.put("resourceCategoryId", categoryCache.get(columns.getColumnByName("category").getStringValue()));
			}
		}
		if(columns.getColumnByName("category") != null){
			resourceMap.put("category", columns.getColumnByName("category").getStringValue());
		}
		if(columns.getColumnByName("resourceType") != null){
			resourceMap.put("typeName", columns.getColumnByName("resourceType").getStringValue());
		}
		if(columns.getColumnByName("resourceFormat") != null){
			resourceMap.put("resourceFormat", columns.getColumnByName("resourceFormat").getStringValue());
			resourceMap.put("resourceFormatId", resourceFormatCache.get(columns.getColumnByName("resourceFormat").getStringValue()));
		}
		if(columns.getColumnByName("instructional") != null){
			resourceMap.put("instructional", columns.getColumnByName("instructional").getStringValue());
			resourceMap.put("instructionalId", instructionalCache.get(columns.getColumnByName("instructional").getStringValue()));
		}
		if(gooruOid != null){
			Set<String> contentItems = baseDao.getAllLevelParents(ColumnFamily.COLLECTIONITEM.getColumnFamily(),gooruOid, 0);
			if(!contentItems.isEmpty()){
				resourceMap.put("contentItems",contentItems);
			}
			resourceMap.put("gooruOid", gooruOid);	 
			
			ColumnList<String> questionList = baseDao.readWithKey(ColumnFamily.QUESTIONCOUNT.getColumnFamily(), gooruOid,0);

			this.getLiveCounterData("all~"+gooruOid, resourceMap);
	    	
	    	if(questionList != null && questionList.size() > 0){
	    		resourceMap.put("questionCount",questionList.getColumnByName("questionCount") != null ? questionList.getColumnByName("questionCount").getLongValue() : 0L);
	    		resourceMap.put("resourceCount",questionList.getColumnByName("resourceCount") != null ? questionList.getColumnByName("resourceCount").getLongValue() : 0L);
	    		resourceMap.put("oeCount",questionList.getColumnByName("oeCount") != null ? questionList.getColumnByName("oeCount").getLongValue() : 0L);
	    		resourceMap.put("mcCount",questionList.getColumnByName("mcCount") != null ? questionList.getColumnByName("mcCount").getLongValue() : 0L);
	    		
	    		resourceMap.put("fibCount",questionList.getColumnByName("fibCount") != null ? questionList.getColumnByName("fibCount").getLongValue() : 0L);
	    		resourceMap.put("maCount",questionList.getColumnByName("maCount") != null ? questionList.getColumnByName("maCount").getLongValue() : 0L);
	    		resourceMap.put("tfCount",questionList.getColumnByName("tfCount") != null ? questionList.getColumnByName("tfCount").getLongValue() : 0L);
	    		
	    		resourceMap.put("itemCount",questionList.getColumnByName("itemCount") != null ? questionList.getColumnByName("itemCount").getLongValue() : 0L );
	    	}
		}
		if(columns.getColumnByName("owner.userUid") != null){
			resourceMap = this.getUserInfo(resourceMap, columns.getColumnByName("owner.userUid").getStringValue());
		}
		if(gooruOid != null){
			resourceMap = this.getTaxonomyInfo(resourceMap, gooruOid);
			this.saveInESIndex(resourceMap, ESIndexices.CONTENTCATALOGINFO.getIndex()+"_"+cache.get(INDEXINGVERSION), IndexType.DIMRESOURCE.getIndexType(), gooruOid);
		}
		}
    }
    
    public void getResourceAndIndex(Rows<String, String> resource) throws ParseException{
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss+0000");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss.000");
		
		Map<String,Object> resourceMap = new LinkedHashMap<String, Object>();
		
		for(int a = 0 ; a < resource.size(); a++){
			
		ColumnList<String> columns = resource.getRowByIndex(a).getColumns();
		
		if(columns == null){
			return;
		}
		if(columns.getColumnByName("gooru_oid") != null){
			Set<String> contentItems = baseDao.getAllLevelParents(ColumnFamily.COLLECTIONITEM.getColumnFamily(),columns.getColumnByName("gooru_oid").getStringValue(), 0);
			if(!contentItems.isEmpty()){
				resourceMap.put("contentItems",contentItems);
			}
	    	
		}
		if(columns.getColumnByName("title") != null){
			resourceMap.put("title", columns.getColumnByName("title").getStringValue());
		}
		if(columns.getColumnByName("description") != null){
			resourceMap.put("description", columns.getColumnByName("description").getStringValue());
		}
		if(columns.getColumnByName("gooru_oid") != null){
			resourceMap.put("gooruOid", columns.getColumnByName("gooru_oid").getStringValue());
		}
		if(columns.getColumnByName("last_modified") != null){
		try{
			resourceMap.put("lastModified", formatter.parse(columns.getColumnByName("last_modified").getStringValue()));
		}catch(Exception e){
			try{
				resourceMap.put("lastModified", formatter2.parse(columns.getColumnByName("last_modified").getStringValue()));
			}catch(Exception e2){
				resourceMap.put("lastModified", formatter3.parse(columns.getColumnByName("last_modified").getStringValue()));
			}
		}
		}
		if(columns.getColumnByName("created_on") != null){
		try{
			resourceMap.put("createdOn", columns.getColumnByName("created_on") != null  ? formatter.parse(columns.getColumnByName("created_on").getStringValue()) : formatter.parse(columns.getColumnByName("last_modified").getStringValue()));
		}catch(Exception e){
			try{
				resourceMap.put("createdOn", columns.getColumnByName("created_on") != null  ? formatter2.parse(columns.getColumnByName("created_on").getStringValue()) : formatter2.parse(columns.getColumnByName("last_modified").getStringValue()));
			}catch(Exception e2){
				resourceMap.put("createdOn", columns.getColumnByName("created_on") != null  ? formatter3.parse(columns.getColumnByName("created_on").getStringValue()) : formatter3.parse(columns.getColumnByName("last_modified").getStringValue()));
			}
		}
		}
		if(columns.getColumnByName("creator_uid") != null){
			resourceMap.put("creatorUid", columns.getColumnByName("creator_uid").getStringValue());
		}
		if(columns.getColumnByName("user_uid") != null){
			resourceMap.put("userUid", columns.getColumnByName("user_uid").getStringValue());
		}
		if(columns.getColumnByName("record_source") != null){
			resourceMap.put("recordSource", columns.getColumnByName("record_source").getStringValue());
		}
		if(columns.getColumnByName("sharing") != null){
			resourceMap.put("sharing", columns.getColumnByName("sharing").getStringValue());
		}
		/*if(columns.getColumnByName("views_count") != null){
			resourceMap.put("viewsCount", columns.getColumnByName("views_count").getLongValue());
		}*/
		if(columns.getColumnByName("organization_uid") != null){
			resourceMap.put("contentOrganizationId", columns.getColumnByName("organization_uid").getStringValue());
		}
		if(columns.getColumnByName("thumbnail") != null){
			resourceMap.put("thumbnail", columns.getColumnByName("thumbnail").getStringValue());
		}
		if(columns.getColumnByName("instructional_id") != null){
			resourceMap.put("instructionalId", columns.getColumnByName("instructional_id").getLongValue());
		}
		if(columns.getColumnByName("resource_format_id") != null && StringUtils.isNotBlank(columns.getStringValue("resource_format_id", null))){
			resourceMap.put("resourceFormatId", columns.getColumnByName("resource_format_id").getLongValue());
		} else if(columns.getColumnByName("resource_format_id") == null && StringUtils.isNotBlank(columns.getStringValue("type_name", null))) {
			String typeName = columns.getColumnByName("type_name").getStringValue();
			resourceMap.put("resourceFormatId", DataUtils.getResourceFormatId(typeName));
		}
		if(columns.getColumnByName("grade") != null){
			Set<String> gradeArray = new HashSet<String>(); 
			for(String gradeId : columns.getColumnByName("grade").getStringValue().split(",")){
				gradeArray.add(gradeId);	
			}
			if(gradeArray != null && gradeArray.isEmpty() ){
				resourceMap.put("grade", gradeArray);
			}
		}
		if(columns.getColumnByName("license_name") != null){
			//ColumnList<String> license = baseDao.readWithKey(ColumnFamily.LICENSE.getColumnFamily(), columns.getColumnByName("license_name").getStringValue());
			if(licenseCache.containsKey(columns.getColumnByName("license_name").getStringValue())){    							
				resourceMap.put("licenseId", licenseCache.get(columns.getColumnByName("license_name").getStringValue()));
			}
		}
		if(columns.getColumnByName("type_name") != null){
			//ColumnList<String> resourceType = baseDao.readWithKey(ColumnFamily.RESOURCETYPES.getColumnFamily(), columns.getColumnByName("type_name").getStringValue());
			if(resourceTypesCache.containsKey(columns.getColumnByName("type_name").getStringValue())){    							
				resourceMap.put("resourceTypeId", resourceTypesCache.get(columns.getColumnByName("type_name").getStringValue()));
			}
		}
		if(columns.getColumnByName("category") != null){
			//ColumnList<String> resourceType = baseDao.readWithKey(ColumnFamily.CATEGORY.getColumnFamily(), columns.getColumnByName("category").getStringValue());
			if(categoryCache.containsKey(columns.getColumnByName("category").getStringValue())){    							
				resourceMap.put("resourceCategoryId", categoryCache.get(columns.getColumnByName("category").getStringValue()));
			}
		}
		if(columns.getColumnByName("category") != null){
			resourceMap.put("category", columns.getColumnByName("category").getStringValue());
		}
		if(columns.getColumnByName("type_name") != null){
			resourceMap.put("typeName", columns.getColumnByName("type_name").getStringValue());
		}
		if(columns.getColumnByName("resource_format") != null){
			resourceMap.put("resourceFormat", columns.getColumnByName("resource_format").getStringValue());
		}
		if(columns.getColumnByName("instructional") != null){
			resourceMap.put("instructional", columns.getColumnByName("instructional").getStringValue());
		}
		if(columns.getColumnByName("gooru_oid") != null){
			ColumnList<String> questionList = baseDao.readWithKey(ColumnFamily.QUESTIONCOUNT.getColumnFamily(), columns.getColumnByName("gooru_oid").getStringValue(),0);

			this.getLiveCounterData("all~"+columns.getColumnByName("gooru_oid").getStringValue(), resourceMap);
	    	
	    	if(questionList != null && questionList.size() > 0){
	    		resourceMap.put("questionCount",questionList.getColumnByName("questionCount") != null ? questionList.getColumnByName("questionCount").getLongValue() : 0L);
	    		resourceMap.put("resourceCount",questionList.getColumnByName("resourceCount") != null ? questionList.getColumnByName("resourceCount").getLongValue() : 0L);
	    		resourceMap.put("oeCount",questionList.getColumnByName("oeCount") != null ? questionList.getColumnByName("oeCount").getLongValue() : 0L);
	    		resourceMap.put("mcCount",questionList.getColumnByName("mcCount") != null ? questionList.getColumnByName("mcCount").getLongValue() : 0L);
	    		
	    		resourceMap.put("fibCount",questionList.getColumnByName("fibCount") != null ? questionList.getColumnByName("fibCount").getLongValue() : 0L);
	    		resourceMap.put("maCount",questionList.getColumnByName("maCount") != null ? questionList.getColumnByName("maCount").getLongValue() : 0L);
	    		resourceMap.put("tfCount",questionList.getColumnByName("tfCount") != null ? questionList.getColumnByName("tfCount").getLongValue() : 0L);
	    		
	    		resourceMap.put("itemCount",questionList.getColumnByName("itemCount") != null ? questionList.getColumnByName("itemCount").getLongValue() : 0L );
	    	}
		}
		if(columns.getColumnByName("user_uid") != null){
			resourceMap = this.getUserInfo(resourceMap, columns.getColumnByName("user_uid").getStringValue());
		}
		if(columns.getColumnByName("gooru_oid") != null){
			resourceMap = this.getTaxonomyInfo(resourceMap, columns.getColumnByName("gooru_oid").getStringValue());
			this.saveInESIndex(resourceMap, ESIndexices.CONTENTCATALOGINFO.getIndex()+"_"+cache.get(INDEXINGVERSION), IndexType.DIMRESOURCE.getIndexType(), columns.getColumnByName("gooru_oid").getStringValue());
		}
		}
    }
    
    private Map<String,Object> getLiveCounterData(String key,Map<String,Object> resourceMap){
    	
    	ColumnList<String> vluesList = baseDao.readWithKey(ColumnFamily.LIVEDASHBOARD.getColumnFamily(),key,0);
    	
    	if(vluesList != null && vluesList.size() > 0){
    		
    		long views = vluesList.getColumnByName("count~views") != null ?vluesList.getColumnByName("count~views").getLongValue() : 0L ;
    		long totalTimespent = vluesList.getColumnByName("time_spent~total") != null ?vluesList.getColumnByName("time_spent~total").getLongValue() : 0L ;

    		resourceMap.put("viewsCount",views);
    		resourceMap.put("totalTimespent",totalTimespent);
    		resourceMap.put("avgTimespent",views != 0L ? (totalTimespent/views) : 0L );
    		
    		long ratings = vluesList.getColumnByName("count~ratings") != null ?vluesList.getColumnByName("count~ratings").getLongValue() : 0L ;
    		long sumOfRatings = vluesList.getColumnByName("sum~rate") != null ?vluesList.getColumnByName("sum~rate").getLongValue() : 0L ;
    		resourceMap.put("ratingsCount",ratings);
    		resourceMap.put("sumOfRatings",sumOfRatings);
    		resourceMap.put("avgRating",ratings != 0L ? (sumOfRatings/ratings) : 0L );
    		
    		long reactions = vluesList.getColumnByName("count~reactions") != null ?vluesList.getColumnByName("count~reactions").getLongValue() : 0L ;
    		long sumOfreactionType = vluesList.getColumnByName("sum~reactionType") != null ?vluesList.getColumnByName("sum~reactionType").getLongValue() : 0L ;
    		resourceMap.put("reactionsCount",reactions);
    		resourceMap.put("sumOfreactionType",sumOfreactionType);
    		resourceMap.put("avgReaction",reactions != 0L ? (sumOfreactionType/reactions) : 0L );
    		
    		resourceMap.put("countOfRating5",vluesList.getColumnByName("count~5") != null ?vluesList.getColumnByName("count~5").getLongValue() : 0L );
    		resourceMap.put("countOfRating4",vluesList.getColumnByName("count~4") != null ?vluesList.getColumnByName("count~4").getLongValue() : 0L );
    		resourceMap.put("countOfRating3",vluesList.getColumnByName("count~3") != null ?vluesList.getColumnByName("count~3").getLongValue() : 0L );
    		resourceMap.put("countOfRating2",vluesList.getColumnByName("count~2") != null ?vluesList.getColumnByName("count~2").getLongValue() : 0L );
    		resourceMap.put("countOfRating1",vluesList.getColumnByName("count~1") != null ?vluesList.getColumnByName("count~1").getLongValue() : 0L );
    		
    		resourceMap.put("countOfICanExplain",vluesList.getColumnByName("count~i-can-explain") != null ?vluesList.getColumnByName("count~i-can-explain").getLongValue() : 0L );
    		resourceMap.put("countOfINeedHelp",vluesList.getColumnByName("count~i-need-help") != null ?vluesList.getColumnByName("count~i-need-help").getLongValue() : 0L );
    		resourceMap.put("countOfIDoNotUnderstand",vluesList.getColumnByName("count~i-donot-understand") != null ?vluesList.getColumnByName("count~i-donot-understand").getLongValue() : 0L );
    		resourceMap.put("countOfMeh",vluesList.getColumnByName("count~meh") != null ?vluesList.getColumnByName("count~meh").getLongValue() : 0L );
    		resourceMap.put("countOfICanUnderstand",vluesList.getColumnByName("count~i-can-understand") != null ?vluesList.getColumnByName("count~i-can-understand").getLongValue() : 0L );
    		resourceMap.put("copyCount",vluesList.getColumnByName("count~copy") != null ?vluesList.getColumnByName("count~copy").getLongValue() : 0L );
    		resourceMap.put("sharingCount",vluesList.getColumnByName("count~share") != null ?vluesList.getColumnByName("count~share").getLongValue() : 0L );
    		resourceMap.put("commentCount",vluesList.getColumnByName("count~comment") != null ?vluesList.getColumnByName("count~comment").getLongValue() : 0L );
    		resourceMap.put("reviewCount",vluesList.getColumnByName("count~review") != null ?vluesList.getColumnByName("count~review").getLongValue() : 0L );
    	}
    	return resourceMap;
    }
    
	public void saveInESIndex(Map<String,Object> eventMap ,String indexName,String indexType,String id ) {
		XContentBuilder contentBuilder = null;
		try {
				
				contentBuilder = jsonBuilder().startObject();			
				for(Map.Entry<String, Object> entry : eventMap.entrySet()){
					String rowKey = null;  				
					if(beFieldName.containsKey(entry.getKey())){
						rowKey = beFieldName.get(entry.getKey());
					}
					if(rowKey != null && entry.getValue() != null && !entry.getValue().equals("null") && entry.getValue() != ""){	            	
		            	contentBuilder.field(rowKey, TypeConverter.stringToAny(String.valueOf(entry.getValue()),fieldDataTypes.containsKey(entry.getKey()) ? fieldDataTypes.get(entry.getKey()) : "String"));
		            }
				}
			} catch (Exception e) {
				logger.info("Indexing failed in content Builder ",e);	
			}
			
			indexingES(indexName, indexType, id, contentBuilder, 0);
	}
	
	
	public void indexingES(String indexName,String indexType,String id ,XContentBuilder contentBuilder,int retryCount){
		try{
			contentBuilder.field("index_updated_time", new Date());
			getESClient().prepareIndex(indexName, indexType, id).setSource(contentBuilder).execute().actionGet();
		}catch(Exception e){
			if(retryCount < 6){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				logger.info("Retrying count: {}  ",retryCount);
				retryCount++;
    			indexingES(indexName, indexType, id, contentBuilder, retryCount);
        	}else{
        		logger.info("Indexing failed in Prod : {} ",e);
        		e.printStackTrace();
        	}
		}
		
	}


    public void getUserAndIndex(String userId) throws Exception{
		ColumnList<String> userInfos = baseDao.readWithKey(ColumnFamily.USER.getColumnFamily(), userId,0);
		
		if(userInfos != null & userInfos.size() > 0){
			logger.info("INdexing user : "+ userId);			
			XContentBuilder contentBuilder = jsonBuilder().startObject();
			if(userId != null){
				contentBuilder.field("user_uid",userId);
				Map<String,Object> userMap = new HashMap<String, Object>();
				this.getLiveCounterData("all~"+userId, userMap);
				for(Map.Entry<String, Object> entry : userMap.entrySet()){
					String rowKey = null;  				
					if(beFieldName.containsKey(entry.getKey())){
						rowKey = beFieldName.get(entry.getKey());
						contentBuilder.field(rowKey,entry.getValue());
					}
				}
			}
			if(userInfos.getColumnByName("confirmStatus") != null){
				contentBuilder.field("confirm_status",Long.valueOf(userInfos.getColumnByName("confirmStatus").getStringValue()));
			}
			if(userInfos.getColumnByName("createdOn") != null){
				contentBuilder.field("registered_on",userInfos.getColumnByName("createdOn").getDateValue());
			}
			if(userInfos.getColumnByName("addedBySystem") != null){
				contentBuilder.field("added_by_system",userInfos.getColumnByName("addedBySystem").getLongValue());
			}
			if(userInfos.getColumnByName("accountRegisterType") != null){
				contentBuilder.field("account_created_type",userInfos.getColumnByName("accountRegisterType").getStringValue());
			}
			if(userInfos.getColumnByName("referenceUid") != null){
				contentBuilder.field("reference_uid",userInfos.getColumnByName("referenceUid").getStringValue());
			}
			if(userInfos.getColumnByName("emailSso") != null){
				contentBuilder.field("email_sso",userInfos.getColumnByName("emailSso").getStringValue());
			}
			if(userInfos.getColumnByName("deactivatedOn") != null){
				contentBuilder.field("deactivated_on",userInfos.getColumnByName("deactivatedOn").getDateValue());
			}
			if(userInfos.getColumnByName("active") != null){
				contentBuilder.field("active",Integer.valueOf(userInfos.getColumnByName("active").getShortValue()));
			}
			if(userInfos.getColumnByName("lastLogin") != null){
				contentBuilder.field("last_login",userInfos.getColumnByName("lastLogin").getDateValue());
			}
			if(userInfos.getColumnByName("roleSet") != null){
				Set<String> roleSet = new HashSet<String>();
				for(String role : userInfos.getColumnByName("roleSet").getStringValue().split(",")){
					roleSet.add(role);
				}
				contentBuilder.field("roles",roleSet);
			}
			
			if(userInfos.getColumnByName("identityId") != null){
				contentBuilder.field("identity_id",userInfos.getColumnByName("identityId").getIntegerValue());
			}
			if(userInfos.getColumnByName("mailStatus") != null){
				contentBuilder.field("mail_status",userInfos.getColumnByName("mailStatus").getLongValue());
			}
			if(userInfos.getColumnByName("idpId") != null){
				contentBuilder.field("idp_id",userInfos.getColumnByName("idpId").getIntegerValue());
			}
			if(userInfos.getColumnByName("state") != null){
				contentBuilder.field("state",userInfos.getColumnByName("state").getStringValue());
			}
			if(userInfos.getColumnByName("loginType") != null){
				contentBuilder.field("login_type",userInfos.getColumnByName("loginType").getStringValue());
			}
			if(userInfos.getColumnByName("userGroupUid") != null){
				contentBuilder.field("user_group_uid",userInfos.getColumnByName("userGroupUid").getStringValue());
			}
			if(userInfos.getColumnByName("primaryOrganizationUid") != null){
				contentBuilder.field("primary_organization_uid",userInfos.getColumnByName("primaryOrganizationUid").getStringValue());
			}
			if(userInfos.getColumnByName("licenseVersion") != null){
				contentBuilder.field("license_version",userInfos.getColumnByName("licenseVersion").getStringValue());
			}
			if(userInfos.getColumnByName("parentId") != null){
				contentBuilder.field("parent_id",userInfos.getColumnByName("parentId").getLongValue());
			}
			if(userInfos.getColumnByName("lastname") != null){
				contentBuilder.field("lastname",userInfos.getColumnByName("lastname").getStringValue());
			}
			if(userInfos.getColumnByName("accountTypeId") != null){
				contentBuilder.field("account_type_id",userInfos.getColumnByName("accountTypeId").getLongValue());
			}
			if(userInfos.getColumnByName("isDeleted") != null){
				contentBuilder.field("is_deleted",userInfos.getColumnByName("isDeleted").getBooleanValue() ? 1 : 0);
			}
			if(userInfos.getColumnByName("emailId") != null){
				contentBuilder.field("external_id",userInfos.getColumnByName("emailId").getStringValue());
			}
			if(userInfos.getColumnByName("organization.partyUid") != null){
				contentBuilder.field("user_organization_uid",userInfos.getColumnByName("organization.partyUid").getStringValue());
			}
			if(userInfos.getColumnByName("importCode") != null){
				contentBuilder.field("import_code",userInfos.getColumnByName("importCode").getStringValue());
			}
			if(userInfos.getColumnByName("parentUid") != null){
				contentBuilder.field("parent_uid",userInfos.getColumnByName("parentUid").getStringValue());
			}
			if(userInfos.getColumnByName("securityGroupUid") != null){
				contentBuilder.field("security_group_uid",userInfos.getColumnByName("securityGroupUid").getStringValue());
			}
			if(userInfos.getColumnByName("userName") != null){
				contentBuilder.field("username",userInfos.getColumnByName("userName").getStringValue());
			}
			if(userInfos.getColumnByName("roleId") != null){
				contentBuilder.field("role_id",userInfos.getColumnByName("roleId").getLongValue());
			}
			if(userInfos.getColumnByName("firstname") != null){
				contentBuilder.field("firstname",userInfos.getColumnByName("firstname").getStringValue());
			}
			if(userInfos.getColumnByName("registerToken") != null){
				contentBuilder.field("register_token",userInfos.getColumnByName("registerToken").getStringValue());
			}
			if(userInfos.getColumnByName("viewFlag") != null){
				contentBuilder.field("view_flag",userInfos.getColumnByName("viewFlag").getLongValue());
			}
			if(userInfos.getColumnByName("accountUid") != null){
				contentBuilder.field("account_uid",userInfos.getColumnByName("accountUid").getStringValue());
			}
	    	
	    	ColumnList<String> eventDetailsNeww = baseDao.readWithKey(ColumnFamily.EXTRACTEDUSER.getColumnFamily(), userId, 0);
	    	for(Column<String> column : eventDetailsNeww) {
	    		if(column.getStringValue() != null){
	    			contentBuilder.field(column.getName(), column.getStringValue());
	    		}
	    	}
	     	
	    	ColumnList<String> aliasUserData = baseDao.readWithKey(ColumnFamily.ANONYMIZEDUSERDATA.getColumnFamily(), userId, 0);
	    	
	    	if(aliasUserData.getColumnNames().contains("firstname_alias")){
	    		contentBuilder.field("firstname_alias", aliasUserData.getColumnByName("firstname_alias").getStringValue());
	    	}
	    	if(aliasUserData.getColumnNames().contains("lastname_alias")){
	    		contentBuilder.field("lastname_alias", aliasUserData.getColumnByName("lastname_alias").getStringValue());
	    	}
	    	if(aliasUserData.getColumnNames().contains("username_alias")){
	    		contentBuilder.field("username_alias", aliasUserData.getColumnByName("username_alias").getStringValue());
	    	}
	    	
	    	contentBuilder.field("index_updated_time", new Date());
			connectionProvider.getESClient().prepareIndex(ESIndexices.USERCATALOGINFO.getIndex()+"_"+cache.get(INDEXINGVERSION), IndexType.DIMUSER.getIndexType(), userId).setSource(contentBuilder).execute().actionGet()			
    		;
		}else {
			throw new AccessDeniedException("Invalid Id : " + userId);
		}	
			
	}
    public void indexTaxonomy(String key) throws Exception{
    	
    	for(String id : key.split(",")){
    		ColumnList<String> sourceValues = baseDao.readWithKey(ColumnFamily.TAXONOMYCODE.getColumnFamily(), id,0);
	    	if(sourceValues != null && sourceValues.size() > 0){
	    		XContentBuilder contentBuilder = jsonBuilder().startObject();
	            for(int i = 0 ; i < sourceValues.size() ; i++) {
	            	if(taxonomyCodeType.get(sourceValues.getColumnByIndex(i).getName()).equalsIgnoreCase("String")){
	            		contentBuilder.field(sourceValues.getColumnByIndex(i).getName(),sourceValues.getColumnByIndex(i).getStringValue());
	            	}
	            	if(taxonomyCodeType.get(sourceValues.getColumnByIndex(i).getName()).equalsIgnoreCase("Long")){
	            		contentBuilder.field(sourceValues.getColumnByIndex(i).getName(),sourceValues.getColumnByIndex(i).getLongValue());
	            	}
	            	if(taxonomyCodeType.get(sourceValues.getColumnByIndex(i).getName()).equalsIgnoreCase("Integer")){
	            		contentBuilder.field(sourceValues.getColumnByIndex(i).getName(),sourceValues.getColumnByIndex(i).getIntegerValue());
	            	}
	            	if(taxonomyCodeType.get(sourceValues.getColumnByIndex(i).getName()).equalsIgnoreCase("Double")){
	            		contentBuilder.field(sourceValues.getColumnByIndex(i).getName(),sourceValues.getColumnByIndex(i).getDoubleValue());
	            	}
	            	if(taxonomyCodeType.get(sourceValues.getColumnByIndex(i).getName()).equalsIgnoreCase("Date")){
	            		contentBuilder.field(sourceValues.getColumnByIndex(i).getName(),TypeConverter.stringToAny(sourceValues.getColumnByIndex(i).getStringValue(), "Date"));
	            	}
	            }
	            contentBuilder.field("index_updated_time", new Date());
	    		connectionProvider.getESClient().prepareIndex(ESIndexices.TAXONOMYCATALOG.getIndex()+"_"+cache.get(INDEXINGVERSION), IndexType.TAXONOMYCODE.getIndexType(), id).setSource(contentBuilder).execute().actionGet()
	    		;
	    	}
    	}
    }
   
}