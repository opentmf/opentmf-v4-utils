package org.opentmf.v4.tmf622.util;

import static org.opentmf.commons.util.JacksonUtil.fileToObject;
import static org.opentmf.commons.util.ListUtil.safeMutable;
import static org.opentmf.v4.tmf622.util.ProductOrderUtil.findProductCharacteristicsBySpecificationId;
import static org.opentmf.v4.tmf622.util.ProductOrderUtil.findProductOrderItemById;
import static org.opentmf.v4.tmf622.util.ProductOrderUtil.isBundle;
import static org.opentmf.v4.tmf622.util.ProductOrderUtil.validateOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.opentmf.v4.tmf622.model.OrderItemRelationship;
import org.opentmf.v4.tmf622.model.ProductOrder;
import org.opentmf.v4.tmf622.model.ProductOrderCreate;
import org.opentmf.v4.tmf622.model.ProductOrderItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opentmf.v4.tmf622.util.ProductOrderUtil;

class ProductOrderUtilTests {

  @Test
  void testFindProductOrderItemById_Success() {
    var productOrder = new ProductOrder();
    var item1 = new ProductOrderItem();
    item1.setId("item1");
    var item2 = new ProductOrderItem();
    item2.setId("item2");
    productOrder.setProductOrderItems(List.of(item1, item2));

    ProductOrderItem foundItem = ProductOrderUtil.findProductOrderItemById(productOrder, "item1");
    assertNotNull(foundItem);
    assertEquals("item1", foundItem.getId());
  }

  @Test
  void testFindProductOrderItemById_NotFound() {
    var productOrder = new ProductOrder();
    productOrder.setProductOrderItems(new ArrayList<>());

    assertThrows(
        IllegalArgumentException.class,
        () -> ProductOrderUtil.findProductOrderItemById(productOrder, "nonExistentItem"));
  }

  private static final ProductOrderCreate PRODUCT_ORDER_CREATE =
      fileToObject("tmf622/util/product_order_create_valid.json", ProductOrderCreate.class);

  private static final ProductOrder PRODUCT_ORDER =
      fileToObject("tmf622/util/product_order.json", ProductOrder.class);

  @Test
  void testValidateOrder_withValidData_validatesSuccessfully() {
    assertDoesNotThrow(() -> ProductOrderUtil.validateOrder(PRODUCT_ORDER_CREATE));
  }

  @Test
  void testFindProductCharacteristicsBySpecificationId_withValidInput_returnsValidResult() {
    var list =
        findProductCharacteristicsBySpecificationId(
            PRODUCT_ORDER, "UCDigitalLineLicenseSpecification");

    assertNotNull(list);
    assertEquals(15, list.size());
  }

  @Test
  void testFindProductCharacteristicsBySpecificationId_withNonexistentId_returnsEmptyList() {
    var list = findProductCharacteristicsBySpecificationId(PRODUCT_ORDER, "NonExistentId");

    assertNotNull(list);
    assertEquals(0, list.size());
    assertSame(Collections.emptyList(), list);
  }

  @Test
  void testIsBundle_withBundleItem_returnsTrue() {
    assertTrue(isBundle(findProductOrderItemById(PRODUCT_ORDER_CREATE, "100")));
  }

  @Test
  void testIsBundle_withNonBundleItem_returnsFalse() {
    assertFalse(isBundle(findProductOrderItemById(PRODUCT_ORDER_CREATE, "100-1")));
    assertFalse(isBundle(findProductOrderItemById(PRODUCT_ORDER_CREATE, "100-2")));
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
    assertEquals("Very complex product tree not supported.", e.getMessage());
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
    assertEquals("No end node exists for the Product Order Flow.", e.getMessage());
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

  private ProductOrder getComplexOrder() {
    var order = order("1");
    order.setProductOrderItems(safeMutable(List.of(item("IndependentStartItem"))));
    for (int i = 0; i < 1000; i++) {
      var item = item("" + i);
      var list = new ArrayList<OrderItemRelationship>();
      for (int j = 0; j < 1000; j++) {
        var rel = new OrderItemRelationship();
        rel.setId("" + j);
        list.add(rel);
      }
      item.setProductOrderItemRelationships(list);
      order.getProductOrderItems().add(item);
    }
    return order;
  }

  private ProductOrder order(String id, ProductOrderItem... items) {
    var order = new ProductOrder();
    order.setId(id);
    order.setProductOrderItems(Arrays.stream(items).toList());
    return order;
  }

  private ProductOrderItem item(String id, String... dependentIds) {
    var item = new ProductOrderItem();
    item.setId(id);
    item.setProductOrderItemRelationships(
        Arrays.stream(dependentIds).map(dependentId -> {
          var rel = new OrderItemRelationship();
          rel.setId(dependentId);
          return rel;
        }).toList());
    return item;
  }
}
