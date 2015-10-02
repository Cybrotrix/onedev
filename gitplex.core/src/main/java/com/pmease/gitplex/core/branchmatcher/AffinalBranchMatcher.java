package com.pmease.gitplex.core.branchmatcher;

import java.io.Serializable;

import com.pmease.commons.util.trimmable.Trimmable;
import com.pmease.commons.wicket.editable.annotation.Editable;
import com.pmease.gitplex.core.model.Repository;

@Editable
public interface AffinalBranchMatcher extends Serializable, Trimmable {
	boolean matches(Repository repository, String branch);
}
