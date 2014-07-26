	private String revisions;
	public DiffCommand revisions(String revisions) {
		this.revisions = revisions;
		Preconditions.checkNotNull(revisions, "revisions has to be specified.");
		cmd.addArgs("diff", revisions, "--full-index", "--no-color", "--find-renames", 
				"--find-copies", "--src-prefix=#gitplex_old/", "--dst-prefix=#gitplex_new/");
					line = line.substring("diff --git #gitplex_old/".length());
					changeBuilder.oldPath = StringUtils.substringBefore(line, " #gitplex_new/");
					changeBuilder.newPath = StringUtils.substringAfter(line, " #gitplex_new/");