package org.opentmf.v4.common.util;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

import org.opentmf.common.model.ICharacteristicSpecificationBase;
import org.opentmf.common.model.ICharacteristicValueSpecification;
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

  /**
   * Returns whether the characteristic specification is mandatory, i.e. both min and max
   * cardinality equal 1.
   *
   * @param cs The characteristic specification to check.
   * @return {@code true} if the specification is mandatory, {@code false} otherwise.
   */
  public static boolean isMandatory(ICharacteristicSpecificationBase cs) {
    var minCardinality = toInt(String.valueOf(cs.getMinCardinality()), 0);
    var maxCardinality = toInt(String.valueOf(cs.getMaxCardinality()), 0);
    return (minCardinality == 1 && minCardinality == maxCardinality);
  }

  /**
   * Extracts the allowed values from the given list of characteristic value specifications.
   *
   * @param specifications The list of characteristic value specifications.
   * @return A list of allowed values, or an empty list if none are defined.
   */
  public static List<Object> allowedValues(
      List<? extends ICharacteristicValueSpecification> specifications) {
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
