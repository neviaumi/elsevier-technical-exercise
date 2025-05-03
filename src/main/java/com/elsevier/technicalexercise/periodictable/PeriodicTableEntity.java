package com.elsevier.technicalexercise.periodictable;

import java.util.List;
import java.util.Map;

/**
 * Entity representing the periodic table data.
 * Contains a list of element data and an etag for versioning.
 */
public record PeriodicTableEntity(
    List<Map<String, Object>> data,
    String etag) {
}
