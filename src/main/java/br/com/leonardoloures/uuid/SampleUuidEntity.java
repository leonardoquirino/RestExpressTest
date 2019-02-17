package br.com.leonardoloures.uuid;

import org.restexpress.plugin.hyperexpress.Linkable;
import br.com.leonardoloures.Constants;
import br.com.leonardoloures.serialization.UuidFormatter;

import com.strategicgains.hyperexpress.annotation.BindToken;
import com.strategicgains.hyperexpress.annotation.TokenBindings;
import com.strategicgains.repoexpress.mongodb.AbstractUuidMongodbEntity;

/**
 * This is a sample entity identified by a UUID (instead of a MongoDB ObjectID).
 * It also contains createdAt and updatedAt properties that are automatically maintained
 * by the persistence layer (SampleUuidEntityRepository).
 */
@TokenBindings({
	@BindToken(value=Constants.Url.SAMPLE_ID, field="id", formatter=UuidFormatter.class)
})
public class SampleUuidEntity
extends AbstractUuidMongodbEntity
implements Linkable
{
	public SampleUuidEntity()
	{
	}
}
