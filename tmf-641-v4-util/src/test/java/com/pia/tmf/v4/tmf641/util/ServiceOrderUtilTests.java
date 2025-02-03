package com.pia.tmf.v4.tmf641.util;

import static com.pia.commons.util.JacksonUtil.fileToObject;
import static com.pia.commons.util.ListUtil.safeMutable;
import static com.pia.tmf.v4.tmf641.util.ServiceOrderUtil.findServiceOrderItemById;
import static com.pia.tmf.v4.tmf641.util.ServiceOrderUtil.validateOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pia.tmf.v4.tmf641.model.ServiceOrder;
import com.pia.tmf.v4.tmf641.model.ServiceOrderItem;
import com.pia.tmf.v4.tmf641.model.ServiceOrderItemRef;
import com.pia.tmf.v4.tmf641.model.ServiceOrderItemRelationship;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author Gokhan Demir
 */
class ServiceOrderUtilTests {

  private static final ServiceOrder SERVICE_ORDER =
      fileToObject("tmf641/service_order_42.json", ServiceOrder.class);

  @ParameterizedTest
  @ValueSource(strings = {"1", "2", "3", "4"})
  void testFindServiceOrderItemById_withValidId_returnsFoundItem(String id) {
    var item = findServiceOrderItemById(SERVICE_ORDER, id);
    assertNotNull(item);
    assertEquals(id, item.getId());
  }

  @Test
  void testFindServiceOrderItemById_withInvalidId_throwsException() {
    var e = assertThrows(IllegalArgumentException.class,
        () -> findServiceOrderItemById(SERVICE_ORDER, "nonExistentId"));
    assertEquals("ServiceOrderItem with id = nonExistentId not found in the collection",
        e.getMessage());
  }

  @Test
  void testValidateOrder_withValidData_validatesSuccessfully() {
    assertDoesNotThrow(() -> validateOrder(SERVICE_ORDER));
  }

  @Test
  void testValidate_withCircularDependencies_throwsException() {
    var order = order("1",
        item("100"),
        item("200", "300"),
        item("300", "400"),
        item("400", "200")
    );
    var e = assertThrows(IllegalArgumentException.class, () -> validateOrder(order));
    assertEquals("There is a cyclic dependency on order Item 200", e.getMessage());
  }

  @Test
  void testValidate_withComplexProductTree_throwsException() {
    var order = getComplexOrder();
    var e = assertThrows(IllegalArgumentException.class, () -> validateOrder(order));
    assertEquals("Very complex service order tree not supported.", e.getMessage());
  }

  @Test
  void testValidate_withoutIndependentOrderItem_throwsException() {
    var order = order("1");
    var e = assertThrows(IllegalArgumentException.class, () -> validateOrder(order));
    assertEquals("No independent start node exists.", e.getMessage());
  }

  @Test
  void testValidate_withAllReferencedOrderItems_throwsException() {
    var order = order("1",
        item("100"),
        item("200", "400"),
        item("300", "400"),
        item("400", "100", "200", "300")
    );
    var e = assertThrows(IllegalArgumentException.class, () -> validateOrder(order));
    assertEquals("No end node exists for the Service Order Flow.", e.getMessage());
  }

  @Test
  void testValidate_withCircularDependent_throwsException() {
    var order = order("1",
        item("100"),
        item("200", "400"),
        item("300", "400"),
        item("400", "100", "200", "300"),
        item("500")
    );
    var e = assertThrows(IllegalArgumentException.class, () -> validateOrder(order));
    assertEquals("There is a cyclic dependency on order Item 200", e.getMessage());
  }

  @Test
  void testValidate_withNonExistentOrderItemDependencySpecified_throwsException() {
    var order = order("1",
        item("100"),
        item("200", "400"),
        item("300", "400")
    );
    var e = assertThrows(IllegalArgumentException.class, () -> validateOrder(order));
    assertEquals("Item 200 refers to non-existent item 400", e.getMessage());
  }

  private ServiceOrder getComplexOrder() {
    var order = order("1");
    order.setServiceOrderItems(safeMutable(List.of(item("IndependentStartItem"))));
    for (int i = 0; i < 1000; i++) {
      var item = item("" + i);
      var list = new ArrayList<ServiceOrderItemRelationship>();
      for (int j = 0; j < 1000; j++) {
        var rel = new ServiceOrderItemRelationship();
        var itemRef = new ServiceOrderItemRef();
        itemRef.setItemId("" + j);
        rel.setOrderItem(itemRef);
        list.add(rel);
      }
      item.setServiceOrderItemRelationships(list);
      order.getServiceOrderItems().add(item);
    }
    return order;
  }

  private ServiceOrder order(String id, ServiceOrderItem... items) {
    var order = new ServiceOrder();
    order.setId(id);
    order.setServiceOrderItems(Arrays.stream(items).toList());
    return order;
  }

  private ServiceOrderItem item(String id, String... dependentIds) {
    var item = new ServiceOrderItem();
    item.setId(id);
    item.setServiceOrderItemRelationships(
        Arrays.stream(dependentIds)
            .map(
                dependentId -> {
                  var rel = new ServiceOrderItemRelationship();
                  var itemRef = new ServiceOrderItemRef();
                  itemRef.setItemId(dependentId);
                  rel.setOrderItem(itemRef);
                  return rel;
                })
            .toList());
    return item;
  }
}
