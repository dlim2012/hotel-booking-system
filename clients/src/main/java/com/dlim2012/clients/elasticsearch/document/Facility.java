package com.dlim2012.clients.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "facility")
public class Facility {

    private Integer id;

//    @Field(type = FieldType.Text, name = "display_name")
//    private String displayName;
}
