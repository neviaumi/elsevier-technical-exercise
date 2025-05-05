package com.elsevier.technicalexercise.periodictable;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for element listing requests.
 * Contains filtering parameters for querying periodic table elements.
 */
@Schema(description = "Data Transfer Object for element listing requests")
public class ElementListingRequestDto {
  @Schema(description = "The periodic table group to filter by. "
      + "Valid values are 1 to 18 (inclusive) or 'n/a'.",
      example = "2"
  )
  @ValidGroup
  private String group;

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }


}
