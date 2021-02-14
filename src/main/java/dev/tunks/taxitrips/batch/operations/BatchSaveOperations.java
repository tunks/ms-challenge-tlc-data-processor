package dev.tunks.taxitrips.batch.operations;

import java.util.List;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import com.mongodb.BasicDBObject;

/***
 * Save batch save to MongoDB collections
 * 
 * @author ebrimatunkara 
 */
public class BatchSaveOperations<T> implements SaveOperations<T> {
    private MongoTemplate mongoTemplate;
    private String collection;
    
	public BatchSaveOperations(MongoTemplate mongoTemplate, String collection) {
		this.mongoTemplate = mongoTemplate;
		this.collection = collection;
	}

	@Override
	public void saveAll(List<? extends T> items) {
		if(items.isEmpty()) 
		{
		   return;
		}
		BulkOperations blkOperations = mongoTemplate.bulkOps(BulkMode.UNORDERED, collection);
		items.stream().forEach(item->insertOrUpdate(blkOperations, item));
		blkOperations.execute();
	}

	private void insertOrUpdate(BulkOperations blkOperations, T item) {
		if(collection.equals("location")) {
			BasicDBObject object = new BasicDBObject();
			mongoTemplate.getConverter().write(item, object);
			Query query = new Query(Criteria.where("_id").is(object.get("_id")));
			Update update = new Update();
			for(String key: object.keySet()) {
			   if(key.equals("_id")) 
				  continue;
			   
			   update.setOnInsert(key, object.get(key));
			}	
			blkOperations.upsert(query, update);
		}
		else {
			blkOperations.insert(item);	
		}
	}

}
