package org.opentmf.v4.common.util;

import org.opentmf.common.model.IRelatedParty;
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
  public static IRelatedParty findRelatedPartyByRole(
      Collection<? extends IRelatedParty> relatedParties, String role) {
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
  public static Optional<IRelatedParty> findOptionalRelatedPartyByRole(
      Collection<? extends IRelatedParty> relatedParties, String role) {
    for (IRelatedParty relatedParty : relatedParties) {
      if (role.equalsIgnoreCase(relatedParty.getRole())) {
        return Optional.of(relatedParty);
      }
    }
    return Optional.empty();
  }

  private static Collection<IRelatedParty> findAllRelatedPartiesByRole(
      Collection<? extends IRelatedParty> relatedPartyList, String role) {
    Collection<IRelatedParty> list = new ArrayList<>();
    for (IRelatedParty relatedParty : relatedPartyList) {
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
  public static IRelatedParty findUniqueRelatedPartyByRole(
      Collection<? extends IRelatedParty> relatedParties, String role) {
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

  private static IRelatedParty findRelatedPartyByReferredTypeAndRole(
      Collection<? extends IRelatedParty> relatedParties, String referredType, String role) {
    return relatedParties.stream()
        .filter(relatedParty -> referredType.equals(relatedParty.getAtReferredType()))
        .filter(relatedParty -> role.equalsIgnoreCase(relatedParty.getRole()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("RelatedParty of referredType = "
            + referredType + " and role = " + role + NOT_FOUND));
  }

  /**
   * Finds the related party with referred type "Customer" and role "customer".
   *
   * @param relatedParties The collection of related parties.
   * @return The customer party.
   * @throws IllegalArgumentException If no matching related party is found.
   */
  public static IRelatedParty findCustomerParty(
      Collection<? extends IRelatedParty> relatedParties) {
    return findRelatedPartyByReferredTypeAndRole(relatedParties, CUSTOMER_TYPE, CUSTOMER_ROLE);
  }

  /**
   * Finds the related party with referred type "Organization" and role "operator".
   *
   * @param relatedParties The collection of related parties.
   * @return The operator party.
   * @throws IllegalArgumentException If no matching related party is found.
   */
  public static IRelatedParty findOperatorParty(
      Collection<? extends IRelatedParty> relatedParties) {
    return findRelatedPartyByReferredTypeAndRole(relatedParties, ORGANIZATION_TYPE, OPERATOR_ROLE);
  }

  /**
   * Finds the related party with referred type "Organization" and role "supplier".
   *
   * @param relatedParties The collection of related parties.
   * @return The supplier party.
   * @throws IllegalArgumentException If no matching related party is found.
   */
  public static IRelatedParty findSupplierParty(
      Collection<? extends IRelatedParty> relatedParties) {
    return findRelatedPartyByReferredTypeAndRole(relatedParties, ORGANIZATION_TYPE, SUPPLIER_ROLE);
  }
}
