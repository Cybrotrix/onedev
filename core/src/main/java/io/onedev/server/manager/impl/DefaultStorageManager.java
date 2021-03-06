package io.onedev.server.manager.impl;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.onedev.launcher.loader.Listen;
import io.onedev.server.event.entity.EntityRemoved;
import io.onedev.server.event.system.SystemStarting;
import io.onedev.server.manager.SettingManager;
import io.onedev.server.manager.StorageManager;
import io.onedev.server.model.Project;
import io.onedev.server.model.User;
import io.onedev.server.persistence.annotation.Transactional;
import io.onedev.server.persistence.dao.Dao;
import io.onedev.utils.FileUtils;

@Singleton
public class DefaultStorageManager implements StorageManager {

	private static final Logger logger = LoggerFactory.getLogger(DefaultStorageManager.class);
	
	private static final String OLD_DELETE_MARK1 = "to_be_deleted_when_gitplex_is_restarted";
	
	private static final String OLD_DELETE_MARK2 = "to_be_deleted_when_turbodev_is_restarted";
	
	private static final String DELETE_MARK = "to_be_deleted_when_onedev_is_restarted";
	
	private final Dao dao;
	
    private final SettingManager configManager;
    
    @Inject
    public DefaultStorageManager(Dao dao, SettingManager configManager) {
    	this.dao = dao;
        this.configManager = configManager;
    }

    @Override
    public File getStorageDir() {
    	File storageDir = new File(configManager.getSystemSetting().getStoragePath());
    	FileUtils.createDir(storageDir);
    	return storageDir;
    }
    
    private File getProjectsDir() {
    	File projectsDir = new File(getStorageDir(), "projects");
    	FileUtils.createDir(projectsDir);
    	return projectsDir;
    }
    
    private File getProjectDir(Long projectId) {
        File projectDir = new File(getProjectsDir(), String.valueOf(projectId));
        FileUtils.createDir(projectDir);
        return projectDir;
    }
    
    @Override
    public File getProjectGitDir(Long projectId) {
        File gitDir = new File(getProjectDir(projectId), "git");
        FileUtils.createDir(gitDir);
        return gitDir;
    }

	@Override
	public File getProjectInfoDir(Long projectId) {
        File infoDir = new File(getProjectDir(projectId), "info");
        FileUtils.createDir(infoDir);
        return infoDir;
	}

	@Override
	public File getProjectIndexDir(Long projectId) {
        File indexDir = new File(getProjectDir(projectId), "index");
        FileUtils.createDir(indexDir);
        return indexDir;
	}

	@Override
	public File getProjectAttachmentDir(Long projectId) {
        File attachmentDir = new File(getProjectDir(projectId), "attachment");
        FileUtils.createDir(attachmentDir);
        return attachmentDir;
	}

	@Listen
	public void on(SystemStarting event) {
        for (File projectDir: getProjectsDir().listFiles()) {
        	if (new File(projectDir, OLD_DELETE_MARK1).exists()
        			|| new File(projectDir, OLD_DELETE_MARK2).exists()
        			|| new File(projectDir, DELETE_MARK).exists()) { 
        		logger.info("Deleting directory marked for deletion: " + projectDir);
        		FileUtils.deleteDir(projectDir);
        	}
        }
        for (File userDir: getUsersDir().listFiles()) {
        	if (new File(userDir, OLD_DELETE_MARK1).exists()
        			|| new File(userDir, OLD_DELETE_MARK2).exists()
        			|| new File(userDir, DELETE_MARK).exists()) { 
        		logger.info("Deleting directory marked for deletion: " + userDir);
        		FileUtils.deleteDir(userDir);
        	}
        }
	}

	@Transactional
	@Listen
	public void on(EntityRemoved event) {
		Long id = event.getEntity().getId();
		
		File projectDir;
		if (event.getEntity() instanceof Project)
			projectDir = getProjectDir(id);
		else
			projectDir = null;
		
		File userDir;
		if (event.getEntity() instanceof User)
			userDir = getUserInfoDir(id);
		else
			userDir = null;
		
		dao.doAfterCommit(new Runnable() {

			@Override
			public void run() {
				try {
					if (projectDir != null) 
						new File(projectDir, DELETE_MARK).createNewFile();
					if (userDir != null)
						new File(userDir, DELETE_MARK).createNewFile();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
		});
	}

    private File getUsersDir() {
    	File usersDir = new File(getStorageDir(), "users");
    	FileUtils.createDir(usersDir);
    	return usersDir;
    }
    
	@Override
    public File getUserInfoDir(Long userId) {
        File userDir = new File(getUsersDir(), String.valueOf(userId));
        FileUtils.createDir(userDir);
        return userDir;
    }
    
}
