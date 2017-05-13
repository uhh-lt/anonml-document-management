package ml.anon.docmgmt.model;

import ml.anon.model.docmgmt.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "document", path = "document")
public interface DocumentRepository extends CrudRepository<Document, Long> {


}