package swkom_dms.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import swkom_dms.DAL.entities.DocumentEntity;
import swkom_dms.DTO.DocumentDTO;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    // The componentModel="spring" makes sure MapStruct generates a Spring bean
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    DocumentDTO toDTO(DocumentEntity documentEntity);
    DocumentEntity toEntity(DocumentDTO documentDTO);
}
