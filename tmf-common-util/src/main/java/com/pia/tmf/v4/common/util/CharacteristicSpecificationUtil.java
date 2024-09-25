package com.pia.tmf.v4.common.util;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

import com.pia.tmf.v4.common.model.CharacteristicSpecificationBase;
import com.pia.tmf.v4.common.model.CharacteristicValueSpecification;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Generated;

/**
 * @author Gokhan Demir
 */
public final class CharacteristicSpecificationUtil {

  @Generated
  private CharacteristicSpecificationUtil() {
  }

  public static boolean isMandatory(CharacteristicSpecificationBase cs) {
    var minCardinality = toInt(String.valueOf(cs.getMinCardinality()), 0);
    var maxCardinality = toInt(String.valueOf(cs.getMaxCardinality()), 0);
    return (minCardinality == 1 && minCardinality == maxCardinality);
  }

  public static List<Object> allowedValues(List<CharacteristicValueSpecification> specifications) {
    if (specifications == null || specifications.isEmpty()) {
      return Collections.emptyList();
    }
    List<Object> values = new ArrayList<>();
    for (var spec : specifications) {
      values.add(spec.getValue());
    }
    return values;
  }
}
