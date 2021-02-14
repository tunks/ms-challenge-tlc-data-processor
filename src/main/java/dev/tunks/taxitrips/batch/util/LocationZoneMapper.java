package dev.tunks.taxitrips.batch.util;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import dev.tunks.taxitrips.model.Location;

public class LocationZoneMapper implements FieldSetMapper<Location> {
	@Override
	public Location mapFieldSet(FieldSet fieldSet) throws BindException {
		if (fieldSet == null) {
	          return null;
	    }
		String id = fieldSet.readString(DataUtil.LOCATION_ID_FIELD);
		String borough = fieldSet.readString(DataUtil.BOROUGH_FIELD);
        String zone = fieldSet.readString(DataUtil.ZONE_FIELD);
        return new Location(id,borough,zone);
	}
}
