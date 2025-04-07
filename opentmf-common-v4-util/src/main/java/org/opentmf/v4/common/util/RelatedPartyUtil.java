package org.opentmf.v4.common.util;

import org.opentmf.v4.common.model.RelatedParty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.Generated;

/**
 * Useful utility methods for working with related parties.
 *
 * @author Gokhan Demir
 */
public class RelatedPartyUtil {

  private static final String CUSTOMER_TYPE = "Customer";
  private static final String ORGANIZATION_TYPE = "Organization";

  private static final String CUSTOMER_ROLE = "customer";
  private static final String OPERATOR_ROLE = "operator";
  private static final String SUPPLIER_ROLE = "supplier";
  private static final String NOT_FOUND = " not found";

  @Generated
  private RelatedPartyUtil() {
    throw new UnsupportedOperationException(
        "RelatedPartyUtil is a utility class only with static methods, "
            + "therefore cannot be instantiated.");
  }

  /**
   * Finds first related party that matches the requester role in the given collection.
   *
   * @param relatedParties the related party collection.
   * @param role the requested role of the relatedParty.
   * @return The first found relatedParty that matches the requested role in the related party
   *     collection.
   * @throws IllegalArgumentException If no relatedParty with the requested role exists in the
   *     collection.
   */
  public static RelatedParty findRelatedPartyByRole(
      Collection<RelatedParty> relatedParties, String role) {
    return findOptionalRelatedPartyByRole(relatedParties, role)
        .orElseThrow(
            () -> new IllegalArgumentException("RelatedParty with role = " + role + NOT_FOUND));
  }

  /**
   * Finds first related party that matches the requester role in the given collection.
   *
   * @param relatedParties the related party collection.
   * @param role the requested role of the relatedParty.
   * @return The first found relatedParty that matches the requested role in the related party
   *     collection or optional empty.
   */
  public static Optional<RelatedParty> findOptionalRelatedPartyByRole(
      Collection<RelatedParty> relatedParties, String role) {
    return relatedParties.stream()
        .filter(relatedParty -> role.equalsIgnoreCase(relatedParty.getRole()))
        .findFirst();
  }

  private static Collection<RelatedParty> findAllRelatedPartiesByRole(
      Collection<RelatedParty> relatedPartyList, String role) {
    Collection<RelatedParty> list = new ArrayList<>();
    for (RelatedParty relatedParty : relatedPartyList) {
      if (role.equalsIgnoreCase(relatedParty.getRole())) {
        list.add(relatedParty);
      }
    }
    return list;
  }

  /**
   * Finds and returns the unique related party by the specified role.
   *
   * @param relatedParties the relatedParty collection.
   * @param role The requested role to detect uniqueness.
   * @return the unique related party by the specified role.
   */
  public static RelatedParty findUniqueRelatedPartyByRole(
      Collection<RelatedParty> relatedParties, String role) {
    var allPartiesWithRole = findAllRelatedPartiesByRole(relatedParties, role);
    if (allPartiesWithRole.isEmpty()) {
      throw new IllegalArgumentException("RelatedParty with role=" + role + NOT_FOUND);
    } else if (allPartiesWithRole.size() > 1) {
      throw new IllegalArgumentException(
          "More than one RelatedParty ("
              + allPartiesWithRole.size()
              + ") exists with role="
              + role
              + ".");
    }
    return allPartiesWithRole.iterator().next();
  }

  private static RelatedParty findRelatedPartyByReferredTypeAndRole(
      Collection<RelatedParty> relatedParties, String referredType, String role) {
    return relatedParties.stream()
        .filter(relatedParty -> referredType.equals(relatedParty.getAtReferredType()))
        .filter(relatedParty -> role.equalsIgnoreCase(relatedParty.getRole()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("RelatedParty of referredType = " 
            + referredType + " and role = " + role + NOT_FOUND));
  }

  public static RelatedParty findCustomerParty(Collection<RelatedParty> relatedParties) {
    return findRelatedPartyByReferredTypeAndRole(relatedParties, CUSTOMER_TYPE, CUSTOMER_ROLE);
  }

  public static RelatedParty findOperatorParty(Collection<RelatedParty> relatedParties) {
    return findRelatedPartyByReferredTypeAndRole(relatedParties, ORGANIZATION_TYPE, OPERATOR_ROLE);
  }

  public static RelatedParty findSupplierParty(Collection<RelatedParty> relatedParties) {
    return findRelatedPartyByReferredTypeAndRole(relatedParties, ORGANIZATION_TYPE, SUPPLIER_ROLE);
  }
}
