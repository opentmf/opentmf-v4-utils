package com.pia.tmf.v4.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pia.tmf.v4.common.model.CharacteristicSpecificationBase;
import com.pia.tmf.v4.common.model.CharacteristicValueSpecification;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

/**
 * @author Gokhan Demir
 */
class CharacteristicSpecificationUtilTests {

  @Test
  void isMandatoryReturnsTrueWhenMinAndMaxCardinalityIsOne() {
    var cs = new CharacteristicSpecificationBase();
    cs.setMinCardinality(1);
    cs.setMaxCardinality(1);

    boolean result = CharacteristicSpecificationUtil.isMandatory(cs);
    assertTrue(result);
  }

  @Test
  void isMandatoryReturnsFalseWhenMinAndMaxCardinalityIsNotOne() {
    var cs = new CharacteristicSpecificationBase();
    cs.setMinCardinality(0);
    cs.setMaxCardinality(0);

    boolean result = CharacteristicSpecificationUtil.isMandatory(cs);
    assertFalse(result);
  }

  @Test
  void isMandatoryReturnsFalseWhenMinAndMaxCardinalityIsNotEquals() {
    var cs = new CharacteristicSpecificationBase();
    cs.setMinCardinality(1);
    cs.setMaxCardinality(0);

    boolean result = CharacteristicSpecificationUtil.isMandatory(cs);
    assertFalse(result);
  }

  @Test
  void allowedValuesReturnsEmptyListWhenCharacteristicValueSpecificationIsNull() {
    var result = CharacteristicSpecificationUtil.allowedValues(null);
    assertTrue(result.isEmpty());
  }

  @Test
  void allowedValuesReturnsEmptyListWhenCharacteristicValueSpecificationIsEmpty() {
    var result = CharacteristicSpecificationUtil.allowedValues(Collections.emptyList());
    assertTrue(result.isEmpty());
  }

  @Test
  void allowedValuesReturnsCorrectValues() {
    CharacteristicValueSpecification cvs1 = new CharacteristicValueSpecification();
    cvs1.setValue("value1");
    CharacteristicValueSpecification cvs2 = new CharacteristicValueSpecification();
    cvs2.setValue("value2");

    var result = CharacteristicSpecificationUtil.allowedValues(Arrays.asList(cvs1, cvs2));

    assertEquals(2, result.size());
    assertTrue(result.contains("value1"));
    assertTrue(result.contains("value2"));
  }
}
