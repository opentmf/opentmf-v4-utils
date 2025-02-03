package com.pia.tmf.v4.tmf641.util;

import com.pia.tmf.v4.tmf641.model.ServiceOrder;
import com.pia.tmf.v4.tmf641.model.ServiceOrderCreate;
import com.pia.tmf.v4.tmf641.model.ServiceOrderItem;
import com.pia.tmf.v4.tmf641.model.ServiceOrderItemRelationship;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Generated;

/**
 * Contains reusable utility methods related with service orders.
 *
 * @author Gokhan Demir
 */
public class ServiceOrderUtil {

  private static final int MAX_ITERATION = 100000;

  @Generated
  private ServiceOrderUtil() {}

  /**
   * Finds a Service Order item within the given Service Order by its ID.
   *
   * @param serviceOrder The Service Order containing the items to search within.
   * @param id The ID of the Service Order item to find.
   * @return The found Service Order item.
   * @throws IllegalArgumentException If the Service Order item with the given ID is not found in
   *     the list.
   */
  public static ServiceOrderItem findServiceOrderItemById(ServiceOrder serviceOrder, String id) {
    return serviceOrder.getServiceOrderItems().stream()
        .filter(serviceOrderItem -> id.equals(serviceOrderItem.getId()))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "ServiceOrderItem with id = " + id + " not found in the collection"));
  }

  /**
   * Validates a ServiceOrder and ensures the following:
   *
   * <ul>
   *   <li>The fulfillment flow can start, by ensuring there is at least one independent service
   *       item for the order flow to be runnable
   *   <li>The fulfillment flow can end, by ensuring there is at least one service item without
   *       being referenced by other service items
   *   <li>Validates all order items exist that are pointed by the orderItemRelationship.
   *   <li>Validates that order items does not have circular dependencies.
   * </ul>
   *
   * @param order The ServiceOrderCreate payload.
   */
  public static void validateOrder(ServiceOrderCreate order) {
    validateFlowCanStart(order);
    validateFlowCanEnd(order);
    validateAllDependentNodesExist(order);
    validateCircularDependencies(order);
  }

  /**
   * Ensures there is at least one independent order item for the flow to be runnable.
   *
   * @param order Service order to validate.
   */
  private static void validateFlowCanStart(ServiceOrderCreate order) {
    for (var item : order.getServiceOrderItems()) {
      if (referenceCount(item) == 0) {
        return;
      }
    }
    throw new IllegalArgumentException("No independent start node exists.");
  }

  private static int referenceCount(ServiceOrderItem me) {
    var relList = me.getServiceOrderItemRelationships();
    return relList == null ? 0 : relList.size();
  }

  /**
   * Ensures that there is at least one service item without being referenced by other service items
   * so that order flow can end.
   *
   * @param order The service order to validate.
   */
  private static void validateFlowCanEnd(ServiceOrderCreate order) {
    for (var item : order.getServiceOrderItems()) {
      var referencesMeCount = referencesMeCount(order, item);
      if (referencesMeCount == 0) {
        return;
      }
    }
    throw new IllegalArgumentException("No end node exists for the Service Order Flow.");
  }

  private static int referencesMeCount(ServiceOrderCreate order, ServiceOrderItem me) {
    var count = 0;
    for (var orderItem : order.getServiceOrderItems()) {
      var relList = orderItem.getServiceOrderItemRelationships();
      if (orderItem == me || relList == null ) {
        continue;
      }
      for (var rel : relList) {
        if (rel.getOrderItem().getItemId().equals(me.getId())) {
          count++;
        }
      }
    }
    return count;
  }

  private static void validateAllDependentNodesExist(ServiceOrderCreate order) {
    var map = orderItemMap(order);
    order.getServiceOrderItems().forEach(item -> validateDependentNodesExist(item, map));
  }

  private static Map<String, ServiceOrderItem> orderItemMap(ServiceOrderCreate order) {
    var map = new HashMap<String, ServiceOrderItem>();
    for (var item : order.getServiceOrderItems()) {
      map.put(item.getId(), item);
    }
    return map;
  }

  private static void validateDependentNodesExist(
      ServiceOrderItem item, Map<String, ServiceOrderItem> orderItemMap) {
    var relList = item.getServiceOrderItemRelationships();
    if(relList == null) {
      return;
    }

    for (var rel : relList) {
      var otherItem = orderItemMap.get(rel.getOrderItem().getItemId());
      if (otherItem == null) {
        throw new IllegalArgumentException(
            "Item "
                + item.getId()
                + " refers to non-existent item "
                + rel.getOrderItem().getItemId());
      }
    }
  }

  private static void validateCircularDependencies(ServiceOrderCreate order) {
    order.getServiceOrderItems().forEach(item -> validateCircularDependency(order, item));
  }

  private static void validateCircularDependency(ServiceOrderCreate order, ServiceOrderItem item) {
    var deepDependencySet = deepDependencies(order, item);
    if (deepDependencySet.contains(item.getId())) {
      throw new IllegalArgumentException(
          "There is a cyclic dependency on order Item " + item.getId());
    }
  }

  private static Set<String> deepDependencies(ServiceOrderCreate order, ServiceOrderItem item) {
    var deepDependencySet = new HashSet<String>();
    var alreadyTraversedIds = new HashSet<String>();
    addDeepDependencies(order, item, deepDependencySet, alreadyTraversedIds, 0);
    return deepDependencySet;
  }

  private static void addDeepDependencies(
      ServiceOrderCreate order,
      ServiceOrderItem item,
      Set<String> deepDependencySet,
      Set<String> alreadyTraversedSet,
      int iterationCount) {

    if (alreadyTraversedSet.contains(item.getId())) {
      return;
    }
    alreadyTraversedSet.add(item.getId());
    if (iterationCount > MAX_ITERATION) {
      throw new IllegalArgumentException("Very complex service order tree not supported.");
    }

    var relList = item.getServiceOrderItemRelationships();
    if(relList == null) {
      return;
    }
    for (ServiceOrderItemRelationship rel : relList) {
      deepDependencySet.add(rel.getOrderItem().getItemId());
      addDeepDependencies(
          order,
          orderItemMap(order).get(rel.getOrderItem().getItemId()),
          deepDependencySet,
          alreadyTraversedSet,
          ++iterationCount);
    }

  }

}
