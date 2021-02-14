package dev.tunks.taxitrips.batch.operations;

import java.util.List;

/***
 * Save operations base interface
 * 
 *  @author ebrimatunkara
 */
public interface SaveOperations<T> {
     public void saveAll(List<? extends T> items);
}
