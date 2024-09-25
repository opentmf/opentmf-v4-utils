package com.pia.tmf.v4.tmf641.util;

import static com.pia.commons.util.JacksonUtil.fileToObject;
import static com.pia.tmf.v4.tmf641.util.ServiceOrderUtil.findServiceOrderItemById;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pia.tmf.v4.tmf641.model.ServiceOrder;
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
}
