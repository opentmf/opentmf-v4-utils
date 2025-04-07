package org.opentmf.v4.common.util;

import static org.junit.jupiter.api.Assertions.*;

import org.opentmf.v4.common.model.RelatedParty;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.opentmf.v4.common.util.RelatedPartyUtil;

/**
 * @author Gokhan Demir
 */
class RelatedPartyUtilTests {

  @Test
  void findRelatedPartyByRoleReturnsCorrectParty() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("operator");

    RelatedParty result =
        RelatedPartyUtil.findRelatedPartyByRole(Arrays.asList(party1, party2), "customer");

    assertEquals(party1, result);
  }

  @Test
  void findRelatedPartyByRoleThrowsExceptionWhenNoMatch() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("operator");
    var list = Arrays.asList(party1, party2);

    assertThrows(
        IllegalArgumentException.class,
        () -> RelatedPartyUtil.findRelatedPartyByRole(list, "supplier"));
  }

  @Test
  void findUniqueRelatedPartyByRoleReturnsCorrectParty() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("operator");

    RelatedParty result =
        RelatedPartyUtil.findUniqueRelatedPartyByRole(Arrays.asList(party1, party2), "customer");

    assertEquals(party1, result);
  }

  @Test
  void findUniqueRelatedPartyByRoleThrowsExceptionWhenMultipleMatches() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("customer");
    var list = Arrays.asList(party1, party2);

    assertThrows(IllegalArgumentException.class, () ->
        RelatedPartyUtil.findUniqueRelatedPartyByRole(list, "customer"));
  }

  @Test
  void findUniqueRelatedPartyByRoleThrowsExceptionWhenEmptyList() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("operator");
    var list = Arrays.asList(party1, party2);

    assertThrows(
        IllegalArgumentException.class,
        () ->
            RelatedPartyUtil.findUniqueRelatedPartyByRole(
                list, "nonExistentRole"));
  }

  @Test
  void findCustomerPartyReturnsCorrectParty() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    party1.setAtReferredType("Customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("operator");
    party2.setAtReferredType("Organization");

    RelatedParty result = RelatedPartyUtil.findCustomerParty(Arrays.asList(party1, party2));

    assertEquals(party1, result);
  }

  @Test
  void findCustomerPartyThrowsExceptionWhenNoMatch() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("operator");
    party1.setAtReferredType("Organization");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("supplier");
    party2.setAtReferredType("Organization");
    var list = Arrays.asList(party1, party2);

    assertThrows(IllegalArgumentException.class, () ->
        RelatedPartyUtil.findCustomerParty(list));
  }

  @Test
  void findOperatorPartyReturnsCorrectParty() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    party1.setAtReferredType("Customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("operator");
    party2.setAtReferredType("Organization");

    RelatedParty result = RelatedPartyUtil.findOperatorParty(Arrays.asList(party1, party2));

    assertEquals(party2, result);
  }

  @Test
  void findOperatorPartyThrowsExceptionWhenNoMatch() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    party1.setAtReferredType("Customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("supplier");
    party2.setAtReferredType("Organization");
    var list = Arrays.asList(party1, party2);

    assertThrows(IllegalArgumentException.class, () ->
        RelatedPartyUtil.findOperatorParty(list));
  }

  @Test
  void findSupplierPartyReturnsCorrectParty() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    party1.setAtReferredType("Customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("supplier");
    party2.setAtReferredType("Organization");

    RelatedParty result = RelatedPartyUtil.findSupplierParty(Arrays.asList(party1, party2));

    assertEquals(party2, result);
  }

  @Test
  void findSupplierPartyThrowsExceptionWhenNoMatch() {
    RelatedParty party1 = new RelatedParty();
    party1.setRole("customer");
    party1.setAtReferredType("Customer");
    RelatedParty party2 = new RelatedParty();
    party2.setRole("operator");
    party2.setAtReferredType("Organization");
    var list = Arrays.asList(party1, party2);

    assertThrows(
        IllegalArgumentException.class,
        () -> RelatedPartyUtil.findSupplierParty(list));
  }
}
