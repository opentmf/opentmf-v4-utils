package org.opentmf.v4.common.util;

import static org.opentmf.v4.common.util.CharacteristicUtil.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

import org.opentmf.v4.common.model.Characteristic;
import org.opentmf.v4.common.model.RelatedParty;
import java.time.OffsetDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentmf.v4.common.util.CharacteristicUtil;

/**
 * @author Gokhan Demir
 */
class CharacteristicUtilTests {

  private Collection<Characteristic> characteristics;
  private static final String CHARACTERISTIC_NAME = "test1";

  @BeforeEach
  void setUp() {
    Characteristic char1 = Characteristic.of("char1", "value1");
    Characteristic char2 = Characteristic.of("char2", 42);
    Characteristic char3 = Characteristic.of("char3", "true");
    characteristics = Arrays.asList(char1, char2, char3);
  }

  private static final List<Characteristic> CHARACTERISTIC_LIST = List.of(
      Characteristic.of("key1", "value1"),
      Characteristic.of("key2", "value2"),
      Characteristic.of("key3", true),
      Characteristic.of("key4", 1),
      Characteristic.of("key5", Long.MAX_VALUE),
      Characteristic.of("dateStr", "2023-02-16")
  );

  @Test
  void test_getMandatoryCharacteristicValue_returnsValidStringResult() {
    assertEquals("value1", getMandatoryCharacteristicValue("key1", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getMandatoryCharacteristicValue_returnsValidIntegerResult() {
    Object result = getMandatoryCharacteristicValue("key4", CHARACTERISTIC_LIST);
    assertNotNull(result);
    assertEquals(1, result);
  }

  @Test
  void test_getMandatoryCharacteristicValue_withValidData_returnsValidResult() {
    var characteristic = Characteristic.of(CHARACTERISTIC_NAME, "1");
    assertEquals("1", getMandatoryCharacteristicValue(
        characteristic.getName(), List.of(characteristic)));
  }

  @Test
  void test_getMandatoryCharacteristic_returnsValidResult() {
    var result = CharacteristicUtil.getMandatoryCharacteristic("key1", CHARACTERISTIC_LIST);
    assertNotNull(result);
    assertEquals("key1", result.getName());
  }

  @Test
  void test_getMandatoryCharacteristic_withoutCharacteristic_throwsException() {
    var e = assertThrows(IllegalArgumentException.class, () ->
            CharacteristicUtil.getMandatoryCharacteristic("nonExistent", CHARACTERISTIC_LIST));
    assertEquals("Mandatory characteristic nonExistent not found in the characteristic list",
            e.getMessage());
  }

  @Test
  void test_getMandatoryCharacteristic_NotFound() {
    assertThrows(
            IllegalArgumentException.class,
            () -> CharacteristicUtil.getMandatoryCharacteristic("nonExistentChar", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getMandatoryCharacteristicStringValue_withValidData() {
    String result =
            CharacteristicUtil.getMandatoryCharacteristicStringValue("key1", CHARACTERISTIC_LIST);
    assertNotNull(result);
    assertEquals("value1", result);
  }

  @Test
  void test_getMandatoryCharacteristicStringValue_withNonExistData() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
            CharacteristicUtil.getMandatoryCharacteristicStringValue("nonExistent", CHARACTERISTIC_LIST));
    assertEquals("Mandatory characteristic nonExistent not found in the characteristic list",
            e.getMessage());
  }

  @Test
  void test_getMandatoryCharacteristicIntegerValue_withStringData() {
    assertEquals(0, getMandatoryCharacteristicIntegerValue("key1", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getMandatoryCharacteristicIntegerValue_withExistInteger() {
    int result = getMandatoryCharacteristicIntegerValue("key4", CHARACTERISTIC_LIST);
    assertEquals(1, result);
  }

  @Test
  void test_getMandatoryCharacteristicIntegerValue_withNonExistData() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
            CharacteristicUtil.getMandatoryCharacteristicIntegerValue("nonExistent", CHARACTERISTIC_LIST));
    assertEquals("Mandatory characteristic nonExistent not found in the characteristic list",
            e.getMessage());
  }

  @Test
  void test_getMandatoryCharacteristicLongValue_withStringData() {
    assertEquals(0, getMandatoryCharacteristicLongValue("key1", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getMandatoryCharacteristicLongValue_withExistInteger() {
    Long result = getMandatoryCharacteristicLongValue("key5", CHARACTERISTIC_LIST);
    assertEquals(Long.MAX_VALUE, result);
  }

  @Test
  void test_getMandatoryCharacteristicLongValue_withNonExistData() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
            getMandatoryCharacteristicLongValue("nonExistent", CHARACTERISTIC_LIST));
    assertEquals("Mandatory characteristic nonExistent not found in the characteristic list",
            e.getMessage());
  }

  @Test
  void test_getMandatoryCharacteristicBooleanValue_withValidData_returnsValidResult() {
    assertTrue(getMandatoryCharacteristicBooleanValue("key3", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getMandatoryCharacteristicBooleanValue_withNoDataFound_throwsException() {
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () -> getMandatoryCharacteristicBooleanValue("nonExistent", CHARACTERISTIC_LIST));
    assertEquals(
        "Mandatory characteristic nonExistent not found in the characteristic list",
        e.getMessage());
  }


  @Test
  void test_getMandatoryCharacteristicOffsetDateTimeValue_withValidDateFormat_returnsValidResult() {
    var offsetDateTime = getMandatoryCharacteristicOffsetDateTimeValue("dateStr", CHARACTERISTIC_LIST);
    assertNotNull(offsetDateTime);
    assertInstanceOf(OffsetDateTime.class, offsetDateTime);
  }

  @Test
  void test_getMandatoryCharacteristicOffsetDateTimeValue_withNullValue_returnsValidResult() {
    var offsetDateTime =
        getMandatoryCharacteristicOffsetDateTimeValue(
            "dateStr", Collections.singletonList(Characteristic.of("dateStr", null)));
    assertNull(offsetDateTime);
  }

  @Test
  void test_getMandatoryCharacteristicOffsetDateTimeValue_withNonExistKey_returnsValidResult() {
    assertThrows(IllegalArgumentException.class, () ->
        getMandatoryCharacteristicOffsetDateTimeValue("nonExistent", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getMandatoryCharacteristicOffsetDateTimeValue_withInvalidFormat_ThrowsException() {
    Characteristic characteristic = new Characteristic();
    characteristic.setName("test");
    characteristic.setValue("invalid format");
    var list = Collections.singletonList(characteristic);
    assertThrows(IllegalArgumentException.class, () ->
        getMandatoryCharacteristicOffsetDateTimeValue("test", list));
  }

  @Test
  void test_getOptionalCharacteristicValue_returnsValidStringResult() {
    assertEquals("value1", getOptionalCharacteristicValue("key1", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getOptionalCharacteristicValue_returnsValidIntegerResult() {
    Object result = getOptionalCharacteristicValue("key4", CHARACTERISTIC_LIST);
    assertNotNull(result);
    assertEquals(1, result);
  }

  @Test
  void test_getOptionalCharacteristicValue_withValidData_returnsValidResult() {
    var characteristic = Characteristic.of(CHARACTERISTIC_NAME, "1");
    assertEquals("1", getOptionalCharacteristicValue(
        characteristic.getName(), List.of(characteristic)));
  }

  @Test
  void test_getOptionalCharacteristicStringValue_withValidData() {
    String result = CharacteristicUtil.getOptionalCharacteristicStringValue(
        "key1", CHARACTERISTIC_LIST);
    assertNotNull(result);
    assertEquals("value1", result);
  }

  @Test
  void test_getOptionalCharacteristicStringValue_withNonExistData() {
    assertNull(getOptionalCharacteristicStringValue("nonExistent", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getOptionalCharacteristicIntegerValue_withStringData() {
    assertEquals(0, getOptionalCharacteristicIntegerValue("key1", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getOptionalCharacteristicIntegerValue_withExistInteger() {
    int result = getOptionalCharacteristicIntegerValue("key4", CHARACTERISTIC_LIST);
    assertEquals(1, result);
  }

  @Test
  void test_getOptionalCharacteristicIntegerValue_withNonExistData() {
    assertNull(getOptionalCharacteristicIntegerValue("nonExistent", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getOptionalCharacteristicLongValue_withStringData() {
    assertEquals(0, getOptionalCharacteristicLongValue("key1", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getOptionalCharacteristicLongValue_withExistInteger() {
    Long result = getOptionalCharacteristicLongValue("key5", CHARACTERISTIC_LIST);
    assertEquals(Long.MAX_VALUE, result);
  }

  @Test
  void test_getOptionalCharacteristicLongValue_withNonExistData() {
    assertNull(getOptionalCharacteristicLongValue("nonExistent", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getOptionalCharacteristicBooleanValue_withValidData_returnsValidResult() {
    assertTrue(getOptionalCharacteristicBooleanValue("key3", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getOptionalCharacteristicBooleanValue_withNoDataFound_returnNull() {
    assertNull(getOptionalCharacteristicBooleanValue("nonExistent", CHARACTERISTIC_LIST));
  }


  @Test
  void test_getOptionalCharacteristicOffsetDateTimeValue_withValidDateFormat_returnsValidResult() {
    var offsetDateTime = getOptionalCharacteristicOffsetDateTimeValue(
        "dateStr", CHARACTERISTIC_LIST);
    assertNotNull(offsetDateTime);
    assertInstanceOf(OffsetDateTime.class, offsetDateTime);
  }

  @Test
  void test_getOptionalCharacteristicOffsetDateTimeValue_withNullValue_returnsValidResult() {
    var offsetDateTime = getOptionalCharacteristicOffsetDateTimeValue(
        "dateStr", Collections.singletonList(Characteristic.of("dateStr", null)));
    assertNull(offsetDateTime);
  }

  @Test
  void test_getOptionalCharacteristicOffsetDateTimeValue_withNonExistKey_returnsValidResult() {
    assertNull(getOptionalCharacteristicOffsetDateTimeValue("nonExistent", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getOptionalCharacteristicOffsetDateTimeValue_withInvalidFormat_ThrowsException() {
    Characteristic characteristic = new Characteristic();
    characteristic.setName("test");
    characteristic.setValue("invalid format");
    var list = Collections.singletonList(characteristic);
    assertThrows(IllegalArgumentException.class, () ->
        getOptionalCharacteristicOffsetDateTimeValue("test", list));
  }

  @Test
  void test_getOptionalCharacteristicStringValue_isEmpty() {
    assertNull(getOptionalCharacteristicStringValue("abc", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getOptionalCharacteristicStringValue_isPresent() {
    assertEquals("value1", getOptionalCharacteristicStringValue("key1", CHARACTERISTIC_LIST));
  }

  @Test
  void test_getStringValue_withCharacteristicValue_returnsNull() {
    assertNull(getStringValue(Characteristic.of("key1", null)));
  }

  @Test
  void test_getBooleanValue_withCharacteristicValue_returnsNull() {
    assertNull(getBooleanValue(Characteristic.of("key1", null)));
  }

  @Test
  void test_getIntegerValue_withCharacteristicValue_returnsNull() {
    assertNull(getIntegerValue(Characteristic.of("key1", null)));
  }

  @Test
  void test_getLongValue_withCharacteristicValue_returnsNull() {
    assertNull(getLongValue(Characteristic.of("key1", null)));
  }

  @Test
  void test_findCharacteristicByName_withNullCollection_returnsEmpty() {
    assertFalse(findCharacteristicByName("any", null).isPresent());
  }

  @Test
  void test_findCharacteristicByName_withEmptyCollection_returnsEmpty() {
    assertFalse(findCharacteristicByName("any", emptyList()).isPresent());
  }

  @Test
  void test_findCharacteristicByName() {
    Characteristic result = CharacteristicUtil.findCharacteristicByName("char2", characteristics)
        .orElse(null);
    assertNotNull(result);
    assertEquals("char2", result.getName());
  }

  @Test
  void test_findCharacteristicByName_NotFound() {
    assertFalse(CharacteristicUtil.findCharacteristicByName("nonExistentChar", characteristics)
        .isPresent());
  }

  @Test
  void test_toNameStringMap_returnsCorrectMap() {
    Characteristic characteristic1 = new Characteristic();
    characteristic1.setName("test1");
    characteristic1.setValue("value1");

    Characteristic characteristic2 = new Characteristic();
    characteristic2.setName("test2");
    characteristic2.setValue("value2");

    Map<String, String> result = CharacteristicUtil.toNameStringMap(Arrays.asList(characteristic1, characteristic2));

    assertEquals(2, result.size());
    assertEquals("value1", result.get("test1"));
    assertEquals("value2", result.get("test2"));
  }

  @Test
  void test_toNameStringMap_returnsLastValueForDuplicateKeys() {
    Characteristic characteristic1 = new Characteristic();
    characteristic1.setName("test");
    characteristic1.setValue("value1");

    Characteristic characteristic2 = new Characteristic();
    characteristic2.setName("test");
    characteristic2.setValue("value2");

    Map<String, String> result = CharacteristicUtil.toNameStringMap(Arrays.asList(characteristic1, characteristic2));

    assertEquals(1, result.size());
    assertEquals("value2", result.get("test"));
  }

  @Test
  void tesT_toNameObjectMap_returnsCorrectMap() {
    Characteristic characteristic1 = new Characteristic();
    characteristic1.setName("test1");
    characteristic1.setValue("value1");

    Characteristic characteristic2 = new Characteristic();
    characteristic2.setName("test2");
    characteristic2.setValue("value2");

    Map<String, Object> result = CharacteristicUtil.toNameObjectMap(Arrays.asList(characteristic1, characteristic2));

    assertEquals(2, result.size());
    assertEquals("value1", result.get("test1"));
    assertEquals("value2", result.get("test2"));
  }

  @Test
  void test_toNameObjectMap_returnsLastValueForDuplicateKeys() {
    Characteristic characteristic1 = new Characteristic();
    characteristic1.setName("test-object");
    RelatedParty relatedParty1 = new RelatedParty();
    relatedParty1.setName("related_party1");
    characteristic1.setValue(relatedParty1);

    Characteristic characteristic2 = new Characteristic();
    characteristic2.setName("test-object");
    RelatedParty relatedParty2 = new RelatedParty();
    relatedParty2.setName("related_party2");
    characteristic2.setValue(relatedParty2);

    Map<String, Object> result = CharacteristicUtil.toNameObjectMap(Arrays.asList(characteristic1, characteristic2));

    assertEquals(1, result.size());
    assertInstanceOf(RelatedParty.class, result.get("test-object"));
    assertEquals("related_party2", ((RelatedParty) result.get("test-object")).getName());
  }

  @Test
  void detectDuplicatesReturnsCorrectMap() {
    Characteristic characteristic1 = new Characteristic();
    characteristic1.setName("test");
    characteristic1.setValue("value1");

    Characteristic characteristic2 = new Characteristic();
    characteristic2.setName("test");
    characteristic2.setValue("value2");

    var result = CharacteristicUtil.detectDuplicates(
        Arrays.asList(characteristic1, characteristic2));

    assertEquals(1, result.size());
    assertTrue(result.containsKey("test"));
    assertEquals(2, result.get("test"));
  }

  @Test
  void detectDuplicatesReturnsEmptyMapWhenNoDuplicates() {
    Characteristic characteristic1 = new Characteristic();
    characteristic1.setName("test1");
    characteristic1.setValue("value1");

    Characteristic characteristic2 = new Characteristic();
    characteristic2.setName("test2");
    characteristic2.setValue("value2");

    var result = CharacteristicUtil.detectDuplicates(
        Arrays.asList(characteristic1, characteristic2));

    assertTrue(result.isEmpty());
  }
}
