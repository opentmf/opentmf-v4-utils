package org.opentmf.v4.common.util;

import static org.apache.commons.lang3.BooleanUtils.toBooleanObject;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import org.opentmf.v4.common.model.Characteristic;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Generated;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Utility class for working with characteristics within collections. This class provides convenient
 * helper methods to retrieve and manipulate characteristics in a collection.
 *
 * @author Yusuf BOZKURT
 * @author Gokhan Demir
 */
public final class CharacteristicUtil {

  @Generated
  private CharacteristicUtil() {
    throw new UnsupportedOperationException(
        "CharacteristicUtil is a utility class only with static methods, "
            + "therefore cannot be instantiated.");
  }

  /**
   * Returns the optional characteristic's value as an object, or null if it does not exist.
   *
   * @param name The optional characteristic name.
   * @param characteristics The collection that holds characteristics.
   * @return the optional characteristic's value as an object, or null if it does not exist.
   */
  public static Object getOptionalCharacteristicValue(
      String name, Collection<Characteristic> characteristics) {
    return findCharacteristicByName(name, characteristics)
        .map(Characteristic::getValue)
        .orElse(null);
  }

  /**
   * Returns the optional characteristic's string value. If the characteristic does not exist then
   * returns null.
   *
   * @param name The name of the characteristic.
   * @param characteristics The collection that holds characteristics.
   * @return the optional characteristic's string value or null if the characteristic does not
   *     exist.
   */
  public static String getOptionalCharacteristicStringValue(
      String name, Collection<Characteristic> characteristics) {
    return findCharacteristicByName(name, characteristics)
        .map(CharacteristicUtil::getStringValue)
        .orElse(null);
  }

  public static Boolean getOptionalCharacteristicBooleanValue(
      String name, Collection<Characteristic> characteristics) {
    return findCharacteristicByName(name, characteristics)
        .map(CharacteristicUtil::getBooleanValue)
        .orElse(null);
  }

  public static Integer getOptionalCharacteristicIntegerValue(
      String name, Collection<Characteristic> characteristics) {
    return findCharacteristicByName(name, characteristics)
        .map(CharacteristicUtil::getIntegerValue)
        .orElse(null);
  }

  public static Long getOptionalCharacteristicLongValue(
      String name, Collection<Characteristic> characteristics) {
    return findCharacteristicByName(name, characteristics)
        .map(CharacteristicUtil::getLongValue)
        .orElse(null);
  }

  public static OffsetDateTime getOptionalCharacteristicOffsetDateTimeValue(
      String name, Collection<Characteristic> characteristics) {
    return findCharacteristicByName(name, characteristics)
        .map(CharacteristicUtil::getOffsetDateTimeValue)
        .orElse(null);
  }

  /**
   * Retrieves the mandatory characteristic from the provided collection by its name.
   *
   * @param name The name of the mandatory characteristic to be retrieved.
   * @param characteristics The collection of characteristics to search within.
   * @return The found mandatory characteristic.
   * @throws IllegalArgumentException If the mandatory characteristic is not found in the
   *     collection.
   */
  public static Characteristic getMandatoryCharacteristic(
      String name, Collection<Characteristic> characteristics) {
    return findCharacteristicByName(name, characteristics)
        .orElseThrow(() -> mandatoryCharacteristicNotFound(name));
  }

  /**
   * Retrieves the value of the mandatory characteristic from the provided collection by its name.
   *
   * @param name The name of the mandatory characteristic whose value is to be retrieved.
   * @param characteristics The collection of characteristics to search within.
   * @return The value of the found mandatory characteristic.
   * @throws IllegalArgumentException If the mandatory characteristic is not found in the
   *     collection.
   */
  public static Object getMandatoryCharacteristicValue(
      String name, Collection<Characteristic> characteristics) {
    return getMandatoryCharacteristic(name, characteristics).getValue();
  }

  /**
   * Parses the string value of a mandatory characteristic that holds a date-time value in one of
   * the following patterns in order and then returns its value as an OffsetDateTime in the default
   * system time zone.
   *
   * <ul>
   *   <li>yyyy/MM/dd'T'HH:mm:ss.SSSSSSZ
   *   <li>yyyy/MM/dd'T'HH:mm:ssZ
   *   <li>yyyy/MM/dd'T'HH:mm:ss.SSSSSS
   *   <li>yyyy/MM/dd'T'HH:mm:ss
   *   <li>yyyy/MM/dd
   *   <li>yyyy-MM-dd"
   * </ul>
   *
   * @param name The name of the mandatory characteristic whose value is to be retrieved.
   * @param characteristics The collection of characteristics to search within.
   * @return The date-time string value of the mandatory characteristic as an OffsetDateTime.
   * @throws IllegalArgumentException If the mandatory characteristic is not found in the
   *     collection, or it cannot be parsed using neither of the above formats.
   */
  public static OffsetDateTime getMandatoryCharacteristicOffsetDateTimeValue(
      String name, Collection<Characteristic> characteristics) {
    return getOffsetDateTimeValue(getMandatoryCharacteristic(name, characteristics));
  }

  /**
   * Retrieves the string value of the mandatory characteristic from the provided collection by its
   * name.
   *
   * @param name The name of the mandatory characteristic whose string value is to be retrieved.
   * @param characteristics The collection of characteristics to search within.
   * @return The string value of the found mandatory characteristic.
   * @throws IllegalArgumentException If the mandatory characteristic is not found in the
   *     collection.
   */
  public static String getMandatoryCharacteristicStringValue(
      String name, Collection<Characteristic> characteristics) {
    return getStringValue(getMandatoryCharacteristic(name, characteristics));
  }

  /**
   * Returns the string value of the existing characteristic.
   *
   * @param characteristic Existing characteristic.
   * @return the string value of the existing characteristic or null.
   * @throws NullPointerException If the characteristic itself is null.
   */
  public static String getStringValue(final Characteristic characteristic) {
    return characteristic.getValue() == null ? null : String.valueOf(characteristic.getValue());
  }

  public static Boolean getBooleanValue(final Characteristic characteristic) {
    String stringValue = getStringValue(characteristic);
    return stringValue == null ? null : toBooleanObject(stringValue);
  }

  public static Integer getIntegerValue(final Characteristic characteristic) {
    String stringValue = getStringValue(characteristic);
    return stringValue == null ? null : toInt(stringValue);
  }

  public static Long getLongValue(final Characteristic characteristic) {
    String stringValue = getStringValue(characteristic);
    return stringValue == null ? null : toLong(stringValue);
  }

  public static OffsetDateTime getOffsetDateTimeValue(final Characteristic characteristic) {
    var dateStr = getStringValue(characteristic);
    if (dateStr == null) {
      return null;
    }
    try {
      var date =
          DateUtils.parseDateStrictly(
              dateStr,
              "yyyy/MM/dd'T'HH:mm:ss.SSSSSSZ",
              "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ",
              "yyyy/MM/dd'T'HH:mm:ssZ",
              "yyyy-MM-dd'T'HH:mm:ssZ",
              "yyyy/MM/dd'T'HH:mm:ss.SSSSSS",
              "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
              "yyyy/MM/dd'T'HH:mm:ss",
              "yyyy-MM-dd'T'HH:mm:ss",
              "yyyy/MM/dd",
              "yyyy-MM-dd",
              "yyyy-MM-dd'T'HH:mm:ssX");
      return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    } catch (ParseException e) {
      throw new IllegalArgumentException("Date string " + dateStr + " could not be parsed.");
    }
  }

  /**
   * Retrieves the integer value of the mandatory characteristic from the provided collection by its
   * name.
   *
   * @param name The name of the mandatory characteristic whose integer value is to be retrieved.
   * @param collection The collection of characteristics to search within.
   * @return The integer value of the found mandatory characteristic.
   * @throws IllegalArgumentException If the mandatory characteristic is not found in the collection
   *     or if the value cannot be converted to an integer.
   */
  public static Integer getMandatoryCharacteristicIntegerValue(
      String name, Collection<Characteristic> collection) {
    return getIntegerValue(getMandatoryCharacteristic(name, collection));
  }

  /**
   * Retrieves the integer value of the mandatory characteristic from the provided collection by its
   * name.
   *
   * @param name The name of the mandatory characteristic whose integer value is to be retrieved.
   * @param characteristics The collection of characteristics to search within.
   * @return The integer value of the found mandatory characteristic.
   * @throws IllegalArgumentException If the mandatory characteristic is not found in the collection
   *     or if the value cannot be converted to an integer.
   */
  public static Long getMandatoryCharacteristicLongValue(
      String name, Collection<Characteristic> characteristics) {
    return getLongValue(getMandatoryCharacteristic(name, characteristics));
  }

  /**
   * Retrieves the boolean value of the mandatory characteristic from the provided collection by its
   * name.
   *
   * @param name The name of the mandatory characteristic whose boolean value is to be retrieved.
   * @param characteristics The collection of characteristics to search within.
   * @return The boolean value of the found mandatory characteristic.
   * @throws IllegalArgumentException If the mandatory characteristic is not found in the collection
   *     or if the value cannot be converted to a boolean.
   */
  public static Boolean getMandatoryCharacteristicBooleanValue(
      String name, Collection<Characteristic> characteristics) {
    return getBooleanValue(getMandatoryCharacteristic(name, characteristics));
  }

  /**
   * Finds a characteristic by its name in the provided collection.
   *
   * @param name The name of the characteristic to find.
   * @param characteristics The collection of characteristics to search within.
   * @return An Optional containing the found characteristic, or an empty Optional if not found.
   */
  public static Optional<Characteristic> findCharacteristicByName(
      String name, Collection<Characteristic> characteristics) {
    if (characteristics != null && !characteristics.isEmpty()) {
      for (Characteristic c : characteristics) {
        if (c.getName().equals(name)) {
          return Optional.of(c);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Returns the name - value pairs in this characteristics as a map.
   *
   * @param characteristics The characteristic collection to map.
   * @return The map of the characteristic names and their values as an object.
   */
  public static Map<String, Object> toNameObjectMap(Collection<Characteristic> characteristics) {
    return characteristics.stream()
        .collect(
            Collectors.toMap(
                Characteristic::getName, Characteristic::getValue, (a, b) -> b, HashMap::new));
  }

  /**
   * Returns the name - value pairs in the characteristics as a map.
   *
   * @param characteristics The characteristic collection to map.
   * @return The map of the characteristic names and their values as a string.
   */
  public static Map<String, String> toNameStringMap(Collection<Characteristic> characteristics) {
    return characteristics.stream()
        .collect(
            Collectors.toMap(
                Characteristic::getName,
                c -> emptyINull(CharacteristicUtil.getStringValue(c)),
                (a, b) -> b,
                HashMap::new));
  }

  private static String emptyINull(String s) {
    return s == null ? "" : s;
  }

  private static IllegalArgumentException mandatoryCharacteristicNotFound(String name) {
    String message =
        MessageFormat.format(
            "Mandatory characteristic {0} not found " + "in the characteristic list", name);
    return new IllegalArgumentException(message);
  }

  /**
   * Detects the duplicate characteristic names in the given collection of characteristics and
   * return a name - count map that holds the duplicate characteristic names and the counts.
   *
   * @param characteristics the collection to be inspected for duplicate characteristic names.
   * @return A map of name - count pairs for the detected duplicate characteristic names.
   */
  public static Map<String, Integer> detectDuplicates(Collection<Characteristic> characteristics) {
    var temp = new HashMap<String, Integer>();
    for (var characteristic : characteristics) {
      if (temp.containsKey(characteristic.getName())) {
        temp.put(characteristic.getName(), temp.get(characteristic.getName()) + 1);
      } else {
        temp.put(characteristic.getName(), 1);
      }
    }
    var ret = new HashMap<String, Integer>();
    for (var entry : temp.entrySet()) {
      if (entry.getValue() > 1) {
        ret.put(entry.getKey(), entry.getValue());
      }
    }
    return ret;
  }
}
