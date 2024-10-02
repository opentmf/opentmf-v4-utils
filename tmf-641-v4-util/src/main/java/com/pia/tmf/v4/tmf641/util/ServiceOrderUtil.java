package com.pia.tmf.v4.tmf641.util;

import com.pia.tmf.v4.tmf641.model.ServiceOrder;
import com.pia.tmf.v4.tmf641.model.ServiceOrderItem;
import lombok.Generated;

/**
 * Contains reusable utility methods related with service orders.
 *
 * @author Gokhan Demir
 */
public class ServiceOrderUtil {

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
}
