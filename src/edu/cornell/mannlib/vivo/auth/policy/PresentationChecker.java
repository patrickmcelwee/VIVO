/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;

/**
 * Does the requested action involve a change to a Presentation that the
 * self-editor is authorized to modify?
 */
public class PresentationChecker extends RelationshipChecker {
	private static final String NS_CORE = "http://vivoweb.org/ontology/core#";
	private static final String URI_PRESENTATION_TYPE = NS_CORE
			+ "Presentation";
	private static final String URI_RELATED_ROLE_PROPERTY = NS_CORE
			+ "relatedRole";
	private static final String URI_PRESENTER_ROLE_OF_PROPERTY = NS_CORE
			+ "presenterRoleOf";

	private final String[] resourceUris;

	public PresentationChecker(AbstractPropertyStatementAction action) {
		super(action.getOntModel());
		this.resourceUris = action.getResourceUris();
	}

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to a Presentation, and if the self-editor:
	 * 
	 * 1) is a Presenter of that Presentation
	 */
	public PolicyDecision isAuthorized(List<String> userUris) {
		for (String resourceUri : resourceUris) {
			if (isPresentation(resourceUri)) {
				if (anyUrisInCommon(userUris, getUrisOfPresenters(resourceUri))) {
					return authorizedPresenter(resourceUri);
				}
			}
		}
		return null;
	}

	private boolean isPresentation(String resourceUri) {
		return isResourceOfType(resourceUri, URI_PRESENTATION_TYPE);
	}

	private List<String> getUrisOfPresenters(String resourceUri) {
		return getObjectsOfLinkedProperty(resourceUri,
				URI_RELATED_ROLE_PROPERTY, URI_PRESENTER_ROLE_OF_PROPERTY);
	}

	private PolicyDecision authorizedPresenter(String resourceUri) {
		return authorizedDecision("User is a Presenter of " + resourceUri);
	}

}
