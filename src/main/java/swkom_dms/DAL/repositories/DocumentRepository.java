package swkom_dms.DAL.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swkom_dms.DAL.entities.DocumentEntity;
@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    // Custom query methods (if needed) can be defined here
}

