package org.opentmf.v4.tmf622.util;

import org.opentmf.v4.common.model.Characteristic;
import org.opentmf.v4.product.model.ProductRefOrValue;
import org.opentmf.v4.tmf622.model.OrderItemRelationship;
import org.opentmf.v4.tmf622.model.ProductOrderCreate;
import org.opentmf.v4.tmf622.model.ProductOrderItem;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Generated;

/**
 * Utility class for working with Product Order-related operations. This class provides methods to
 * retrieve specific Product Order items from a Product Order. The utility methods in this class
 * simplify the process of querying and handling Product Order items.
 *
 * @author Yusuf BOZKURT
 */
public final class ProductOrderUtil {

  private static final String BUNDLES = "bundles";

  @Generated
  private ProductOrderUtil() {
    throw new UnsupportedOperationException("ProductOrderUtil is a utility class with only static "
        + "methods, therefore cannot be instantiated.");
  }

  /**
   * Finds a Product Order item within the given Product Order by its ID.
   *
   * @param productOrder The Product Order containing the items to search within.
   * @param id The ID of the Product Order item to find.
   * @return The found Product Order item.
   * @throws IllegalArgumentException If the Product Order item with the given ID is not found in
   *     the list.
   */
  public static ProductOrderItem findProductOrderItemById(
      ProductOrderCreate productOrder, String id) {
    return productOrder.getProductOrderItems().stream()
        .filter(productOrderItem -> id.equals(productOrderItem.getId()))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "ProductOrderItem with id = " + id + " not found in the list"));
  }

  /**
   * Traverses the productOrderItems and returns the characteristics of the first matching product
   * with the requested product specification id.
   *
   * @param productOrder the product order.
   * @param productSpecificationId the product specification id to match.
   * @return the characteristics of the first matching product.
   */
  public static List<Characteristic> findProductCharacteristicsBySpecificationId(
      ProductOrderCreate productOrder, String productSpecificationId) {
    return productOrder.getProductOrderItems().stream()
        .map(ProductOrderItem::getProduct)
        .filter(Objects::nonNull)
        .filter(product -> productSpecificationMatchesId(product, productSpecificationId))
        .map(ProductRefOrValue::getProductCharacteristics)
        .findFirst()
        .orElse(Collections.emptyList());
  }

  private static boolean productSpecificationMatchesId(
      ProductRefOrValue product, String productSpecificationId) {
    return Objects.nonNull(product.getProductSpecification())
        && productSpecificationId.equals(product.getProductSpecification().getId());
  }

  public static boolean isBundle(ProductOrderItem item) {
    if (item.getProductOrderItemRelationships() != null) {
      return item.getProductOrderItemRelationships().stream()
          .anyMatch(relationship -> BUNDLES.equals(relationship.getRelationshipType()));
    }
    return false;
  }

  /**
   * Validates a ProductOrder and ensures the following:
   *
   * <ul>
   *   <li>The fulfillment flow can start, by ensuring there is at least one independent product
   *       item for the order flow to be runnable
   *   <li>The fulfillment flow can end, by ensuring there is at least one product item without
   *       being referenced by other product items
   *   <li>Validates all order items exist that are pointed by the orderItemRelationship.
   *   <li>Validates that order items does not have circular dependencies.
   * </ul>
   *
   * @param order The productOrderCreate payload.
   */
  public static void validateOrder(ProductOrderCreate order) {
    validateFlowCanStart(order);
    validateFlowCanEnd(order);
    validateAllDependentNodesExist(order);
    validateCircularDependencies(order);
  }

  private static final int MAX_ITERATION = 100000;

  private static void addDeepDependencies(
      ProductOrderCreate order,
      ProductOrderItem item,
      Set<String> deepDependencySet,
      Set<String> alreadyTraversed,
      int iterationCount) {
    if (!alreadyTraversed.contains(item.getId())) {
      alreadyTraversed.add(item.getId());
      if (iterationCount > MAX_ITERATION) {
        throw new IllegalArgumentException("Very complex product tree not supported.");
      }
      var relList = item.getProductOrderItemRelationships();
      if (relList != null) {
        for (OrderItemRelationship rel : relList) {
          deepDependencySet.add(rel.getId());
          addDeepDependencies(
              order,
              orderItemMap(order).get(rel.getId()),
              deepDependencySet,
              alreadyTraversed,
              ++iterationCount);
        }
      }
    }
  }

  private static Set<String> deepDependencies(ProductOrderCreate order, ProductOrderItem item) {
    var deepDependencySet = new HashSet<String>();
    var alreadyTraversedIds = new HashSet<String>();
    addDeepDependencies(order, item, deepDependencySet, alreadyTraversedIds, 0);
    return deepDependencySet;
  }

  private static void validateCircularDependency(ProductOrderCreate order, ProductOrderItem item) {
    var deepDependencySet = deepDependencies(order, item);
    if (deepDependencySet.contains(item.getId())) {
      throw new IllegalArgumentException(
          "There is a cyclic dependency on order Item " + item.getId());
    }
  }

  private static void validateCircularDependencies(ProductOrderCreate order) {
    order.getProductOrderItems().forEach(item -> validateCircularDependency(order, item));
  }

  /**
   * Ensures there is at least one independent order item for the flow to be runnable.
   *
   * @param order Product order to validate.
   */
  private static void validateFlowCanStart(ProductOrderCreate order) {
    for (var item : order.getProductOrderItems()) {
      if (referenceCount(item) == 0) {
        return;
      }
    }
    throw new IllegalArgumentException("No independent start node exists.");
  }

  private static int referenceCount(ProductOrderItem me) {
    var relList = me.getProductOrderItemRelationships();
    return relList == null ? 0 : relList.size();
  }

  private static int referencesMeCount(ProductOrderCreate order, ProductOrderItem me) {
    var count = 0;
    for (var orderItem : order.getProductOrderItems()) {
      if (orderItem != me) {
        var relList = orderItem.getProductOrderItemRelationships();
        if (relList != null) {
          for (var rel : relList) {
            if (rel.getId().equals(me.getId())) {
              count++;
            }
          }
        }
      }
    }
    return count;
  }

  /**
   * Ensures that there is at least one product item without being referenced by other product items
   * so that order flow can end.
   *
   * @param order The product order to validate.
   */
  private static void validateFlowCanEnd(ProductOrderCreate order) {
    for (var item : order.getProductOrderItems()) {
      var referencesMeCount = referencesMeCount(order, item);
      if (referencesMeCount == 0) {
        return;
      }
    }
    throw new IllegalArgumentException("No end node exists for the Product Order Flow.");
  }

  private static void validateDependentNodesExist(
      ProductOrderItem item, Map<String, ProductOrderItem> orderItemMap) {
    var relList = item.getProductOrderItemRelationships();
    if (relList != null) {
      for (var rel : relList) {
        var otherItem = orderItemMap.get(rel.getId());
        if (otherItem == null) {
          throw new IllegalArgumentException(
              "Item " + item.getId() + " refers to non-existent item " + rel.getId());
        }
      }
    }
  }

  private static Map<String, ProductOrderItem> orderItemMap(ProductOrderCreate order) {
    var map = new HashMap<String, ProductOrderItem>();
    for (var item : order.getProductOrderItems()) {
      map.put(item.getId(), item);
    }
    return map;
  }

  private static void validateAllDependentNodesExist(ProductOrderCreate order) {
    var map = orderItemMap(order);
    order.getProductOrderItems().forEach(item -> validateDependentNodesExist(item, map));
  }
}
